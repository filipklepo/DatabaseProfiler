package hr.fer.zavrad.dbprofiler.controller;

import hr.fer.zavrad.dbprofiler.model.statistics.BitColumnStatistics;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;

public class BitColumnStatisticsController {

    @FXML
    private BarChart bcRecordCount;
    @FXML
    private PieChart pcDistribution;

    private final BitColumnStatistics statistics;

    public BitColumnStatisticsController(BitColumnStatistics statistics) {
        this.statistics = statistics;
    }

    public void initialize() {
        bcRecordCount.setTitle("Record Count");
        pcDistribution.setTitle("Distribution");
        bcRecordCount.getData().add(statistics.getRecordCountData());
        pcDistribution.getData().addAll(statistics.getDistributionData());
    }
}
