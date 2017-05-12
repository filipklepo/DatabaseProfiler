package hr.fer.zavrad.dbprofiler.model;

import hr.fer.zavrad.dbprofiler.util.Connections;

import java.sql.Connection;
import java.util.Optional;

public class TableColumn extends DatabaseObject {

    private final String tableName;
    private final String columnName;
    private final TableColumnType columnType;
    private Optional<NumericColumnStatistics> statistics;
    private final Connection connection;

    public TableColumn(String tableName, String columnName, int columnType, Connection connection) {
        super(DatabaseObjectType.COLUMN);

        this.tableName = tableName;
        this.columnName = columnName;
        System.out.println(columnType);
        this.columnType = Connections.getColumnType(columnType).get();
        this.connection = connection;

        this.statistics = Connections.generateNumericColumnStatistics(connection, tableName, columnName, this.columnType);
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public TableColumnType getColumnType() {
        return columnType;
    }



    @Override
    public String toString() {
        return String.format("%s: %s", columnName, columnType);
    }
}
