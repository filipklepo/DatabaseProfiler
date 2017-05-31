package hr.fer.zavrad.dbprofiler.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Table extends ProfilerObject {

    private static final int REPRESENTATION_WHITESPACE_OFFSET = 5;

    private final String name;
    private Optional<String> representationWithColumns;
    private Optional<String> representationWithRowsCount;
    private boolean showAttributes;
    private boolean showRowsCount;

    public Table(String name) {
        super(ProfilerObjectType.TABLE);
        this.name = name;
        this.representationWithColumns = Optional.empty();
        this.representationWithRowsCount = Optional.empty();
    }

    public Table(Connection connection, String name, boolean rowCountRepresentation) {
        this(name);

        List<String> attributes = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement()
                    .executeQuery("SELECT * FROM " + name + " LIMIT 1");

            while (resultSet.next()) {
                for (int i = 1, length = resultSet.getMetaData().getColumnCount(); i <= length; ++i) {

                    attributes.add(new TableColumn(name, resultSet.getMetaData().getColumnName(i),
                            resultSet.getMetaData().getColumnType(i),
                            connection, false).toString());

                }
            }

            int underscoreSeparatorLength = attributes.stream().mapToInt(String::length).max().getAsInt()
                                + REPRESENTATION_WHITESPACE_OFFSET;

            attributes.add(0, this.name);
            attributes.add(1, String.join("", Collections.nCopies(underscoreSeparatorLength, "_")));
            attributes.add(2, String.join("", Collections.nCopies(underscoreSeparatorLength, " ")));

            StringJoiner joiner = new StringJoiner(System.lineSeparator());
            attributes.forEach(a -> joiner.add(a));

            this.representationWithColumns = Optional.of(joiner.toString());
            this.representationWithRowsCount = Optional.empty();

        } catch (SQLException e) {
            this.representationWithColumns = Optional.empty();
            this.representationWithRowsCount = Optional.empty();
        }

        if(rowCountRepresentation) {

            ResultSet rowCountResultSet = null;
            try {
                int rowsCount = 0;

                rowCountResultSet = connection.createStatement()
                        .executeQuery("SELECT COUNT(*) AS COUNT FROM " + name);

                while (rowCountResultSet.next()) {
                    rowsCount = rowCountResultSet.getInt("COUNT");
                    break;
                }

                this.representationWithRowsCount = Optional.of(String.format("%s (%d rows)", this.name, rowsCount));
                this.representationWithColumns = Optional.empty();

            } catch (SQLException e) {
                e.printStackTrace();

                this.representationWithColumns = Optional.empty();
                this.representationWithRowsCount = Optional.empty();
            }
        }
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

    @Override
    public String toString() {
        if(representationWithRowsCount.isPresent() && showRowsCount) {
            return representationWithRowsCount.get();
        }
        if(representationWithColumns.isPresent() && showAttributes) {
            return representationWithColumns.get();
        }

        return this.getName();
    }
}