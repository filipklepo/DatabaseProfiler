package hr.fer.zavrad.dbprofiler.model.statistics;

import hr.fer.zavrad.dbprofiler.model.statistics.ColumnStatistics;
import javafx.scene.chart.XYChart;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class NumericColumnStatistics extends ColumnStatistics {

    private final Double minimumValue;
    private final Double maximumValue;
    private final Double mean;
    private final Double stdDev;
    private final XYChart.Series recordCountData;
    private final Optional<XYChart.Series> patternInformationData;
    private final Optional<XYChart.Series> distributionData;

    public NumericColumnStatistics(Integer nullValuesCount, Double minimumValue,
                                   Double maximumValue, Map<Double, Integer> valuesByCount, Double mean, Double stdDev) {
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.mean = mean;
        this.stdDev = stdDev;

        long patternValuesCount = valuesByCount.entrySet().stream().filter(e -> e.getValue() > 1).count();

        recordCountData = new XYChart.Series();
        recordCountData.getData().add(new XYChart.Data("Null", nullValuesCount));
        recordCountData.getData().add(new XYChart.Data("Pattern", patternValuesCount));

        if(patternValuesCount < 5) {
            patternInformationData = Optional.empty();
            distributionData = Optional.empty();
            return;
        }

        double threeSigma = stdDev * 3;
        long potentiallyWrongValuesCount = valuesByCount.entrySet().stream()
                .filter(e -> Double.compare(e.getKey(), threeSigma) > 0).count();
        recordCountData.getData().add(new XYChart.Data("Pot. Wrong", potentiallyWrongValuesCount));

        List<XYChart.Data> patternInformationDataData = valuesByCount.keySet().stream().sorted(new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return Integer.compare(valuesByCount.get(o2), valuesByCount.get(o1));
            }
        }).limit(10)
                .map(x -> new XYChart.Data(x.toString(), (int)valuesByCount.get(x)))
                .collect(Collectors.toList());

        List<XYChart.Data> distributionData = valuesByCount.keySet().stream()
                .sorted()
                .map(x -> new XYChart.Data(x.toString(), (int)valuesByCount.get(x)))
                .collect(Collectors.toList());

        XYChart.Series patternInformatonDataValues = new XYChart.Series();
        patternInformatonDataValues.getData().addAll(patternInformationDataData);
        XYChart.Series distributionDataValues = new XYChart.Series();
        distributionDataValues.getData().addAll(distributionData);

        this.patternInformationData = Optional.of(patternInformatonDataValues);
        this.distributionData = Optional.of(distributionDataValues);
    }

    public Double getMinimumValue() {
        return minimumValue;
    }

    public Double getMaximumValue() {
        return maximumValue;
    }

    public Double getMean() { return mean; }

    public Double getStdDev() { return stdDev; }

    public XYChart.Series getRecordCountData() { return recordCountData; }

    public Optional<XYChart.Series> getPatternInformationData() { return patternInformationData; }

    public Optional<XYChart.Series> getDistributionData() {
        return distributionData;
    }
}