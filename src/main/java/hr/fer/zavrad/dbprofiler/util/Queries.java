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

    private static final int SELECT_LIMITLESS_THRESHOLD = 500_000;
    private static final int SELECT_RANDOM_LIMIT = 50_000;

    public static final String POSTGRE = "PostgreSQL";
    public static final String MYSQL = "MySQL";
    public static final String SQL_SERVER = "Microsoft SQL Server";

    private Queries() {
    }

    public static Optional<String> getSelectColumnQuery(Connection connection, Table table, TableColumn column) {

        if(Integer.compare(table.getRowCount(), SELECT_LIMITLESS_THRESHOLD) <= 0) {
            return Optional.of(String.format("SELECT %s FROM %s.%s", column.getColumnName(), table.getSchemaName(), table.getName()));
        }

        String databaseType = null;
        try {
            databaseType = connection.getMetaData().getDatabaseProductName();

            switch(databaseType) {
                case POSTGRE:
                    return Optional.of(
                            String.format("SELECT %s FROM %s.%s ORDER BY RANDOM() LIMIT %d", column.getColumnName(), table.getSchemaName(), table.getName(), SELECT_RANDOM_LIMIT));

                case MYSQL:
                    return Optional.of(
                            String.format("SELECT %s FROM %s.%s ORDER BY RAND() LIMIT %d", column.getColumnName(), table.getSchemaName(), table.getName(), SELECT_RANDOM_LIMIT));

                case SQL_SERVER:
                    return Optional.of(
                            String.format("SELECT TOP %d %s FROM %s.%s ORDER BY newid()",
                                    SELECT_RANDOM_LIMIT, column.getColumnName(), table.getSchemaName(), table.getName()));

                default:
                    return Optional.empty();
            }
        } catch (SQLException e) {
            return Optional.empty();
        }
    }
}
