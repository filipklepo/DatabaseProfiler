package hr.fer.zavrad.dbprofiler.controller;

import hr.fer.zavrad.dbprofiler.DatabaseProfiler;
import hr.fer.zavrad.dbprofiler.util.ConnectionGenerator;
import hr.fer.zavrad.dbprofiler.model.DatabaseType;
import hr.fer.zavrad.dbprofiler.util.AlertBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.util.Optional;

public class ConnectController {

    @FXML
    private ChoiceBox cbType;

    @FXML
    private Button btnTest;
    @FXML
    private Button btnConnect;

    @FXML
    private TextField tfAddress;
    @FXML
    private TextField tfPort;
    @FXML
    private TextField tfName;

    @FXML
    private TextField tfUsername;
    @FXML
    private TextField tfPassword;

    private DatabaseProfiler databaseProfiler;

    public ConnectController(DatabaseProfiler databaseProfiler) {
        this.databaseProfiler = databaseProfiler;
    }

    public void initialize() {

        tfAddress.setText(ConnectionGenerator.DEFAULT_ADDRESS);

        ObservableList<DatabaseType> cbItems = FXCollections.observableArrayList();
        cbItems.addAll(DatabaseType.POSTGRE, DatabaseType.MYSQL, DatabaseType.SQL_SERVER);
        cbType.setItems(cbItems);

        cbType.getSelectionModel().selectFirst();
        tfPort.setText(((DatabaseType)cbType.getValue()).getPort());

        cbType.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if((DatabaseType)cbType.getValue() == DatabaseType.POSTGRE) {
                    tfPort.setText(DatabaseType.POSTGRE.getPort());
                } else if((DatabaseType)cbType.getValue() == DatabaseType.MYSQL){
                    tfPort.setText(DatabaseType.MYSQL.getPort());
                } else if((DatabaseType)cbType.getValue() == DatabaseType.SQL_SERVER){
                    tfPort.setText(DatabaseType.SQL_SERVER.getPort());
                }
            }
        });

        btnTest.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ConnectionGenerator connectionGenerator = createConnectionGenerator();

                if(!connectionGenerator.generate().isPresent()) {
                    AlertBox.display("Error", "Unable to connect to database with provided credentials.");
                } else {
                    AlertBox.display("Success", "Database with valid credentials is provided.");
                }
            }
        });
        btnConnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ConnectionGenerator connectionGenerator = createConnectionGenerator();

                Optional<Connection> connection = connectionGenerator.generate();
                if(!connection.isPresent()) {
                    AlertBox.display("Error", "Unable to connect to database with provided credentials.");
                } else {
                    databaseProfiler.setConnection(connection.get());
                    databaseProfiler.getConnectStage().hide();
                }
            }
        });
    }

    private ConnectionGenerator createConnectionGenerator() {
        return new ConnectionGenerator((DatabaseType) cbType.getValue(),
                tfAddress.getText(),
                tfPort.getText(),
                tfName.getText(),
                tfUsername.getText(),
                tfPassword.getText());
    }
}