package hr.fer.zavrad.dbprofiler.model;

/**
 * Created by filip on 11.05.17..
 */
public class DatabaseObject {

    private final DatabaseObjectType type;

    public DatabaseObject(DatabaseObjectType type) {
        this.type = type;
    }

    public DatabaseObjectType getType() {
        return type;
    }
}
