package hr.fer.zavrad.dbprofiler.model;

public class ProfilerObject {

    private final ProfilerObjectType type;

    public ProfilerObject(ProfilerObjectType type) {
        this.type = type;
    }

    public ProfilerObjectType getProfilerObjectType() {
        return type;
    }
}
