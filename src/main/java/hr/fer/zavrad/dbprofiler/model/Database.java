package hr.fer.zavrad.dbprofiler.model;

import java.util.Objects;

public class Database extends ProfilerObject {

    private final String name;

    public Database(String name) {
        super(ProfilerObjectType.DATABASE);

        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
