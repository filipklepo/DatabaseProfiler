package hr.fer.zavrad.dbprofiler.controller.rule;

import hr.fer.zavrad.dbprofiler.model.rule.RegularExpressionRule;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.sql.Connection;

public class RegularExpressionRuleController {

    @FXML
    private TextField tfRegularExpression;
    @FXML
    private TextField tfTable;
    @FXML
    private TextField tfColumn;
    @FXML
    private ListView lvResult;
    @FXML
    private Button btnExecute;

    private final Connection connection;

    public RegularExpressionRuleController(Connection connection) {
        this.connection = connection;
    }

    public void initialize() {
        btnExecute.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lvResult.getItems().clear();
                new RegularExpressionRule(
                        connection, tfRegularExpression.getText(), tfTable.getText(), tfColumn.getText())
                        .execute(lvResult);
            }
        });
    }
}
