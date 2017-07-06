package hr.fer.zavrad.dbprofiler.model.statistics;

import javafx.scene.chart.XYChart;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TimestampColumnStatistics extends ColumnStatistics {

    private static final int DISTRIBUTION_CHART_INTERVALS = 20;

    private final Timestamp minimumValue;
    private final Timestamp maximumValue;
    private final Timestamp mean;
    private final XYChart.Series recordCountData;
    private final Optional<XYChart.Series> patternInformationData;
    private final Optional<XYChart.Series> distributionData;
    private final Optional<List<Timestamp>> topTenPotWrongValues;

    public TimestampColumnStatistics(Integer totalValuesCount, Integer nullValuesCount, Timestamp minimumValue,
                                     Timestamp maximumValue, Timestamp mean, Long stdDev,
                                     Map<Timestamp, Integer> valuesByCount) {

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
        List<Map.Entry<Timestamp,Integer>> potentiallyWrongValues = valuesByCount.entrySet().stream()
                .filter(e -> Long.compare(e.getKey().getTime(), mean.getTime() + threeSigma) > 0 ||
                        Long.compare(e.getKey().getTime(), mean.getTime() - threeSigma) < 0)
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toList());

        if(potentiallyWrongValues.size() > 0) {
            recordCountData.getData().add(new XYChart.Data("Pot. Wrong",
                    potentiallyWrongValues.stream().mapToInt(Map.Entry<Timestamp,Integer>::getValue).sum()));

            this.topTenPotWrongValues = Optional.of(potentiallyWrongValues.stream()
                    .limit(10)
                    .map(Map.Entry<Timestamp,Integer>::getKey)
                    .collect(Collectors.toList()));
        } else {
            this.topTenPotWrongValues = Optional.empty();
        }

        List<XYChart.Data> data = valuesByCount.keySet().stream().sorted(new Comparator<Timestamp>() {
            @Override
            public int compare(Timestamp t1, Timestamp t2) {
                return Integer.compare(valuesByCount.get(t2), valuesByCount.get(t1));
            }
        }).limit(10).map(x -> new XYChart.Data(x.toString(), (int)valuesByCount.get(x))).collect(Collectors.toList());

        List<XYChart.Data> distributionData = valuesByCount.keySet().stream()
                .sorted()
                .map(x -> new XYChart.Data(x.toString(), (int)valuesByCount.get(x)))
                .collect(Collectors.toList());

        XYChart.Series patternInformatonDataValues = new XYChart.Series();
        patternInformatonDataValues.getData().addAll(data);
        XYChart.Series distributionDataValues = new XYChart.Series();
        distributionDataValues.getData().addAll(distributionData);

        this.patternInformationData = Optional.of(patternInformatonDataValues);

        if(distributionData.size() <= DISTRIBUTION_CHART_INTERVALS) {
            this.distributionData = Optional.of(distributionDataValues);
        } else {
            this.distributionData = Optional.empty();
        }
    }

    public Timestamp getMinimumValue() {
        return minimumValue;
    }

    public Timestamp getMaximumValue() {
        return maximumValue;
    }

    public Timestamp getMean() {
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

    public Optional<List<Timestamp>> getTopTenPotWrongValues() {
        return topTenPotWrongValues;
    }
}
