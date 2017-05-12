package hr.fer.zavrad.dbprofiler.model;

import java.util.Map;

public class NumericColumnStatistics {

    private final Double minimumValue;
    private final Double maximumValue;
    private final Integer totalValuesCount;
    private final Integer nullValuesCount;
    private final Map<Double, Integer> valuesByCount;

    public NumericColumnStatistics(Integer totalValuesCount, Integer nullValuesCount, Double minimumValue,
                                   Double maximumValue, Map<Double, Integer> valuesByCount) {
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.totalValuesCount = totalValuesCount;
        this.nullValuesCount = nullValuesCount;
        this.valuesByCount = valuesByCount;
    }
}