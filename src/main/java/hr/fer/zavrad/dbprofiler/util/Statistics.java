package hr.fer.zavrad.dbprofiler.util;

import hr.fer.zavrad.dbprofiler.model.Table;
import hr.fer.zavrad.dbprofiler.model.TableColumn;
import hr.fer.zavrad.dbprofiler.model.statistics.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

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

    private Statistics() {
    }

    public static Optional<ColumnStatistics> generateNumericColumnStatistics(
            Connection connection, Table table, TableColumn column) {

        String query = Queries.getSelectColumnQuery(connection, table, column).get();
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
                Double result = resultSet.getDouble(column.getColumnName());
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
                Double result = stdDevResultSet.getDouble(column.getColumnName());

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
            Connection connection, Table table, TableColumn column) {

        String query = Queries.getSelectColumnQuery(connection, table, column).get();
        try {
            Integer totalValues = 0;
            Integer nullValues = 0;
            Integer minimumLength = Integer.MAX_VALUE;
            Integer maximumLength = Integer.MIN_VALUE;
            BigDecimal averageLength = BigDecimal.ZERO;
            Map<String, Integer> valuesByCount = new HashMap<>();

            ResultSet resultSet = connection.createStatement().executeQuery(query);

            while(resultSet.next()) {
                String result = resultSet.getString(column.getColumnName());

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
            Connection connection, Table table, TableColumn column) {

        String query = Queries.getSelectColumnQuery(connection, table, column).get();
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
                Date result = resultSet.getDate(column.getColumnName());
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
                Date result = stdDevResultSet.getDate(column.getColumnName());

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
            Connection connection, Table table, TableColumn column) {

        String query = Queries.getSelectColumnQuery(connection, table, column).get();
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
                Time result = resultSet.getTime(column.getColumnName());
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
                Date result = stdDevResultSet.getDate(column.getColumnName());

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
                Connection connection, Table table, TableColumn column) {

            String query = Queries.getSelectColumnQuery(connection, table, column).get();
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
                    Timestamp result = resultSet.getTimestamp(column.getColumnName());
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
                    Timestamp result = stdDevResultSet.getTimestamp(column.getColumnName());

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
                Connection connection, Table table, TableColumn column) {

            String query = Queries.getSelectColumnQuery(connection, table, column).get();
            try {
                Integer totalValues = 0;
                Integer nullValues = 0;

                ResultSet resultSet = connection.createStatement().executeQuery(query);

                while (resultSet.next()) {
                    Object result = resultSet.getObject(column.getColumnName());
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

    public static Optional<ColumnStatistics> generateBitColumnStatistics(
            Connection connection, Table table, TableColumn column) {

        String query = Queries.getSelectColumnQuery(connection, table, column).get();
        try {
            Integer nullValues = 0;
            Integer zeros = 0;
            Integer ones = 0;

            ResultSet resultSet = connection.createStatement().executeQuery(query);

            while (resultSet.next()) {
                String result = resultSet.getString(column.getColumnName());

                if (Objects.isNull(result)) {
                    nullValues++;
                }

                if(result.equals("0")) {
                    zeros++;
                } else {
                    ones++;
                }
            }

            ObservableList<PieChart.Data> distributionData = FXCollections.observableArrayList();
            distributionData.addAll(new PieChart.Data("zeros", zeros));
            distributionData.addAll(new PieChart.Data("ones", ones));

            XYChart.Series recordCount = new XYChart.Series();
            recordCount.getData().add(new XYChart.Data("Null", nullValues));

            return Optional.of(
                    new BitColumnStatistics(recordCount, distributionData));
        } catch (SQLException e) {

            e.printStackTrace();
            return Optional.empty();
        }
    }
}
