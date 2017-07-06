package hr.fer.zavrad.dbprofiler.controller;

import hr.fer.zavrad.dbprofiler.model.Table;
import hr.fer.zavrad.dbprofiler.util.Connections;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

public class RelationalDiagramController {

    @FXML
    private Button btnSelectAll;
    @FXML
    private Button btnDeselectAll;
    @FXML
    private Button btnOk;
    @FXML
    private Button btnCancel;
    @FXML
    private CheckBox cbShowAttributes;
    @FXML
    private ListView<String> lvSchemas;
    @FXML
    private ListView<Table> lvTables;

    private final Connection connection;

    public RelationalDiagramController(Connection connection) {
        this.connection = connection;
    }

    public void initialize() {
        lvTables.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        lvSchemas.setItems(FXCollections.observableArrayList(Connections.getSchemaNames(connection)));

        lvSchemas.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                lvTables.setItems(
                                FXCollections.observableArrayList(Connections.getTableNames(connection, newValue)
                                        .stream().map(t -> new Table(connection, t, newValue))
                                        .collect(Collectors.toList())));
            }
        });
        lvSchemas.getSelectionModel().selectFirst();

        btnSelectAll.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lvTables.getSelectionModel().selectAll();
            }
        });
        btnDeselectAll.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lvTables.getSelectionModel().clearSelection();
            }
        });

        btnOk.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String selectedSchema = lvSchemas.getSelectionModel().getSelectedItem();
                List<Table> selectedTables = lvTables.getSelectionModel().getSelectedItems();

                ((Stage)btnOk.getScene().getWindow()).close();
                if(!selectedTables.isEmpty()) {
                    final SwingNode swingNode = new SwingNode();

                    if(cbShowAttributes.isSelected()) {
                        selectedTables.forEach(t -> t.setShowAttributes(true));
                    }

                    Connections.createDatabaseRelationshipDiagram(selectedTables, swingNode, connection);

                    StackPane pane = new StackPane();
                    pane.getChildren().add(swingNode);

                    Stage stage = new Stage();
                    stage.setTitle("Relational diagram");
                    stage.setScene(new Scene(pane, 800, 600));
                    stage.show();
                }
            }
        });

        btnCancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ((Stage)btnCancel.getScene().getWindow()).close();
            }
        });
    }
}
