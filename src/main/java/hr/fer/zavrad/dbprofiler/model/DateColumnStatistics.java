package hr.fer.zavrad.dbprofiler.model;

import javafx.scene.chart.XYChart;

import java.sql.Date;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DateColumnStatistics extends ColumnStatistics {

    private final Date minimumValue;
    private final Date maximumValue;
    private final Date mean;
    private final XYChart.Series recordCountData;
    private final Optional<XYChart.Series> patternInformationData;

    public DateColumnStatistics(Integer nullValuesCount, Date minimumValue, Date maximumValue,
                                Date mean, Long stdDev, Map<Date, Integer> valuesByCount) {

        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.mean = mean;

        long patternValuesCount = valuesByCount.entrySet().stream().filter(e -> e.getValue() > 1).count();

        recordCountData = new XYChart.Series();
        recordCountData.getData().add(new XYChart.Data("Null", nullValuesCount));
        recordCountData.getData().add(new XYChart.Data("Distinct", patternValuesCount));

        if(patternValuesCount < 5) {
            patternInformationData = Optional.empty();
            return;
        }

        long threeSigma = stdDev * 3;
        long potentiallyWrongValuesCount = valuesByCount.entrySet().stream()
                .filter(e -> Double.compare(e.getKey().getTime(), threeSigma) > 0).count();
        recordCountData.getData().add(new XYChart.Data("Pot. Wrong", potentiallyWrongValuesCount));

        List<XYChart.Data> data = valuesByCount.keySet().stream().sorted(new Comparator<Date>() {
            @Override
            public int compare(Date d1, Date d2) {
                return Integer.compare(valuesByCount.get(d2), valuesByCount.get(d1));
            }
        }).limit(10).map(x -> new XYChart.Data(x.toString(), (int)valuesByCount.get(x))).collect(Collectors.toList());

        XYChart.Series patternInformatonDataValues = new XYChart.Series();
        patternInformatonDataValues.getData().addAll(data);

        this.patternInformationData = Optional.of(patternInformatonDataValues);
    }

    public Date getMinimumValue() {
        return minimumValue;
    }

    public Date getMaximumValue() {
        return maximumValue;
    }

    public Date getMean() {
        return mean;
    }

    public XYChart.Series getRecordCountData() {
        return recordCountData;
    }

    public Optional<XYChart.Series> getPatternInformationData() {
        return patternInformationData;
    }
}
