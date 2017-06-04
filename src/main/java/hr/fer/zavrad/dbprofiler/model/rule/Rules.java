package hr.fer.zavrad.dbprofiler.model;

public class Rules extends ProfilerObject {
    private static final String REPRESENTATION = "Rules";

    public Rules() {
        super(ProfilerObjectType.RULES);
    }

    @Override
    public String toString() {
        return REPRESENTATION;
    }
}
