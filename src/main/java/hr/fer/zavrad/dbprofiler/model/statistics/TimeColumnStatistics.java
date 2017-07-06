package hr.fer.zavrad.dbprofiler.model.statistics;

import javafx.scene.chart.XYChart;

import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TimeColumnStatistics extends ColumnStatistics {

    private static final int DISTRIBUTION_CHART_INTERVALS = 20;

    private final Time minimumValue;
    private final Time maximumValue;
    private final Time mean;
    private final XYChart.Series recordCountData;
    private final Optional<XYChart.Series> patternInformationData;
    private final Optional<XYChart.Series> distributionData;
    private final Optional<List<Time>>  topTenPotWrongValues;

    public TimeColumnStatistics(Integer totalValuesCount, Integer nullValuesCount, Time minimumValue,
                                     Time maximumValue, Time mean, Long stdDev,
                                     Map<Time, Integer> valuesByCount) {

        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.mean = mean;

        long repeatingValues = valuesByCount.entrySet().stream().filter(e -> e.getValue() > 1).count();
        int repeatingValuesCount = valuesByCount.entrySet().stream().filter(e -> e.getValue() > 1).mapToInt(e -> e.getValue()).sum();

        recordCountData = new XYChart.Series();
        recordCountData.getData().add(new XYChart.Data("Null", nullValuesCount));
        recordCountData.getData().add(new XYChart.Data("Non Null", totalValuesCount - nullValuesCount));
        recordCountData.getData().add(new XYChart.Data("Unique", valuesByCount.size() - repeatingValues));
        recordCountData.getData().add(new XYChart.Data("Repeating", repeatingValuesCount));

        if(repeatingValues < 2) {
            this.patternInformationData = Optional.empty();
            this.distributionData = Optional.empty();
            this.topTenPotWrongValues = Optional.empty();
            return;
        }

        long threeSigma = stdDev * 3;
        List<Map.Entry<Time,Integer>> potentiallyWrongValues = valuesByCount.entrySet().stream()
                .filter(e -> Long.compare(e.getKey().getTime(), mean.getTime() + threeSigma) > 0 ||
                        Long.compare(e.getKey().getTime(), mean.getTime() - threeSigma) < 0)
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toList());


        if(potentiallyWrongValues.size() > 0) {
            recordCountData.getData().add(new XYChart.Data("Pot. Wrong",
                    potentiallyWrongValues.stream().mapToInt(Map.Entry<Time,Integer>::getValue).sum()));

            this.topTenPotWrongValues = Optional.of(potentiallyWrongValues.stream()
                    .limit(10)
                    .map(Map.Entry<Time,Integer>::getKey)
                    .collect(Collectors.toList()));
        } else {
            this.topTenPotWrongValues = Optional.empty();
        }

        List<XYChart.Data> patternInformationData = valuesByCount.keySet().stream()
                .sorted((t1,t2) -> Integer.compare(valuesByCount.get(t2), valuesByCount.get(t1)))
                .limit(10)
                .map(x -> new XYChart.Data(x.toString(), (int)valuesByCount.get(x)))
                .collect(Collectors.toList());

        List<XYChart.Data> distributionData = valuesByCount.keySet().stream()
                .sorted()
                .map(x -> new XYChart.Data(x.toString(), (int)valuesByCount.get(x)))
                .collect(Collectors.toList());

        XYChart.Series patternInformatonDataValues = new XYChart.Series();
        patternInformatonDataValues.getData().addAll(patternInformationData);

        XYChart.Series distributionDataValues = new XYChart.Series();
        distributionDataValues.getData().addAll(distributionData);

        this.patternInformationData = Optional.of(patternInformatonDataValues);

        if(distributionData.size() <= DISTRIBUTION_CHART_INTERVALS) {
            this.distributionData = Optional.of(distributionDataValues);
        } else {
            this.distributionData = Optional.empty();
        }
    }

    public Time getMinimumValue() { return minimumValue; }

    public Time getMaximumValue() {
        return maximumValue;
    }

    public Time getMean() {
        return mean;
    }

    public XYChart.Series getRecordCountData() {
        return recordCountData;
    }

    public Optional<XYChart.Series> getPatternInformationData() {
        return patternInformationData;
    }

    public Optional<XYChart.Series> getDistributionData() {
        return distributionData;
    }

    public Optional<List<Time>> getTopTenPotWrongValues() {
        return topTenPotWrongValues;
    }
}
