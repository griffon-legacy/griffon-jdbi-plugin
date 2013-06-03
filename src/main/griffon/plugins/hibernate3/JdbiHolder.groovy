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

import org.skife.jdbi.v2.DBI
import griffon.core.GriffonApplication
import griffon.util.ApplicationHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static griffon.util.GriffonNameUtils.isBlank

/**
 * @author Andres Almiray
 */
class JdbiHolder {
    private static final String DEFAULT = 'default'
    private static final Logger LOG = LoggerFactory.getLogger(JdbiHolder)
    private final Map<String, DBI> dbis = [:]
    private static final Object[] LOCK = new Object[0]

    private static final JdbiHolder INSTANCE

    static {
        INSTANCE = new JdbiHolder()
    }

    static JdbiHolder getInstance() {
        INSTANCE
    }

    private JdbiHolder() {}

    String[] getJdbiNames() {
        List<String> dataSourceNames = new ArrayList().addAll(dbis.keySet())
        dataSourceNames.toArray(new String[dataSourceNames.size()])
    }

    DBI getJdbi(String dataSourceName = DEFAULT) {
        if (isBlank(dataSourceName)) dataSourceName = DEFAULT
        retrieveJdbi(dataSourceName)
    }

    void setJdbi(String dataSourceName = DEFAULT, DBI dbi) {
        if (isBlank(dataSourceName)) dataSourceName = DEFAULT
        storeJdbi(dataSourceName, dbi)
    }

    boolean isJdbiAvailable(String dataSourceName) {
        if (isBlank(dataSourceName)) dataSourceName = DEFAULT
        retrieveJdbi(dataSourceName) != null
    }

    void disconnectJdbi(String dataSourceName) {
        if (isBlank(dataSourceName)) dataSourceName = DEFAULT
        storeJdbi(dataSourceName, null)
    }

    DBI fetchJdbi(String dataSourceName) {
        if (isBlank(dataSourceName)) dataSourceName = DEFAULT
        DBI dbi = retrieveJdbi(dataSourceName)
        if (dbi == null) {
            GriffonApplication app = ApplicationHolder.application
            dbi = JdbiConnector.instance.connect(app, dataSourceName)
        }

        if (dbi == null) {
            throw new IllegalArgumentException("No such Jdbi configuration for name $dataSourceName")
        }
        dbi
    }

    private DBI retrieveJdbi(String dataSourceName) {
        synchronized (LOCK) {
            dbis[dataSourceName]
        }
    }

    private void storeJdbi(String dataSourceName, DBI dbi) {
        synchronized (LOCK) {
            dbis[dataSourceName] = dbi
        }
    }
}
