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

import org.skife.jdbi.v2.DBI;

/**
 * @author Andres Almiray
 */
public class DefaultJdbiProvider extends AbstractJdbiProvider {
    private static final DefaultJdbiProvider INSTANCE;

    static {
        INSTANCE = new DefaultJdbiProvider();
    }

    public static DefaultJdbiProvider getInstance() {
        return INSTANCE;
    }

    private DefaultJdbiProvider() {}

    @Override
    protected DBI getJdbi(String dataSourceName) {
        return JdbiHolder.getInstance().fetchJdbi(dataSourceName);
    }
}
