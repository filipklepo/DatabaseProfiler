package hr.fer.zavrad.dbprofiler.controller;

import hr.fer.zavrad.dbprofiler.DatabaseProfiler;
import hr.fer.zavrad.dbprofiler.model.ConnectionInfo;
import hr.fer.zavrad.dbprofiler.model.DatabaseType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class ConnectController {

    @FXML
    private ChoiceBox cbType;

    @FXML
    private Button buttonTest;
    @FXML
    private Button buttonConnect;

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

    public ConnectController() {
    }

    @FXML
    private void initialize() {
        tfAddress.setText(ConnectionInfo.DEFAULT_ADDRESS);

        ObservableList<DatabaseType> cbItems = FXCollections.emptyObservableList();
        cbItems.add(DatabaseType.POSTGRE);
        cbItems.add(DatabaseType.MYSQL);
        cbType.setItems(cbItems);
        cbType.getSelectionModel().selectFirst();

        cbType.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if((DatabaseType)cbType.getValue() == DatabaseType.POSTGRE) {
                    tfPort.setText(DatabaseType.POSTGRE.getPort());
                } else {
                    tfPort.setText(DatabaseType.MYSQL.getPort());
                }
            }
        });

        buttonTest.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ConnectionInfo connectionInfo =
                        new ConnectionInfo((DatabaseType) cbType.getValue(),
                                           tfAddress.getText(),
                                           tfPort.getText(),
                                           tfName.getText(),
                                           tfUsername.getText(),
                                           tfPassword.getText());

                if(connectionInfo.generateConnection().isPresent()) {

                }
            }
        });
        buttonConnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });
    }

    public void setDatabaseProfiler(DatabaseProfiler databaseProfiler) {
        this.databaseProfiler = databaseProfiler;
    }
}