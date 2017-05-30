package hr.fer.zavrad.dbprofiler.model;

import hr.fer.zavrad.dbprofiler.util.Connections;
import hr.fer.zavrad.dbprofiler.util.Statistics;

import java.sql.Connection;
import java.util.Optional;

public class TableColumn extends DatabaseObject {

    private final String tableName;
    private final String columnName;
    private final TableColumnType columnType;
    private Optional<ColumnStatistics> statistics;
    private final Connection connection;

    public TableColumn(String tableName, String columnName, int columnType, Connection connection,
                       boolean generateStatistics) {

        super(DatabaseObjectType.COLUMN);

        this.tableName = tableName;
        this.columnName = columnName;
        this.columnType = Connections.getColumnType(columnType).get();
        this.connection = connection;

        if(generateStatistics) {
            if (Connections.isNumericColumn(this.columnType)) {
                this.statistics =
                        Statistics.generateNumericColumnStatistics(connection, tableName, columnName, this.columnType);
            } else if (Connections.isTextualColumn(this.columnType)) {
                this.statistics =
                        Statistics.generateTextualColumnStatistics(connection, tableName, columnName, this.columnType);
            } else if (this.columnType == TableColumnType.DATE) {
                this.statistics =
                        Statistics.generateDateColumnStatistics(connection, tableName, columnName, this.columnType);
            } else if (this.columnType == TableColumnType.TIME) {
                this.statistics =
                        Statistics.generateTimeColumnStatistics(connection, tableName, columnName, this.columnType);
            } else if(this.columnType == TableColumnType.TIMESTAMP) {
                this.statistics =
                        Statistics.generateTimestampColumnStatistics(connection, tableName, columnName, this.columnType);
            } else {
                this.statistics =
                        Statistics.generateGenericColumnStatistics(connection, tableName, columnName, this.columnType);
            }
        }
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

    public Optional<ColumnStatistics> getStatistics() {
        return statistics;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", columnName, columnType);
    }
}
