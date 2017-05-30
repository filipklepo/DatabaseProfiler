package hr.fer.zavrad.dbprofiler.util;

import hr.fer.zavrad.dbprofiler.model.DatabaseType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class ConnectionGenerator {

    public static final String DEFAULT_ADDRESS = "127.0.0.1";
    private static final String CONNECTION_STRING_TEMPLATE = "jdbc:%s://%s:%s/%s";

    private final ObjectProperty<DatabaseType> type;

    private final StringProperty address;
    private final StringProperty port;
    private final StringProperty databaseName;

    private final StringProperty username;
    private final StringProperty password;

    public ConnectionGenerator(DatabaseType type, String databaseName, String username, String password) {
        this(type, DEFAULT_ADDRESS, type.getPort(), databaseName, username, password);
    }

    public ConnectionGenerator(DatabaseType type, String address, String port,
                               String databaseName, String username, String password) {

        this.type = new SimpleObjectProperty<>(type);
        this.address = new SimpleStringProperty(address);
        this.port = new SimpleStringProperty(port);
        this.databaseName = new SimpleStringProperty(databaseName);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
    }

    /**
     * Establishes connection with database defined by ConnectionGenerator's member variables. If for whatever reason
     * connection can not be established, <b>Optional.empty()</b> is returned.
     *
     * @return database connection
     */
    public Optional<Connection> generate() {
        switch(type.getValue()) {

            case POSTGRE:
                try {
                    Class.forName("org.postgresql.Driver");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;

            case MYSQL:
                try {
                    Class.forName("org.mysql.Driver");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case SQL_SERVER:
                try {
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
        }

        try {
            String connectionString = String.format(CONNECTION_STRING_TEMPLATE, type.getValue().getConnectionName(),
                                        address.getValue(), port.getValue(), databaseName.getValue());

            return Optional.of(DriverManager.getConnection(connectionString, username.getValue(),
                                                           password.getValue()));
        } catch (SQLException e) {
            return Optional.empty();
        }
    }
}