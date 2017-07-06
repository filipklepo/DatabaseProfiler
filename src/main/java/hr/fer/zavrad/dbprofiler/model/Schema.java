package hr.fer.zavrad.dbprofiler.model;

public class Schema extends ProfilerObject {

    private final String name;

    public Schema(String name) {
        super(ProfilerObjectType.SCHEMA);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
