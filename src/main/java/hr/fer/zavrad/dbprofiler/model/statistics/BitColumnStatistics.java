package hr.fer.zavrad.dbprofiler.model;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

public class BitColumnStatistics extends ColumnStatistics {

    private final XYChart.Series recordCountData;
    private final ObservableList<PieChart.Data> distributionData;

    public BitColumnStatistics(XYChart.Series recordCountData, ObservableList<PieChart.Data> pieChartData) {
        this.recordCountData = recordCountData;
        this.distributionData = pieChartData;
    }

    public XYChart.Series getRecordCountData() {
        return recordCountData;
    }

    public ObservableList<PieChart.Data> getDistributionData() {
        return distributionData;
    }
}
