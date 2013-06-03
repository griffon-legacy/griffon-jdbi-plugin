/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.plugins.jdbi

import griffon.plugins.datasource.DataSourceConnector
import griffon.plugins.datasource.DataSourceHolder
import org.skife.jdbi.v2.DBI
import griffon.util.ConfigUtils
import griffon.core.GriffonApplication

import javax.sql.DataSource

/**
 * @author Andres Almiray
 */
@Singleton
final class JdbiConnector {
    private static final String DEFAULT = 'default'
    private bootstrap

    DBI connect(GriffonApplication app, String dataSourceName = DEFAULT) {
        if (JdbiHolder.instance.isJdbiAvailable(dataSourceName)) {
            return JdbiHolder.instance.getJdbi(dataSourceName)
        }

        ConfigObject config = DataSourceConnector.instance.createConfig(app)
        DataSource dataSource = DataSourceConnector.instance.connect(app, config, dataSourceName)

        app.event('JdbiConnectStart', [dataSourceName, dataSource])
        DBI dbi = new DBI(dataSource)
        JdbiHolder.instance.setJdbi(dataSourceName, dbi)
        bootstrap = app.class.classLoader.loadClass('BootstrapJdbi').newInstance()
        bootstrap.metaClass.app = app
        resolveJdbiProvider(app).withJdbi(dataSourceName) { dsName, jdbi -> bootstrap.init(dsName, jdbi) }
        app.event('JdbiConnectEnd', [dataSourceName, dataSource])
        dbi
    }

    void disconnect(GriffonApplication app, String dataSourceName = DEFAULT) {
        if (!JdbiHolder.instance.isJdbiAvailable(dataSourceName)) return

        DBI dbi = JdbiHolder.instance.getJdbi(dataSourceName)
        app.event('JdbiDisconnectStart', [dataSourceName])
        resolveJdbiProvider(app).withJdbi(dataSourceName) { dsName, jdbi -> bootstrap.destroy(dsName, jdbi) }
        JdbiHolder.instance.disconnectJdbi(dataSourceName)
        app.event('JdbiDisconnectEnd', [dataSourceName])
        ConfigObject config = DataSourceConnector.instance.createConfig(app)
        DataSourceConnector.instance.disconnect(app, config, dataSourceName)
    }

    JdbiProvider resolveJdbiProvider(GriffonApplication app) {
        def jdbiProvider = app.config.jdbiProvider
        if (jdbiProvider instanceof Class) {
            jdbiProvider = jdbiProvider.newInstance()
            app.config.jdbiProvider = jdbiProvider
        } else if (!jdbiProvider) {
            jdbiProvider = DefaultJdbiProvider.instance
            app.config.jdbiProvider = jdbiProvider
        }
        jdbiProvider
    }
}
