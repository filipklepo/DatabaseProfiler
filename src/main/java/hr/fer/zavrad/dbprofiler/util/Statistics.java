package hr.fer.zavrad.dbprofiler.util;

import hr.fer.zavrad.dbprofiler.model.ColumnStatistics;
import hr.fer.zavrad.dbprofiler.model.NumericColumnStatistics;
import hr.fer.zavrad.dbprofiler.model.TableColumnType;
import hr.fer.zavrad.dbprofiler.model.TextualColumnStatistics;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An utility class which is used for generating various statistics from a database, via a {@code Connection} instance.
 * <br>It uses a primitive sampling technique by taking first {@code QUERY_ROWS_LIMIT} rows of the table.
 *
 * @author filip
 */
public final class Statistics {

    private static final int QUERY_ROWS_LIMIT = 100_000;
    private static final String GENERIC_QUERY_TEMPLATE = "SELECT %s FROM %s LIMIT %d";

    private Statistics() {
    }

    public static Optional<ColumnStatistics> generateNumericColumnStatistics(
            Connection connection, String tableName, String columnName, TableColumnType columnType) {

        if(!Connections.isNumericColumn(columnType)) {
            return Optional.empty();
        }

        String query = String.format(GENERIC_QUERY_TEMPLATE, columnName, tableName, QUERY_ROWS_LIMIT);
        try {
            Integer totalValues = 0;
            Integer nullValues = 0;
            Double minimumValue = Double.MAX_VALUE;
            Double maximumValue = Double.MIN_VALUE;
            Map<Double, Integer> valuesByCount = new HashMap<>();
            BigDecimal bdMean = BigDecimal.ZERO;
            BigDecimal bdStdDev = BigDecimal.ZERO;

            ResultSet resultSet = connection.createStatement().executeQuery(query);

            while(resultSet.next()) {
                Double result = resultSet.getDouble(columnName);
                totalValues++;

                if(Objects.isNull(result)) {
                    nullValues++;
                    continue;
                }

                bdMean = bdMean.add(BigDecimal.valueOf(result));
                valuesByCount.put(result, valuesByCount.getOrDefault(result, 0) + 1);

                if(Double.compare(result, minimumValue) < 0) {
                    minimumValue = result;
                } else if(Double.compare(result, maximumValue) > 0) {
                    maximumValue = result;
                }
            }
            resultSet.close();
            bdMean = bdMean.divide(BigDecimal.valueOf(totalValues - nullValues), 3, BigDecimal.ROUND_HALF_UP);
            Double mean = bdMean.doubleValue();

            ResultSet stdDevResultSet = connection.createStatement().executeQuery(query);

            while(stdDevResultSet.next()) {
                Double result = stdDevResultSet.getDouble(columnName);

                bdStdDev = bdStdDev.add(BigDecimal.valueOf(Math.pow(result - mean, 2.0)));
            }
            stdDevResultSet.close();

            return Optional.of(
                    new NumericColumnStatistics(totalValues, nullValues, minimumValue, maximumValue, valuesByCount,
                            mean, Math.sqrt(bdStdDev.divide(
                                    BigDecimal.valueOf(
                                            totalValues - nullValues), 3, BigDecimal.ROUND_HALF_UP).doubleValue())));
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<ColumnStatistics> generateTextualColumnStatistics(
            Connection connection, String tableName, String columnName, TableColumnType columnType) {

        if(!Connections.isTextualColumn(columnType)) {
            return Optional.empty();
        }

        String query = String.format(GENERIC_QUERY_TEMPLATE, columnName, tableName, QUERY_ROWS_LIMIT);
        try {
            Integer totalValues = 0;
            Integer nullValues = 0;
            Integer minimumLength = Integer.MAX_VALUE;
            Integer maximumLength = Integer.MIN_VALUE;
            BigDecimal averageLength = BigDecimal.ZERO;
            Map<String, Integer> valuesByCount = new HashMap<>();

            ResultSet resultSet = connection.createStatement().executeQuery(query);

            while(resultSet.next()) {
                String result = resultSet.getString(columnName);

                if(Objects.isNull(result)) {
                    nullValues++;
                    continue;
                }

                int resultLength = result.length();
                totalValues++;
                valuesByCount.put(result, valuesByCount.getOrDefault(result, 0) + 1);

                averageLength = averageLength.add(BigDecimal.valueOf(resultLength));
                if(Integer.compare(resultLength, minimumLength) < 0) {
                    minimumLength = resultLength;
                } else if(Integer.compare(resultLength, maximumLength) > 0) {
                    maximumLength = resultLength;
                }
            }
            resultSet.close();

            return Optional.of(
                    new TextualColumnStatistics(totalValues,
                                                nullValues,
                                                minimumLength,
                                                maximumLength,
                                                averageLength.divide(
                                                        BigDecimal.valueOf(
                                                                totalValues - nullValues),
                                                                3,
                                                                BigDecimal.ROUND_HALF_UP).doubleValue(),
                                                valuesByCount));
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
