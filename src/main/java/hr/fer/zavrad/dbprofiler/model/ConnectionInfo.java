package hr.fer.zavrad.dbprofiler.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class ConnectionInfo {

    public static final String DEFAULT_ADDRESS = "127.0.0.1";
    public static final String CONNECTION_STRING_TEMPLATE = "jdbc:%s://%s:%s/%s";

    private final ObjectProperty<DatabaseType> type;

    private final StringProperty address;
    private final StringProperty port;
    private final StringProperty databaseName;

    private final StringProperty username;
    private final StringProperty password;

    public ConnectionInfo(DatabaseType type, String databaseName, String username,
                          String password) {
        this(type, DEFAULT_ADDRESS, type.getPort(), databaseName, username, password);
    }

    public ConnectionInfo(DatabaseType type, String address, String port,
                          String databaseName, String username, String password) {
        this.type = new SimpleObjectProperty<>(type);
        this.address = new SimpleStringProperty(address);
        this.port = new SimpleStringProperty(port);
        this.databaseName = new SimpleStringProperty(databaseName);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
    }

    /**
     * Establishes connection with database defined by ConnectionInfo's member variables. If for whatever reason
     * connection can not be established, <b>Optional.empty()</b> is returned.
     *
     * @return database connection
     */
    public Optional<Connection> generateConnection() {
        switch(type.getValue()) {
            case POSTGRE:
                try {
                    Class.forName("org.postgresql.Driver");
                } catch (ClassNotFoundException e) {
                    return Optional.empty();
                }
                break;
            case MYSQL:
                try {
                    Class.forName("org.mysql.Driver");
                } catch (ClassNotFoundException e) {
                    return Optional.empty();
                }
        }

        try {
            String connectionString = String.format(CONNECTION_STRING_TEMPLATE, type.getValue().getName(),
                                        address.getValue(), port.getValue(), databaseName.getValue());

            Connection connection = DriverManager.getConnection(connectionString, username.getValue(),
                                                                password.getValue());

            return Optional.of(connection);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }
}