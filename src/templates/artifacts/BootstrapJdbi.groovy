import org.skife.jdbi.v2.DBI

class BootstrapJdbi {
    def init = { String dataSourceName, DBI dbi ->
    }

    def destroy = { String dataSourceName, DBI dbi ->
    }
}
