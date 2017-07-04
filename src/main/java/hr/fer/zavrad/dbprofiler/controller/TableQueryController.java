package hr.fer.zavrad.dbprofiler.controller;

import hr.fer.zavrad.dbprofiler.model.Table;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TableViewController {

    @FXML
    private TextField tfQuery;
    @FXML
    private Button btnRun;
    @FXML
    private TableView tvQueryResult;

    private Table table;

    public TableViewController(Table table) {
        this.table = table;
    }

    public void initialize() {

    }
}
