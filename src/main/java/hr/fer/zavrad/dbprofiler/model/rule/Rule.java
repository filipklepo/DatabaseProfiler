package hr.fer.zavrad.dbprofiler.model.rule;

import hr.fer.zavrad.dbprofiler.model.ProfilerObject;
import hr.fer.zavrad.dbprofiler.model.ProfilerObjectType;
import javafx.scene.control.ListView;

public abstract class Rule extends ProfilerObject {

    private final RuleType ruleType;

    public Rule(RuleType ruleType) {
        super(ProfilerObjectType.RULE);
        this.ruleType = ruleType;
    }

    public abstract void execute(ListView<String> listView);

    public RuleType getRuleType() {
        return ruleType;
    }

    @Override
    public String toString() {
        return ruleType.toString();
    }
}
