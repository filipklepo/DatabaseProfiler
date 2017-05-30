package hr.fer.zavrad.dbprofiler.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.StringJoiner;

public class Table extends DatabaseObject {

    private final String name;
    private Optional<String> representationWithColumns;
    private boolean showAttributes;

    public Table(String name) {
        super(DatabaseObjectType.TABLE);
        this.name = name;
        this.representationWithColumns = Optional.empty();
    }

    public Table(Connection connection, String name) {
        this(name);

        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement()
                    .executeQuery("SELECT * FROM " + name + " LIMIT 1");

            while(resultSet.next()) {
                for(int i = 1, length = resultSet.getMetaData().getColumnCount(); i <= length; ++i) {

                    joiner.add(new TableColumn(name, resultSet.getMetaData().getColumnName(i),
                            resultSet.getMetaData().getColumnType(i),
                            connection, false).toString());

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.representationWithColumns = Optional.of(String.format("%s%n%n%s", this.name, joiner.toString()));
    }

    public void setShowAttributes(boolean showAttributes) {
        this.showAttributes = showAttributes;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return representationWithColumns.isPresent() && showAttributes ? representationWithColumns.get() : name;
    }
}