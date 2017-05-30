package hr.fer.zavrad.dbprofiler.controller;

import hr.fer.zavrad.dbprofiler.model.*;
import hr.fer.zavrad.dbprofiler.util.Connections;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class DatabaseOverviewController {

    private static final String NULL = "null";

    private Connection connection;

    @FXML
    private TreeView<DatabaseObject> tvTables;
    @FXML
    private Button btnRelationalDiagram;
    @FXML
    private VBox vbStatistics;

    public DatabaseOverviewController(Connection connection) {
        this.connection = connection;
    }

    public void initialize() {
        final Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                        tvTables.setRoot(Connections.getDatabaseSchema(connection).get());
                });
                return null;
            }
        };
        new Thread(task).start();

        tvTables.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<DatabaseObject>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<DatabaseObject>> observable,
                                TreeItem<DatabaseObject> oldValue, TreeItem<DatabaseObject> newValue) {
                if(Objects.isNull(newValue)) return;

                vbStatistics.getChildren().clear();
                DatabaseObject object = newValue.getValue();
                if(object.getType() == DatabaseObjectType.COLUMN) {

                    TableColumn column = (TableColumn) object;

                    GridPane columnstatistics = null;

                    FXMLLoader loader = new FXMLLoader();
                    if (Connections.isNumericColumn(column.getColumnType())) {
                        loader.setLocation(
                                DatabaseOverviewController.class.getResource(
                                        "/view/NumericColumnStatistics.fxml"));
                        loader.setController(
                                    new NumericColumnStatisticsController(
                                            (NumericColumnStatistics) column.getStatistics().get()));

                    } else if (Connections.isTextualColumn(column.getColumnType())) {
                        loader.setLocation(
                                DatabaseOverviewController.class.getResource(
                                        "/view/TextualColumnStatistics.fxml"));
                        TextualColumnStatisticsController controller =
                                new TextualColumnStatisticsController((TextualColumnStatistics) column.getStatistics().get());
                        loader.setController(controller);
                    } else {
                        loader.setLocation(
                                DatabaseOverviewController.class.getResource(
                                        "/view/NumericColumnStatistics.fxml"));

                        switch (column.getColumnType()) {
                            case DATE:
                                loader.setController(
                                        new DateColumnStatisticsController(
                                                (DateColumnStatistics)column.getStatistics().get()));
                                break;
                            case TIME:
                                loader.setController(
                                        new TimeColumnStatisticsController(
                                                (TimeColumnStatistics)column.getStatistics().get()));
                                break;
                            case TIMESTAMP:
                                loader.setController(
                                        new TimestampColumnStatisticsController(
                                                (TimestampColumnStatistics)column.getStatistics().get()));
                                break;
                            default:
                                loader.setController(
                                        new GenericColumnStatisticsController(
                                                (GenericColumnStatistics)column.getStatistics().get())
                                );
                        }
                    }

                    try {
                        columnstatistics = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    vbStatistics.getChildren().add(columnstatistics);
                }
            }
        });

        btnRelationalDiagram.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Pane pane = null;

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(
                        DatabaseOverviewController.class.getResource("/view/RelationalDiagramDefiner.fxml"));
                loader.setController(new RelationalDiagramController(connection));

                try {
                    pane = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Scene scene = new Scene(pane);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
            }
        });
    }
}