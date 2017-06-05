package hr.fer.zavrad.dbprofiler.model.rule;

public enum RuleType {

    NUMERIC_RANGE("Numeric Range"),
    REGULAR_EPRESSION("Regular Expression");

    private final String name;

    RuleType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
