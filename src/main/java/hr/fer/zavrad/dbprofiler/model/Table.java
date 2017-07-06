package hr.fer.zavrad.dbprofiler.model;

import hr.fer.zavrad.dbprofiler.util.Connections;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Table extends ProfilerObject {

    private static final int REPRESENTATION_WHITESPACE_OFFSET = 5;

    private final String name;
    private int rowCount;
    private Optional<String> representationWithAttributes;
    private Optional<String> representationWithRowsCount;
    private String schemaName;
    private boolean showAttributes;
    private boolean showRowsCount;

    public Table(Connection connection, String name, String schemaName) {
        this(connection, name, false, schemaName);
    }

    public Table(Connection connection, String name, boolean rowCountRepresentation, String schemaName) {
        super(ProfilerObjectType.TABLE);

        this.name = name;
        this.schemaName = schemaName;

        if(rowCountRepresentation) {
            this.rowCount = calculateRowCount(connection, schemaName, name);
            this.representationWithAttributes = Optional.empty();
            this.representationWithRowsCount = Optional.of(String.format("%s (%d rows)", this.name, this.rowCount));
        } else {
            this.representationWithAttributes = createAttributeRepresentation(connection, schemaName, name);
            this.representationWithRowsCount = Optional.empty();
        }
    }

    private static Optional<String> createAttributeRepresentation(Connection connection, String schemaName, String name) {

        try {
            List<String> attributes = new ArrayList<>();

            ResultSet resultSet = connection.createStatement()
                    .executeQuery("SELECT * FROM " + schemaName + "." + name + " WHERE 1 = 0");

            for (int i = 1, length = resultSet.getMetaData().getColumnCount(); i <= length; ++i) {

                attributes.add(String.format("%s: %s", resultSet.getMetaData().getColumnName(i),
                        Connections.getColumnType(resultSet.getMetaData().getColumnType(i)).get()));
            }

            int underscoreSeparatorLength = attributes.stream().mapToInt(String::length).max().getAsInt()
                    + REPRESENTATION_WHITESPACE_OFFSET;

            attributes.add(0, name);
            attributes.add(1, String.join("", Collections.nCopies(underscoreSeparatorLength, "_")));
            attributes.add(2, String.join("", Collections.nCopies(underscoreSeparatorLength, " ")));

            StringJoiner joiner = new StringJoiner(System.lineSeparator());
            attributes.forEach(a -> joiner.add(a));

            return Optional.of(joiner.toString());
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    private static int calculateRowCount(Connection connection, String schemaName, String name) {
        ResultSet rowCountResultSet = null;
        int rowsCount = 0;

        try {

            rowCountResultSet = connection.createStatement()
                    .executeQuery("SELECT COUNT(*) AS COUNT FROM " + schemaName + "." + name);

            while (rowCountResultSet.next()) {
                rowsCount = rowCountResultSet.getInt("COUNT");
                break;
            }

        } catch (SQLException e) {
        }

        return rowsCount;
    }

    public void setShowAttributes(boolean showAttributes) {
        this.showAttributes = showAttributes;
    }

    public void setShowRowsCount(boolean showRowsCount) {
        this.showRowsCount = showRowsCount;
    }

    public String getName() {
        return name;
    }

    public int getRowCount() {
        return rowCount;
    }

    public String getSchemaName() {
        return schemaName;
    }

    @Override
    public String toString() {
        if(representationWithAttributes.isPresent() && showAttributes) {
            return representationWithAttributes.get();
        }
        if(representationWithRowsCount.isPresent() && showRowsCount) {
            return representationWithRowsCount.get();
        }

        return this.getName();
    }
}