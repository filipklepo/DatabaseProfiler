package hr.fer.zavrad.dbprofiler.model;

import javafx.scene.chart.XYChart;

public class GenericColumnStatistics extends ColumnStatistics{

    private final XYChart.Series recordCountData;

    public GenericColumnStatistics(Integer totalValuesCount, Integer nullValuesCount) {
        recordCountData = new XYChart.Series();
        recordCountData.getData().add(new XYChart.Data("Total", totalValuesCount));
        recordCountData.getData().add(new XYChart.Data("Null", nullValuesCount));
    }

    public XYChart.Series getRecordCountData() {
        return recordCountData;
    }
}
