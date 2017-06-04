package hr.fer.zavrad.dbprofiler.controller.rule;

import hr.fer.zavrad.dbprofiler.model.rule.NumericRangeRule;
import hr.fer.zavrad.dbprofiler.model.rule.Rule;
import hr.fer.zavrad.dbprofiler.util.AlertBox;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.sql.Connection;

public class NumericRangeRuleController {

    @FXML
    private TextField tfFrom;
    @FXML
    private TextField tfTo;
    @FXML
    private TextField tfTable;
    @FXML
    private TextField tfColumn;
    @FXML
    private ListView lvResult;
    @FXML
    private Button btnExecute;

    private final Connection connection;

    public NumericRangeRuleController(Connection connection) {
        this.connection = connection;
    }

    public void initialize() {
        btnExecute.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lvResult.getItems().clear();
                new NumericRangeRule(
                        connection, tfFrom.getText(), tfTo.getText(), tfTable.getText(), tfColumn.getText())
                            .execute(lvResult);
            }
        });
    }
}
