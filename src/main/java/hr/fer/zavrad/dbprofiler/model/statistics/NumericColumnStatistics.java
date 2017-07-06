package hr.fer.zavrad.dbprofiler.model.statistics;

import javafx.scene.chart.XYChart;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class NumericColumnStatistics extends ColumnStatistics {

    private static final int DISTRIBUTION_CHART_INTERVALS = 20;

    private final Double minimumValue;
    private final Double maximumValue;
    private final Double mean;
    private final Double stdDev;
    private final XYChart.Series recordCountData;
    private final Optional<XYChart.Series> patternInformationData;
    private final Optional<XYChart.Series> distributionData;
    private final Optional<List<Double>> topTenPotWrongValues;

    public NumericColumnStatistics(Integer totalValuesCount, Integer nullValuesCount, Double minimumValue,
                                   Double maximumValue, Map<Double, Integer> valuesByCount, Double mean, Double stdDev) {
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.mean = mean;
        this.stdDev = stdDev;

        long repeatingValues = valuesByCount.entrySet().stream().filter(e -> e.getValue() > 1).count();
        int repeatingValuesCount = valuesByCount.entrySet().stream().filter(e -> e.getValue() > 1).mapToInt(e -> e.getValue()).sum();

        recordCountData = new XYChart.Series();
        recordCountData.getData().add(new XYChart.Data("Null", nullValuesCount));
        recordCountData.getData().add(new XYChart.Data("Non Null", totalValuesCount - nullValuesCount));
        recordCountData.getData().add(new XYChart.Data("Unique", valuesByCount.size() - repeatingValues));
        recordCountData.getData().add(new XYChart.Data("Repeating", repeatingValuesCount));

        if(repeatingValues < 2) {
            patternInformationData = Optional.empty();
            distributionData = Optional.empty();
            topTenPotWrongValues = Optional.empty();
            return;
        }

        double threeSigma = stdDev * 3;
        List<Map.Entry<Double,Integer>> potentiallyWrongValues = valuesByCount.entrySet().stream()
                .filter(e -> Double.compare(e.getKey(), mean + threeSigma) > 0 ||
                             Double.compare(e.getKey(), mean - threeSigma) < 0)
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toList());

        if(potentiallyWrongValues.size() > 0) {
            recordCountData.getData().add(new XYChart.Data("Pot. Wrong",
                    potentiallyWrongValues.stream().mapToInt(Map.Entry<Double,Integer>::getValue).sum()));

            this.topTenPotWrongValues = Optional.of(potentiallyWrongValues.stream()
                    .limit(10)
                    .map(Map.Entry<Double,Integer>::getKey)
                    .collect(Collectors.toList()));
        } else {
            this.topTenPotWrongValues = Optional.empty();
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.###");

        List<XYChart.Data> patternInformationDataData = valuesByCount.keySet().stream()
                .sorted(new Comparator<Double>() {
                    @Override
                    public int compare(Double o1, Double o2) {
                        return Integer.compare(valuesByCount.get(o2), valuesByCount.get(o1));
                    }
                }).limit(10)
                .map(x -> new XYChart.Data(decimalFormat.format(x), (int)valuesByCount.get(x)))
                .collect(Collectors.toList());

        XYChart.Series patternInformatonDataValues = new XYChart.Series();
        patternInformatonDataValues.getData().addAll(patternInformationDataData);

        this.patternInformationData = Optional.of(patternInformatonDataValues);
        this.distributionData = Optional.of(calculateDistributionDataSeries(valuesByCount, minimumValue, maximumValue));
    }

    private static XYChart.Series calculateDistributionDataSeries(
            Map<Double, Integer> valuesByCount, double minimumValue, double maximumValue) {

        List<XYChart.Data> distributionData = null;

        if(Integer.compare(valuesByCount.size(), DISTRIBUTION_CHART_INTERVALS) <= 0) {
            distributionData = valuesByCount.keySet().stream()
                    .sorted()
                    .map(x -> new XYChart.Data(x.toString(), (int)valuesByCount.get(x)))
                    .collect(Collectors.toList());
        } else {

            double[] valuesPerInterval = new double[DISTRIBUTION_CHART_INTERVALS];
            double intervalLength = (maximumValue - minimumValue) / DISTRIBUTION_CHART_INTERVALS;

            for(Map.Entry<Double, Integer> entry : valuesByCount.entrySet()) {
                int arrayIndex = (int) ((entry.getKey() - minimumValue) / intervalLength);

                if(arrayIndex == DISTRIBUTION_CHART_INTERVALS) {
                    arrayIndex = DISTRIBUTION_CHART_INTERVALS - 1;
                }

                valuesPerInterval[arrayIndex] += entry.getValue();
            }

            distributionData = new ArrayList<>();

            for(int i = 0; i < DISTRIBUTION_CHART_INTERVALS; ++i) {

                String description = String.format("%.2f - %.2f",
                        i * intervalLength,
                        i < DISTRIBUTION_CHART_INTERVALS - 1 ? (i + 1) * intervalLength : maximumValue);

                distributionData.add(new XYChart.Data(description, valuesPerInterval[i]));
            }
        }

        XYChart.Series distributionDataValues = new XYChart.Series();
        distributionDataValues.getData().addAll(distributionData);

        return distributionDataValues;
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

    public Optional<List<Double>> getTopTenPotWrongValues() {
        return topTenPotWrongValues;
    }
}