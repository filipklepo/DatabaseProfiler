package hr.fer.zavrad.dbprofiler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class DatabaseProfiler extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("DatabaseProfiler");

        initRootLayout();
        showDatabaseData();
    }

    private void initRootLayout() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(DatabaseProfiler.class.getResource("/view/Root.fxml"));
        rootLayout = loader.load();

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showDatabaseData() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(DatabaseProfiler.class.getResource("/view/DatabaseOverview.fxml"));
            AnchorPane databaseOverview = (AnchorPane) loader.load();

            rootLayout.setCenter(databaseOverview);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        launch(args);

        //Class.forName("org.postgresql.Driver");

        //Connection connection = DriverManager.getConnection(
        //            "jdbc:postgresql://127.0.0.1:5432/dvdrental", "postgres",
        //            "filip95");

        //DatabaseMetaData metadata = connection.getMetaData();
        //ResultSet tables = metadata.getTables(null, null, "%",
        //                                      new String[]{"TABLE"});
        //while(tables.next()) {
        //    String name = tables.getString("TABLE_NAME");
        //    System.out.println(name);
        //    System.out.println("---------------------------------------");

        //    Statement st = connection.createStatement();
        //    ResultSet rs = st.executeQuery("SELECT * FROM " + name);

        //    rs.close();
        //    st.close();

        //    System.out.println();
        //}
    }
}
