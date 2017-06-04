package hr.fer.zavrad.dbprofiler.model;

import javafx.scene.chart.XYChart;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TimestampColumnStatistics extends ColumnStatistics {

    private final Timestamp minimumValue;
    private final Timestamp maximumValue;
    private final Timestamp mean;
    private final XYChart.Series recordCountData;
    private final Optional<XYChart.Series> patternInformationData;

    public TimestampColumnStatistics(Integer nullValuesCount, Timestamp minimumValue,
                                     Timestamp maximumValue, Timestamp mean, Long stdDev,
                                     Map<Timestamp, Integer> valuesByCount) {

        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.mean = mean;

        long patternValuesCount = valuesByCount.entrySet().stream().filter(e -> e.getValue() > 1).count();

        recordCountData = new XYChart.Series();
        recordCountData.getData().add(new XYChart.Data("Null", nullValuesCount));
        recordCountData.getData().add(new XYChart.Data("Pattern", patternValuesCount));

        if(patternValuesCount < 5) {
            patternInformationData = Optional.empty();
            return;
        }

        long threeSigma = stdDev * 3;
        long potentiallyWrongValuesCount = valuesByCount.entrySet().stream()
                .filter(e -> Double.compare(e.getKey().getTime(), threeSigma) > 0).count();
        recordCountData.getData().add(new XYChart.Data("Pot. Wrong", potentiallyWrongValuesCount));

        List<XYChart.Data> data = valuesByCount.keySet().stream().sorted(new Comparator<Timestamp>() {
            @Override
            public int compare(Timestamp t1, Timestamp t2) {
                return Integer.compare(valuesByCount.get(t2), valuesByCount.get(t1));
            }
        }).limit(10).map(x -> new XYChart.Data(x.toString(), (int)valuesByCount.get(x))).collect(Collectors.toList());

        XYChart.Series patternInformatonDataValues = new XYChart.Series();
        patternInformatonDataValues.getData().addAll(data);

        this.patternInformationData = Optional.of(patternInformatonDataValues);
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
}
