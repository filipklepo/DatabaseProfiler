package hr.fer.zavrad.dbprofiler.model.statistics;

import hr.fer.zavrad.dbprofiler.model.statistics.ColumnStatistics;
import javafx.scene.chart.XYChart;

public class GenericColumnStatistics extends ColumnStatistics {

    private final XYChart.Series recordCountData;

    public GenericColumnStatistics(Integer totalValuesCount, Integer nullValuesCount) {
        recordCountData = new XYChart.Series();
        recordCountData.getData().add(new XYChart.Data("Null", nullValuesCount));
        recordCountData.getData().add(new XYChart.Data("Non Null", totalValuesCount - nullValuesCount));
    }

    public XYChart.Series getRecordCountData() {
        return recordCountData;
    }
}
