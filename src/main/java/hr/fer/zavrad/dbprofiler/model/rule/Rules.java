package hr.fer.zavrad.dbprofiler.model.rule;

import hr.fer.zavrad.dbprofiler.model.ProfilerObject;
import hr.fer.zavrad.dbprofiler.model.ProfilerObjectType;

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
