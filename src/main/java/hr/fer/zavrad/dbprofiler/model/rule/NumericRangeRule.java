package hr.fer.zavrad.dbprofiler.model.rule;

import hr.fer.zavrad.dbprofiler.model.TableColumnType;
import hr.fer.zavrad.dbprofiler.util.AlertBox;
import hr.fer.zavrad.dbprofiler.util.Connections;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class NumericRangeRule extends Rule {

    private static final String NAME = "Range";
    private static final int RESULTS_PRINT_LIMIT = 100;

    private Connection connection;
    private String from;
    private String to;
    private String table;
    private String column;

    public NumericRangeRule(Connection connection, String from, String to, String table, String column) {
        super(RuleType.NUMERIC_RANGE);
        this.connection = connection;
        this.from = from;
        this.to = to;
        this.table = table;
        this.column = column;
    }

    public NumericRangeRule() {
        this(null, null, null, null, null);
    }

    @Override
    public void execute(ListView<String> listView) {
        Optional<Double> fromDouble = Optional.empty();
        Optional<Double> toDouble = Optional.empty();

        try {
            fromDouble = from.trim().isEmpty() ? Optional.empty() : Optional.of(Double.parseDouble(from));
            toDouble = to.trim().isEmpty() ? Optional.empty() : Optional.of(Double.parseDouble(to));
        } catch (NumberFormatException e) {
            AlertBox.display("Number format error", e.getMessage());
            return;
        }

        if(!fromDouble.isPresent() && !toDouble.isPresent()) {
            AlertBox.display("Range error", "Please specify at least one side of range");
            return;
        }

        Optional<TableColumnType> columnType = Connections.getColumnType(column, table, connection);
        if(!columnType.isPresent()) {
            AlertBox.display("SQL error", "Invalid column or table name");
            return;
        }

        if(!Connections.isNumericColumn(columnType.get())) {
            AlertBox.display("Wrong column type", "Numeric column expected");
            return;
        }

        String query = String.format("SELECT %s FROM %S", column, table);

        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery(query);
            int resultCount = 0;

            while(resultSet.next()) {
                String result = resultSet.getString(1);

                if(Objects.isNull(result)) continue;

                Double resultDouble = Double.parseDouble(result);
                if(fromDouble.isPresent() && toDouble.isPresent()) {
                    if (Double.compare(resultDouble, fromDouble.get()) >= 0 &&
                            Double.compare(resultDouble, toDouble.get()) <= 0) {
                        continue;
                    }
                } else if (fromDouble.isPresent() && Double.compare(resultDouble, fromDouble.get()) >= 0 ||
                        toDouble.isPresent() && Double.compare(resultDouble, toDouble.get()) <= 0) {
                    continue;
                }

                resultCount++;
                if(resultCount <= RESULTS_PRINT_LIMIT) {
                    listView.getItems().add(result);

                    if(resultCount == RESULTS_PRINT_LIMIT) {
                        listView.getItems().add("...");
                    }
                }
            }

            listView.getItems().add(String.format("Total count: %d%n", resultCount));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
