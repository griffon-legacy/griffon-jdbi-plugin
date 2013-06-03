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

/**
 * @author Andres Almiray
 */
public class JdbiContributionAdapter implements JdbiContributionHandler {
    private static final String DEFAULT = "default";

    private JdbiProvider provider = DefaultJdbiProvider.getInstance();

    public void setJdbiProvider(JdbiProvider provider) {
        this.provider = provider != null ? provider : DefaultJdbiProvider.getInstance();
    }

    public JdbiProvider getJdbiProvider() {
        return provider;
    }

    public <R> R withJdbi(Closure<R> closure) {
        return withJdbi(DEFAULT, closure);
    }

    public <R> R withJdbi(String dataSourceName, Closure<R> closure) {
        return provider.withJdbi(dataSourceName, closure);
    }

    public <R> R withJdbi(CallableWithArgs<R> callable) {
        return withJdbi(DEFAULT, callable);
    }

    public <R> R withJdbi(String dataSourceName, CallableWithArgs<R> callable) {
        return provider.withJdbi(dataSourceName, callable);
    }
}