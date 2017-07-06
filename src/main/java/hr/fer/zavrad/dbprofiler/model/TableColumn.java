package hr.fer.zavrad.dbprofiler.model;

import hr.fer.zavrad.dbprofiler.model.statistics.ColumnStatistics;
import hr.fer.zavrad.dbprofiler.util.Connections;
import hr.fer.zavrad.dbprofiler.util.Statistics;
import javafx.scene.control.Tab;

import java.sql.Connection;
import java.util.Optional;

public class TableColumn extends ProfilerObject {

    private final Table table;
    private final String columnName;
    private final TableColumnType columnType;
    private Optional<ColumnStatistics> statistics;
    private final Connection connection;
    private boolean statisticsCreated;

    public TableColumn(Table table, String columnName, int columnType, Connection connection,
                       boolean createStatistics) {

        super(ProfilerObjectType.COLUMN);

        this.table = table;
        this.columnName = columnName;
        this.columnType = Connections.getColumnType(columnType).get();
        this.connection = connection;

        if(createStatistics) createStatistics();
    }

    public Table getTable() {
        return table;
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

    public boolean statisticsCreated() {
        return statisticsCreated;
    }

    public void createStatistics() {
        if(statisticsCreated) return;

        if (Connections.isNumericColumn(this.columnType)) {
            this.statistics =
                    Statistics.generateNumericColumnStatistics(connection, table, this);
        } else if (Connections.isTextualColumn(this.columnType)) {
            this.statistics =
                    Statistics.generateTextualColumnStatistics(connection, table, this);
        } else if (this.columnType == TableColumnType.DATE) {
            this.statistics =
                    Statistics.generateDateColumnStatistics(connection, table, this);
        } else if (this.columnType == TableColumnType.TIME) {
            this.statistics =
                    Statistics.generateTimeColumnStatistics(connection, table, this);
        } else if(this.columnType == TableColumnType.TIMESTAMP) {
            this.statistics =
                    Statistics.generateTimestampColumnStatistics(connection, table, this);
        } else if(this.columnType == TableColumnType.BIT) {
            this.statistics =
                    Statistics.generateBitColumnStatistics(connection, table, this);
        } else {
            this.statistics =
                    Statistics.generateGenericColumnStatistics(connection, table, this);
        }

        statisticsCreated = true;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", columnName, columnType);
    }
}
