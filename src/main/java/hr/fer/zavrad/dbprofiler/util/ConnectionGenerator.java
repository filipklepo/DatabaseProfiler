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
import java.util.Properties;

public class ConnectionGenerator {

    public static final String DEFAULT_ADDRESS = "127.0.0.1";
    private static final String CONNECTION_STRING_TEMPLATE = "jdbc:%s://%s:%s/";
    private static final String SQL_SERVER_CONNECTION_STRING_TEMPLATE = "jdbc:%s://%s%s";

    private final DatabaseType type;

    private final String address;
    private final String port;
    private final String databaseName;

    private final String username;
    private final String password;

    private final Optional<String> instance;

    public ConnectionGenerator(DatabaseType type, String databaseName, String username, String password) {
        this(type, DEFAULT_ADDRESS, type.getPort(), databaseName, username, password);
    }

    public ConnectionGenerator(DatabaseType type, String address, String port,
                               String databaseName, String username, String password) {
        this.type = type;
        this.address = address;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
        this.instance = Optional.empty();
    }

    public ConnectionGenerator(String address, String port, String databaseName, String username, String password,
                               String instance) {
        this.type = DatabaseType.SQL_SERVER;
        this.address = address;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
        this.instance = !instance.isEmpty() ? Optional.of(instance) : Optional.empty();
    }

    /**
     * Establishes connection with database defined by ConnectionGenerator's member variables. If for whatever reason
     * connection can not be established, <b>Optional.empty()</b> is returned.
     *
     * @return database connection
     */
    public Connection generate() throws ConnectionGeneratorException {
        switch(type) {

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
                break;
            default:
                throw new RuntimeException("Unsupported database was given.");
        }

        try {
            String connectionString = String.format(CONNECTION_STRING_TEMPLATE, type.getConnectionName(),
                                        address, port);
            switch (type) {
                case SQL_SERVER:
                    if(instance.isPresent()) {
                        connectionString = String.format(SQL_SERVER_CONNECTION_STRING_TEMPLATE, type.getConnectionName(),
                                address, "\\" + instance.get());
                    } else {
                        connectionString = String.format(SQL_SERVER_CONNECTION_STRING_TEMPLATE, type.getConnectionName(),
                                address, ":" + port);
                    }

                    Properties connectionProperties = new Properties();
                    connectionProperties.put("database", databaseName);
                    connectionProperties.put("user", username);
                    connectionProperties.put("password", password);

                    return DriverManager.getConnection(connectionString, connectionProperties);
                default:
                    return DriverManager.getConnection(connectionString + databaseName, username, password);
            }

        } catch (SQLException e) {
            throw new ConnectionGeneratorException(e.getMessage());
        }
    }
}