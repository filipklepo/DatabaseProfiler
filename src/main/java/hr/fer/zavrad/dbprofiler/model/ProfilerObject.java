package hr.fer.zavrad.dbprofiler.model;

/**
 * Created by filip on 11.05.17..
 */
public class ProfilerObject {

    private final ProfilerObjectType type;

    public ProfilerObject(ProfilerObjectType type) {
        this.type = type;
    }

    public ProfilerObjectType getType() {
        return type;
    }
}
