package hr.fer.zavrad.dbprofiler;

import hr.fer.zavrad.dbprofiler.controller.ConnectController;
import hr.fer.zavrad.dbprofiler.controller.DatabaseOverviewController;
import hr.fer.zavrad.dbprofiler.model.DatabaseType;
import hr.fer.zavrad.dbprofiler.util.ConnectionGenerator;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class DatabaseProfiler extends Application {

    private static final String WINDOW_TITLE = "Database Profiler";
    private static final int WINDOW_WIDTH = 1024;
    private static final int WINDOW_HEIGHT = 768;

    private Stage primaryStage;
    private Stage connectStage;

    private Optional<Connection> connection;

    public DatabaseProfiler() {
        connection = Optional.empty();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.connectStage = new Stage();
        this.primaryStage.setTitle(WINDOW_TITLE);

        initRootLayout();
    }

    private void initRootLayout() throws IOException {
        FXMLLoader connectLoader = new FXMLLoader();
        connectLoader.setLocation(DatabaseProfiler.class.getResource("/view/Connect.fxml"));
        ConnectController connectController = new ConnectController(this);
        connectLoader.setController(connectController);

        Scene connectScene = new Scene(connectLoader.load());
        connectStage.setScene(connectScene);
        connectStage.show();
        connectStage.setOnHidden(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {

                if(connection.isPresent()) {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(DatabaseProfiler.class.getResource("/view/DatabaseOverview.fxml"));
                    DatabaseOverviewController overviewController = new DatabaseOverviewController(connection.get());
                    loader.setController(overviewController);

                    try {
                        Scene scene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);
                        scene.getStylesheets().add(
                                DatabaseProfiler.class.getResource("/assets/database_overview.css").toExternalForm());

                        primaryStage.setScene(scene);
                        primaryStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public Stage getConnectStage() {
        return connectStage;
    }

    public void setConnection(Connection connection) {
        this.connection = Optional.of(connection);
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        launch(args);
    }
}
