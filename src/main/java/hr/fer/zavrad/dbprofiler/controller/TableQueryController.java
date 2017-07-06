package hr.fer.zavrad.dbprofiler.controller;

import hr.fer.zavrad.dbprofiler.model.Table;
import hr.fer.zavrad.dbprofiler.util.AlertBox;
import hr.fer.zavrad.dbprofiler.util.Queries;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TableQueryController {

    private static final int QUERY_RESULT_LIMIT = 20;
    @FXML
    private TextField tfQuery;
    @FXML
    private Button btnRun;
    @FXML
    private TableView tvQueryResult;

    private Connection connection;
    private Table table;

    public TableQueryController(Connection connection, Table table) {
        this.connection = connection;
        this.table = table;
    }

    public void initialize() {
        tvQueryResult.setPlaceholder(new Text("Query result will appear here."));
        tfQuery.setText(Queries.getSelectWithLimitQuery(connection, table, QUERY_RESULT_LIMIT).get());

        btnRun.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String query = tfQuery.getText();
                tvQueryResult.getColumns().clear();
                tvQueryResult.getItems().clear();

                try {
                    ResultSet resultSet = connection.createStatement().executeQuery(query);

                    ObservableList<ObservableList> data = FXCollections.observableArrayList();

                    for(int i = 1, length = resultSet.getMetaData().getColumnCount(); i < length; ++i) {

                        final int j = i - 1;
                        TableColumn column = new TableColumn(resultSet.getMetaData().getColumnName(i));

                        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,
                                                   ObservableValue<String>>(){

                            public ObservableValue<String> call(
                                    TableColumn.CellDataFeatures<ObservableList, String> param) {
                                String value =
                                        param.getValue().get(j) != null ? param.getValue().get(j).toString() : "null";
                                return new SimpleStringProperty(value);
                            }
                        });

                        tvQueryResult.getColumns().addAll(column);
                    }

                    while(resultSet.next()){
                        ObservableList<String> row = FXCollections.observableArrayList();

                        for(int i = 1, length = resultSet.getMetaData().getColumnCount(); i < length; ++i) {
                            row.add(resultSet.getString(i));
                        }

                        data.add(row);
                    }

                    tvQueryResult.setItems(data);
                } catch (SQLException e) {
                    AlertBox.display("SQL Error", e.getMessage());
                }
            }
        });

        btnRun.fire();
    }
}
