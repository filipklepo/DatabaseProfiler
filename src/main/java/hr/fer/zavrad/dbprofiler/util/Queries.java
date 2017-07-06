package hr.fer.zavrad.dbprofiler.util;

import hr.fer.zavrad.dbprofiler.model.Table;
import hr.fer.zavrad.dbprofiler.model.TableColumn;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Utility class which handles retrieval of appropriate queries for the profiler.
 *
 * @author filip
 */
public final class Queries {

    public static final String POSTGRE = "PostgreSQL";
    public static final String MYSQL = "MySQL";
    public static final String SQL_SERVER = "Microsoft SQL Server";

    private Queries() {
    }

    public static Optional<String> getSelectWithLimitQuery(Connection connection, Table table, int rowCountLimit) {
        String databaseType = null;

        try {
            databaseType = connection.getMetaData().getDatabaseProductName();

            switch(databaseType) {
                case POSTGRE:
                    return Optional.of(
                            String.format("SELECT * FROM %s.%s LIMIT %d",
                                    table.getSchemaName(), table.getName(), rowCountLimit));

                case MYSQL:
                    return Optional.of(
                            String.format("SELECT * FROM %s.%s LIMIT %d",
                                    table.getSchemaName(), table.getName(), rowCountLimit));

                case SQL_SERVER:
                    return Optional.of(
                            String.format("SELECT TOP %d * FROM %s.%s",
                                    rowCountLimit, table.getSchemaName(), table.getName()));
            }
        } catch (SQLException e) {}

        return Optional.empty();
    }
}
