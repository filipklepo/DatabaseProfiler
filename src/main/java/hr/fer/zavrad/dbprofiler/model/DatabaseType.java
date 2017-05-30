package hr.fer.zavrad.dbprofiler.model;

public enum DatabaseType {
    POSTGRE("postgresql", "PostgreSQL", "5432"),
    MYSQL("mysql", "MySQL", "3306"),
    SQL_SERVER("sqlserver", "SQL Server", "1433");

    private String connectionName;
    private String fullName;
    private String port;

    DatabaseType(String connectionNname, String fullName, String port) {
        this.connectionName = connectionNname;
        this.fullName = fullName;
        this.port = port;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPort() {
        return port;
    }

    @Override
    public String toString() {
        return fullName;
    }
}