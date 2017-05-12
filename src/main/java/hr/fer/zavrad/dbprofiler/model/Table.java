package hr.fer.zavrad.dbprofiler.model;

public class Table extends DatabaseObject {

    private final String name;

    public Table(String name) {
        super(DatabaseObjectType.TABLE);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}