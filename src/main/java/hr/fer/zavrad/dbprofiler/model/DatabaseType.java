package hr.fer.zavrad.dbprofiler.model;

public enum DatabaseType {
    POSTGRE("postgresql", "5432"),
    MYSQL("mysql", "3306");

    private String name;
    private String port;

    DatabaseType(String name, String port) {
        this.name = name;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public String getPort() {
        return this.port;
    }
}