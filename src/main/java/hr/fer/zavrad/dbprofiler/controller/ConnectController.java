package hr.fer.zavrad.dbprofiler.controller;

import hr.fer.zavrad.dbprofiler.DatabaseProfiler;
import hr.fer.zavrad.dbprofiler.util.ConnectionGenerator;
import hr.fer.zavrad.dbprofiler.model.DatabaseType;
import hr.fer.zavrad.dbprofiler.util.AlertBox;
import hr.fer.zavrad.dbprofiler.util.ConnectionGeneratorException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.util.Optional;

public class ConnectController {

    @FXML
    private ChoiceBox cbType;

    @FXML
    private TextField tfAddress;

    @FXML
    private TextField tfPort;
    @FXML
    private TextField tfName;
    @FXML
    private Label lblInstance;

    @FXML
    private TextField tfInstance;
    @FXML
    private TextField tfUsername;
    @FXML
    private TextField tfPassword;

    @FXML
    private Button btnTest;
    @FXML
    private Button btnConnect;

    private DatabaseProfiler databaseProfiler;

    public ConnectController(DatabaseProfiler databaseProfiler) {
        this.databaseProfiler = databaseProfiler;
    }

    public void initialize() {

        tfAddress.setText(ConnectionGenerator.DEFAULT_ADDRESS);

        ObservableList<DatabaseType> cbItems = FXCollections.observableArrayList();
        cbItems.addAll(DatabaseType.POSTGRE, DatabaseType.MYSQL, DatabaseType.SQL_SERVER);
        cbType.setItems(cbItems);


        cbType.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lblInstance.visibleProperty().setValue(false);
                tfInstance.visibleProperty().setValue(false);
                tfInstance.setText("");

                if((DatabaseType)cbType.getValue() == DatabaseType.POSTGRE) {
                    tfPort.setText(DatabaseType.POSTGRE.getPort());
                } else if((DatabaseType)cbType.getValue() == DatabaseType.MYSQL){
                    tfPort.setText(DatabaseType.MYSQL.getPort());
                } else if((DatabaseType)cbType.getValue() == DatabaseType.SQL_SERVER){
                    tfPort.setText(DatabaseType.SQL_SERVER.getPort());

                    lblInstance.visibleProperty().setValue(true);
                    tfInstance.visibleProperty().setValue(true);
                }
            }
        });
        cbType.getSelectionModel().selectFirst();
        tfPort.setText(((DatabaseType)cbType.getValue()).getPort());

        btnTest.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Connection connection = createConnectionGenerator().generate();
                    AlertBox.display("Success", "Database with valid credentials is provided.");
                } catch (ConnectionGeneratorException e) {
                    AlertBox.display("Error", e.getMessage());
                }
            }
        });
        btnConnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Connection connection = null;

                try {
                    connection = createConnectionGenerator().generate();

                    databaseProfiler.setConnection(connection);
                    databaseProfiler.getConnectStage().hide();
                } catch (ConnectionGeneratorException e) {
                    AlertBox.display("Error", e.getMessage());
                }
            }
        });
    }

    private ConnectionGenerator createConnectionGenerator() {
        return tfInstance.visibleProperty().getValue() ?
                new ConnectionGenerator(
                        tfAddress.getText(),
                        tfPort.getText(),
                        tfName.getText(),
                        tfUsername.getText(),
                        tfPassword.getText(),
                        tfInstance.getText()) :
                new ConnectionGenerator(
                        (DatabaseType) cbType.getValue(),
                        tfAddress.getText(),
                        tfPort.getText(),
                        tfName.getText(),
                        tfUsername.getText(),
                        tfPassword.getText());
    }
}