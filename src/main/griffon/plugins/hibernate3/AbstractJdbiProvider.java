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

package griffon.plugins.jdbi;

import griffon.util.CallableWithArgs;
import groovy.lang.Closure;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 */
public abstract class AbstractJdbiProvider implements JdbiProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractJdbiProvider.class);
    private static final String DEFAULT = "default";

    public <R> R withJdbi(Closure<R> closure) {
        return withJdbi(DEFAULT, closure);
    }

    public <R> R withJdbi(String dataSourceName, Closure<R> closure) {
        if (isBlank(dataSourceName)) dataSourceName = DEFAULT;
        if (closure != null) {
            DBI dbi = getJdbi(dataSourceName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Executing statements on datasource '" + dataSourceName + "'");
            }
            return closure.call(dataSourceName, dbi);
        }
        return null;
    }

    public <R> R withJdbi(CallableWithArgs<R> callable) {
        return withJdbi(DEFAULT, callable);
    }

    public <R> R withJdbi(String dataSourceName, CallableWithArgs<R> callable) {
        if (isBlank(dataSourceName)) dataSourceName = DEFAULT;
        if (callable != null) {
            DBI dbi = getJdbi(dataSourceName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Executing statements on datasource '" + dataSourceName + "'");
            }
            return callable.call(new Object[]{dataSourceName, dbi});
        }
        return null;
    }

    protected abstract DBI getJdbi(String dataSourceName);
}