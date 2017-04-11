package hr.fer.zavrad.dbprofiler.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

import java.sql.*;

public class DatabaseOverviewController {

    private Connection connection;

    @FXML
    private ListView<String> lvTables;

    @FXML
    private TableView tvContent;

    public DatabaseOverviewController(Connection connection) {
        this.connection = connection;
    }

    private void initialize() {
    }
}
