package hr.fer.zavrad.dbprofiler.controller;

import hr.fer.zavrad.dbprofiler.model.DatabaseObject;
import hr.fer.zavrad.dbprofiler.model.DatabaseObjectType;
import hr.fer.zavrad.dbprofiler.model.TableColumn;
import hr.fer.zavrad.dbprofiler.util.Connections;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Objects;

public class DatabaseOverviewController {

    private static final String NULL = "null";

    private Connection connection;

    @FXML
    private TreeView<DatabaseObject> tvTables;

    @FXML
    private TableView tvContent;

    @FXML
    private Button btnDatabaseDiagram;

    public DatabaseOverviewController(Connection connection) {
        this.connection = connection;
    }

    public void initialize() {
//        lvTables.setItems(getTableNames(connection));
//
//        lvTables.setOnMouseClicked(new EventHandler<MouseEvent>() {
//
//            @Override
//            public void handle(MouseEvent event) {
//                String selectedTable = tvTables.getSelectionModel().getSelectedItem().getValue();
//                String query = String.format("SELECT * FROM %s LIMIT 10", selectedTable);
//                tvContent.getItems().clear();
//                tvContent.getColumns().clear();
//
//                try {
//                    ResultSet resultSet = connection.createStatement().executeQuery(query);
//
//                    for(int i = 0, length = resultSet.getMetaData().getColumnCount(); i < length; ++i) {
//                        TableColumn column = new TableColumn(resultSet.getMetaData().getColumnName(i + 1));
//
//                        final int j = i;
//                        column.setCellValueFactory(
//                                new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
//
//                                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
//                                        return new SimpleStringProperty(param.getValue().get(j).toString());
//                                    }
//                        });
//
//                        tvContent.getColumns().add(column);
//                    }
//
//                    ObservableList<ObservableList> tableData = FXCollections.observableArrayList();
//                    while(resultSet.next()) {
//                        ObservableList<String> row = FXCollections.observableArrayList();
//
//                        for(int i = 1, length = resultSet.getMetaData().getColumnCount(); i <= length; ++i) {
//                            row.add(resultSet.getString(i) != null ? resultSet.getString(i) : NULL);
//                        }
//
//                        tableData.add(row);
//                    }
//                    resultSet.close();
//
//                    tvContent.setItems(tableData);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        tvTables.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<DatabaseObject>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<DatabaseObject>> observable,
                                TreeItem<DatabaseObject> oldValue, TreeItem<DatabaseObject> newValue) {
                if(Objects.isNull(newValue)) return;



                DatabaseObject item = tvTables.getSelectionModel().getSelectedItem().getValue();
                if(item.getType() == DatabaseObjectType.COLUMN) {
                    System.out.println(((TableColumn)item).getColumnName());
                    System.out.println(Connections.isNumericColumn(((TableColumn)item).getColumnType()));
                }
            }
        });

        btnDatabaseDiagram.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                final SwingNode swingNode = new SwingNode();

                Connections.createDatabaseRelationshipDiagram(swingNode, connection);

                StackPane pane = new StackPane();
                pane.getChildren().add(swingNode);

                Stage stage = new Stage();
                stage.setTitle("Database relationship diagram");
                stage.setScene(new Scene(pane, 800, 600));
                stage.show();
            }
        });


    }
}