package hr.fer.zavrad.dbprofiler.util;

import hr.fer.zavrad.dbprofiler.model.*;

import java.math.BigDecimal;
import java.sql.*;
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
                }
                if(Double.compare(result, maximumValue) > 0) {
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
                    new NumericColumnStatistics(nullValues, minimumValue, maximumValue, valuesByCount,
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
                }
                if(Integer.compare(resultLength, maximumLength) > 0) {
                    maximumLength = resultLength;
                }
            }
            resultSet.close();

            return Optional.of(
                    new TextualColumnStatistics(nullValues,
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

    public static Optional<ColumnStatistics> generateDateColumnStatistics(
            Connection connection, String tableName, String columnName, TableColumnType columnType) {

        if(columnType != columnType.DATE) {
            return Optional.empty();
        }

        String query = String.format(GENERIC_QUERY_TEMPLATE, columnName, tableName, QUERY_ROWS_LIMIT);
        try {
            Integer totalValues = 0;
            Integer nullValues = 0;
            Long minimumValue = Long.MAX_VALUE;
            Long maximumValue = Long.MIN_VALUE;
            Map<Date, Integer> valuesByCount = new HashMap<>();
            BigDecimal bdMean = BigDecimal.ZERO;
            BigDecimal bdStdDev = BigDecimal.ZERO;

            ResultSet resultSet = connection.createStatement().executeQuery(query);

            while(resultSet.next()) {
                Date result = resultSet.getDate(columnName);
                totalValues++;

                if(Objects.isNull(result)) {
                    nullValues++;
                    continue;
                }

                bdMean = bdMean.add(BigDecimal.valueOf(result.getTime()));
                valuesByCount.put(result, valuesByCount.getOrDefault(result, 0) + 1);

                if(Long.compare(result.getTime(), minimumValue) < 0) {
                    minimumValue = result.getTime();
                }
                if(Double.compare(result.getTime(), maximumValue) > 0) {
                    maximumValue = result.getTime();
                }
            }
            resultSet.close();
            bdMean = bdMean.divide(BigDecimal.valueOf(totalValues - nullValues), 3, BigDecimal.ROUND_HALF_UP);
            Long mean = bdMean.longValue();

            ResultSet stdDevResultSet = connection.createStatement().executeQuery(query);

            while(stdDevResultSet.next()) {
                Date result = stdDevResultSet.getDate(columnName);

                if(Objects.isNull(result)) {
                    continue;
                }

                bdStdDev = bdStdDev.add(BigDecimal.valueOf(Math.pow(result.getTime() - mean, 2.0)));
            }
            stdDevResultSet.close();

            Long stdDev = Double.valueOf(Math.sqrt(bdStdDev.divide(
                    BigDecimal.valueOf(
                            totalValues - nullValues), 3, BigDecimal.ROUND_HALF_UP).doubleValue())).longValue();

            return Optional.of(
                    new DateColumnStatistics(nullValues, new Date(minimumValue),
                                             new Date(maximumValue), new Date(mean), stdDev, valuesByCount));
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<ColumnStatistics> generateTimeColumnStatistics(
            Connection connection, String tableName, String columnName, TableColumnType columnType) {

        if (columnType != columnType.TIME) {
            return Optional.empty();
        }

        String query = String.format(GENERIC_QUERY_TEMPLATE, columnName, tableName, QUERY_ROWS_LIMIT);
        try {
            Integer totalValues = 0;
            Integer nullValues = 0;
            Long minimumValue = Long.MAX_VALUE;
            Long maximumValue = Long.MIN_VALUE;
            Map<Time, Integer> valuesByCount = new HashMap<>();
            BigDecimal bdMean = BigDecimal.ZERO;
            BigDecimal bdStdDev = BigDecimal.ZERO;

            ResultSet resultSet = connection.createStatement().executeQuery(query);

            while (resultSet.next()) {
                Time result = resultSet.getTime(columnName);
                totalValues++;

                if (Objects.isNull(result)) {
                    nullValues++;
                    continue;
                }

                bdMean = bdMean.add(BigDecimal.valueOf(result.getTime()));
                valuesByCount.put(result, valuesByCount.getOrDefault(result, 0) + 1);

                if (Long.compare(result.getTime(), minimumValue) < 0) {
                    minimumValue = result.getTime();
                }
                if (Double.compare(result.getTime(), maximumValue) > 0) {
                    maximumValue = result.getTime();
                }
            }
            resultSet.close();
            bdMean = bdMean.divide(BigDecimal.valueOf(totalValues - nullValues), 3, BigDecimal.ROUND_HALF_UP);
            Long mean = bdMean.longValue();

            ResultSet stdDevResultSet = connection.createStatement().executeQuery(query);

            while (stdDevResultSet.next()) {
                Date result = stdDevResultSet.getDate(columnName);

                if(Objects.isNull(result)) {
                    continue;
                }

                bdStdDev = bdStdDev.add(BigDecimal.valueOf(Math.pow(result.getTime() - mean, 2.0)));
            }
            stdDevResultSet.close();

            Long stdDev = Double.valueOf(Math.sqrt(bdStdDev.divide(
                    BigDecimal.valueOf(
                            totalValues - nullValues), 3, BigDecimal.ROUND_HALF_UP).doubleValue())).longValue();

            return Optional.of(
                    new TimeColumnStatistics(nullValues, new Time(minimumValue),
                            new Time(maximumValue), new Time(mean), stdDev, valuesByCount));
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

        public static Optional<ColumnStatistics> generateTimestampColumnStatistics(
                Connection connection, String tableName, String columnName, TableColumnType columnType) {

            if (columnType != columnType.TIMESTAMP) {
                return Optional.empty();
            }

            String query = String.format(GENERIC_QUERY_TEMPLATE, columnName, tableName, QUERY_ROWS_LIMIT);
            try {
                Integer totalValues = 0;
                Integer nullValues = 0;
                Long minimumValue = Long.MAX_VALUE;
                Long maximumValue = Long.MIN_VALUE;
                Map<Timestamp, Integer> valuesByCount = new HashMap<>();
                BigDecimal bdMean = BigDecimal.ZERO;
                BigDecimal bdStdDev = BigDecimal.ZERO;

                ResultSet resultSet = connection.createStatement().executeQuery(query);

                while (resultSet.next()) {
                    Timestamp result = resultSet.getTimestamp(columnName);
                    totalValues++;

                    if (Objects.isNull(result)) {
                        nullValues++;
                        continue;
                    }

                    bdMean = bdMean.add(BigDecimal.valueOf(result.getTime()));
                    valuesByCount.put(result, valuesByCount.getOrDefault(result, 0) + 1);

                    if (Long.compare(result.getTime(), minimumValue) < 0) {
                        minimumValue = result.getTime();
                    }
                    if (Double.compare(result.getTime(), maximumValue) > 0) {
                        maximumValue = result.getTime();
                    }
                }
                resultSet.close();
                bdMean = bdMean.divide(BigDecimal.valueOf(totalValues - nullValues), 3, BigDecimal.ROUND_HALF_UP);
                Long mean = bdMean.longValue();

                ResultSet stdDevResultSet = connection.createStatement().executeQuery(query);

                while (stdDevResultSet.next()) {
                    Timestamp result = stdDevResultSet.getTimestamp(columnName);

                    if(Objects.isNull(result)) {
                        continue;
                    }

                    bdStdDev = bdStdDev.add(BigDecimal.valueOf(Math.pow(result.getTime() - mean, 2.0)));
                }
                stdDevResultSet.close();

                Long stdDev = Double.valueOf(Math.sqrt(bdStdDev.divide(
                        BigDecimal.valueOf(
                                totalValues - nullValues), 3, BigDecimal.ROUND_HALF_UP).doubleValue())).longValue();

                return Optional.of(
                        new TimestampColumnStatistics(nullValues, new Timestamp(minimumValue),
                                new Timestamp(maximumValue), new Timestamp(mean), stdDev, valuesByCount));
            } catch (SQLException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }

        public static Optional<ColumnStatistics> generateGenericColumnStatistics(
                Connection connection, String tableName, String columnName, TableColumnType columnType) {

            String query = String.format(GENERIC_QUERY_TEMPLATE, columnName, tableName, QUERY_ROWS_LIMIT);
            try {
                Integer totalValues = 0;
                Integer nullValues = 0;

                ResultSet resultSet = connection.createStatement().executeQuery(query);

                while (resultSet.next()) {
                    Object result = resultSet.getObject(columnName);
                    totalValues++;

                    if (Objects.isNull(result)) {
                        nullValues++;
                    }
                }

                return Optional.of(
                        new GenericColumnStatistics(nullValues));
            } catch (SQLException e) {

                e.printStackTrace();
                return Optional.empty();
            }
        }
    }
