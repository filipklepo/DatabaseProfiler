package hr.fer.zavrad.dbprofiler.model.statistics;

import javafx.scene.chart.XYChart;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TextualColumnStatistics extends ColumnStatistics {

    private static final Integer MAX_SHOW_DISTRIBUTION_TEXT_LENGTH = 15;

    private final Integer minimumLength;
    private final Integer maximumLength;
    private final Double averageLength;
    private final XYChart.Series recordCountData;
    private final Optional<XYChart.Series> patternInformationData;

    public TextualColumnStatistics(Integer totalValuesCount, Integer nullValuesCount, Integer minimumLength,
                                   Integer maximumLength, Double averageLength, Map<String, Integer> valuesByCount) {

        this.minimumLength = minimumLength;
        this.maximumLength = maximumLength;
        this.averageLength = averageLength;

        List<Map.Entry<String,Integer>>  repeatingValues = valuesByCount.entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .collect(Collectors.toList());
        int repeatingValuesCount = repeatingValues.stream().mapToInt(e -> e.getValue()).sum();

        recordCountData = new XYChart.Series();
        recordCountData.getData().add(new XYChart.Data("Null", nullValuesCount));
        recordCountData.getData().add(new XYChart.Data("Non Null", totalValuesCount - nullValuesCount));
        recordCountData.getData().add(new XYChart.Data("Unique", valuesByCount.size() - repeatingValues.size()));
        recordCountData.getData().add(new XYChart.Data("Repeating", repeatingValuesCount));

        if(repeatingValues.size() < 2 ||
                maximumLength > MAX_SHOW_DISTRIBUTION_TEXT_LENGTH) {

            patternInformationData = Optional.empty();
            return;
        }

        List<XYChart.Data> patternInformationData = valuesByCount.keySet().stream().sorted(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return Integer.compare(valuesByCount.get(s2), valuesByCount.get(s1));
            }
        }).limit(10).map(x -> new XYChart.Data(x, (int)valuesByCount.get(x))).collect(Collectors.toList());

        XYChart.Series patternInformatonDataValues = new XYChart.Series();
        patternInformatonDataValues.getData().addAll(patternInformationData);

        this.patternInformationData = Optional.of(patternInformatonDataValues);
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
