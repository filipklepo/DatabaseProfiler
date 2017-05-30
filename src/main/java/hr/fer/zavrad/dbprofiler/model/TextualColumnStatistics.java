package hr.fer.zavrad.dbprofiler.model;

import javafx.scene.chart.XYChart;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TextualColumnStatistics extends ColumnStatistics {

    private static final Integer MIN_SHOW_DISTRIBUTION_PATTERN_COUNT = 5;
    private static final Integer MAX_SHOW_DISTRIBUTION_TEXT_LENGTH = 15;

    private final Integer totalValuesCount;
    private final Integer nullValuesCount;
    private final Long patternValuesCount;
    private final Integer minimumLength;
    private final Integer maximumLength;
    private final Double averageLength;
    private final XYChart.Series recordCountData;
    private final Optional<XYChart.Series> patternInformationData;

    public TextualColumnStatistics(Integer totalValues, Integer nullValues, Integer minimumLength,
                                   Integer maximumLength, Double averageLength, Map<String, Integer> valuesByCount) {
        this.totalValuesCount = totalValues;
        this.nullValuesCount = nullValues;

        this.minimumLength = minimumLength;
        this.maximumLength = maximumLength;
        this.averageLength = averageLength;

        this.patternValuesCount = valuesByCount.entrySet().stream().filter(e -> e.getValue() > 1).count();

        this.recordCountData = new XYChart.Series();
        recordCountData.getData().add(new XYChart.Data("Total", totalValuesCount));
        recordCountData.getData().add(new XYChart.Data("Null", nullValuesCount));
        recordCountData.getData().add(new XYChart.Data("Pattern", patternValuesCount));

        if(patternValuesCount < MIN_SHOW_DISTRIBUTION_PATTERN_COUNT ||
                maximumLength > MAX_SHOW_DISTRIBUTION_TEXT_LENGTH) {
            patternInformationData = Optional.empty();
            return;
        }


        List<XYChart.Data> data = valuesByCount.keySet().stream().sorted(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return Integer.compare(valuesByCount.get(s2), valuesByCount.get(s1));
            }
        }).limit(10).map(x -> new XYChart.Data(x, (int)valuesByCount.get(x))).collect(Collectors.toList());

        XYChart.Series patternInformatonDataValues = new XYChart.Series();
        patternInformatonDataValues.getData().addAll(data);

        patternInformationData = Optional.of(patternInformatonDataValues);
    }

    public Integer getTotalValuesCount() {
        return totalValuesCount;
    }

    public Integer getNullValuesCount() {
        return nullValuesCount;
    }

    public Long getPatternValuesCount() {
        return patternValuesCount;
    }

    public Integer getMinimumLength() {
        return minimumLength;
    }

    public Integer getMaximumLength() {
        return maximumLength;
    }

    public Double getAverageLength() {
        return averageLength;
    }

    public XYChart.Series getRecordCountData() {
        return recordCountData;
    }

    public Optional<XYChart.Series> getPatternInformationData() {
        return patternInformationData;
    }
}
