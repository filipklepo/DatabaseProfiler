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
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegularExpressionRule extends Rule {

    private static final int RESULTS_PRINT_LIMIT = 100;

    private Connection connection;
    private String regularExpression;
    private String table;
    private String column;

    public RegularExpressionRule(Connection connection, String regularExpression, String table, String column) {
        super(RuleType.REGULAR_EPRESSION);
        this.connection = connection;
        this.regularExpression = regularExpression;
        this.table = table;
        this.column = column;
    }

    public RegularExpressionRule() {
        this(null, null, null, null);
    }

    @Override
    public void execute(ListView<String> listView) {
        Optional<TableColumnType> columnType = Connections.getColumnType(column, table, connection);
        if(!columnType.isPresent()) {
            AlertBox.display("SQL error", "Invalid column or table name");
            return;
        }

        if(!Connections.isTextualColumn(columnType.get())) {
            AlertBox.display("Wrong column type", "Textual column expected");
            return;
        }

        Pattern pattern = null;
        try {
            pattern = Pattern.compile(regularExpression);
        } catch (PatternSyntaxException e) {
            AlertBox.display("Regular expression error", "Invalid regular expression was provided.");
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

                if(pattern.matcher(result).matches()) continue;

                resultCount++;
                if(resultCount <= RESULTS_PRINT_LIMIT) {
                    listView.getItems().add(String.format("%d. %s", resultCount, result));

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
