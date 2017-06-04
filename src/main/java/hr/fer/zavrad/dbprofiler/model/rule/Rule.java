package hr.fer.zavrad.dbprofiler.model;

import java.util.Objects;

public class Rule extends ProfilerObject {

    private final String name;

    public Rule(String name) {
        super(ProfilerObjectType.RULE);
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
