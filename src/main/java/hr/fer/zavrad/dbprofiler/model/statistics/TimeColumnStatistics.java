package hr.fer.zavrad.dbprofiler.model.statistics;

import hr.fer.zavrad.dbprofiler.model.statistics.ColumnStatistics;
import javafx.scene.chart.XYChart;

import java.sql.Time;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TimeColumnStatistics extends ColumnStatistics {

    private final Time minimumValue;
    private final Time maximumValue;
    private final Time mean;
    private final XYChart.Series recordCountData;
    private final Optional<XYChart.Series> patternInformationData;

    public TimeColumnStatistics(Integer nullValuesCount, Time minimumValue,
                                     Time maximumValue, Time mean, Long stdDev,
                                     Map<Time, Integer> valuesByCount) {

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

        List<XYChart.Data> data = valuesByCount.keySet().stream().sorted(new Comparator<Time>() {
            @Override
            public int compare(Time t1, Time t2) {
                return Integer.compare(valuesByCount.get(t2), valuesByCount.get(t1));
            }
        }).limit(10).map(x -> new XYChart.Data(x.toString(), (int)valuesByCount.get(x))).collect(Collectors.toList());

        XYChart.Series patternInformatonDataValues = new XYChart.Series();
        patternInformatonDataValues.getData().addAll(data);

        this.patternInformationData = Optional.of(patternInformatonDataValues);
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
}
