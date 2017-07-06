package hr.fer.zavrad.dbprofiler.util;

import hr.fer.zavrad.dbprofiler.model.Table;
import hr.fer.zavrad.dbprofiler.model.TableColumn;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ProfilerResultSet {

    public static final int SAMPLING_THRESHOLD = 300_000;
    public static final int SAMPLE_SIZE = 10_000;

    private final Optional<ResultSet> resultSet;
    private final boolean sample;

    private int currentSampleSize;
    private String samplingQueryTemplate;
    private int primaryKeyMinimalValue;
    private int primaryKeyMaximalValue;

    private Connection connection;

    public static final String POSTGRE = "PostgreSQL";
    public static final String MYSQL = "MySQL";
    public static final String SQL_SERVER = "Microsoft SQL Server";

    public ProfilerResultSet(Connection connection, Table table, TableColumn column) throws SQLException {

        if(table.getRowCount() < SAMPLING_THRESHOLD) {
            sample = false;

            String query = String.format("SELECT %s FROM %s.%s", column.getColumnName(), table.getSchemaName(),
                                                                 table.getName());
            resultSet = Optional.of(connection.createStatement().executeQuery(query));
        } else {

            List<String> primaryKeys = Connections.getPrimaryKeys(connection, table);

            if(primaryKeys.size() != 1) {
                String query = getSamplingQuery(connection, table, column).get();

                resultSet = Optional.of(connection.createStatement().executeQuery(query));
                sample = false;
            } else {
                String primaryKey = primaryKeys.get(0);
                this.samplingQueryTemplate = String.format("SELECT %s FROM %s.%s WHERE %s = ",
                                                      column.getColumnName(),
                                                      table.getSchemaName(),
                                                      table.getName(),
                                                      primaryKey) + "%d";

                String minimumPkValueQuery = String.format("SELECT MIN(%s) FROM %s.%s", primaryKey,
                        table.getSchemaName(), table.getName());

                ResultSet minimumPkValueResultSet = connection.createStatement().executeQuery(minimumPkValueQuery);
                if(minimumPkValueResultSet.next()) {
                    this.primaryKeyMinimalValue = minimumPkValueResultSet.getInt(1);
                }

                String maximumPkValueQuery = String.format("SELECT MAX(%s) FROM %s.%s", primaryKey,
                        table.getSchemaName(), table.getName());

                ResultSet maximumPkValueResultSet = connection.createStatement().executeQuery(maximumPkValueQuery);
                if(maximumPkValueResultSet.next()) {
                    this.primaryKeyMaximalValue = (int)maximumPkValueResultSet.getDouble(1);
                }

                this.connection = connection;
                resultSet = Optional.empty();
                sample = true;
            }
        }
    }

    private Optional<String> getSamplingQuery(Connection connection, Table table, TableColumn column) {
        String databaseType = null;

        try {
            databaseType = connection.getMetaData().getDatabaseProductName();

            switch(databaseType) {
                case POSTGRE:
                case MYSQL:
                    return Optional.of(
                            String.format("SELECT %s FROM %s.%s LIMIT %d", column.getColumnName(),
                                    table.getSchemaName(), table.getName(), SAMPLE_SIZE));

                case SQL_SERVER:
                    return Optional.of(
                            String.format("SELECT TOP %d %s FROM %s.%s",
                                    SAMPLE_SIZE, column.getColumnName(), table.getSchemaName(), table.getName()));
            }
        } catch (SQLException e) {}

        return Optional.empty();
    }

    private ResultSet getSamplingResultSet() throws SQLException {
        for(;;) {
            String query = String.format(samplingQueryTemplate, getRandomPrimaryKey());
            ResultSet resultSet = connection.createStatement().executeQuery(query);

            if(resultSet.next()) {
                return resultSet;
            }
            resultSet.close();
        }
    }

    private int getRandomPrimaryKey() {
        return primaryKeyMinimalValue + (int)(Math.random() * (primaryKeyMaximalValue - primaryKeyMinimalValue + 1));
    }

    public boolean next() throws SQLException {
        if(sample) {
            return currentSampleSize < SAMPLE_SIZE;
        }

        return resultSet.get().next();
    }

    public String getString(String columnLabel) throws SQLException {
        if(sample) {
            ++currentSampleSize;
            ResultSet resultSet = this.getSamplingResultSet();
            String result = resultSet.getString(columnLabel);
            resultSet.close();
            return result;
        }

        return resultSet.get().getString(columnLabel);
    }

    public Object getObject(String columnLabel) throws SQLException {
        if (sample) {
            ++currentSampleSize;
            ResultSet resultSet = this.getSamplingResultSet();
            Object result = resultSet.getObject(columnLabel);
            resultSet.close();
            return result;
        }

        return resultSet.get().getObject(columnLabel);
    }

    public double getDouble(String columnLabel) throws SQLException {
        if(sample) {
            ++currentSampleSize;
            ResultSet resultSet = this.getSamplingResultSet();
            double result = resultSet.getDouble(columnLabel);
            resultSet.close();
            return result;
        }

        return resultSet.get().getDouble(columnLabel);
    }

    public Date getDate(String columnLabel) throws SQLException {
        if(sample) {
            ++currentSampleSize;
            ResultSet resultSet = this.getSamplingResultSet();
            Date result = resultSet.getDate(columnLabel);
            resultSet.close();
            return result;
        }

        return resultSet.get().getDate(columnLabel);
    }

    public Time getTime(String columnLabel) throws SQLException {
        if(sample) {
            ++currentSampleSize;
            ResultSet resultSet = this.getSamplingResultSet();
            Time result = resultSet.getTime(columnLabel);
            resultSet.close();
            return result;
        }

        return resultSet.get().getTime(columnLabel);
    }

    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        if(sample) {
            ++currentSampleSize;
            ResultSet resultSet = this.getSamplingResultSet();
            Timestamp result = resultSet.getTimestamp(columnLabel);
            resultSet.close();
            return result;
        }

        return resultSet.get().getTimestamp(columnLabel);
    }
}
