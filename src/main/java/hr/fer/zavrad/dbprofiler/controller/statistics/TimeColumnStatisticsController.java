package hr.fer.zavrad.dbprofiler.controller.statistics;

import hr.fer.zavrad.dbprofiler.model.statistics.TimeColumnStatistics;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class TimeColumnStatisticsController {

    @FXML
    private BarChart bcPatternInformation;
    @FXML
    private BarChart bcRecordCount;
    @FXML
    private Label lblMinValue;
    @FXML
    private Label lblMaxValue;
    @FXML
    private Label lblStdDev;
    @FXML
    private Label lblStdDevValue;
    @FXML
    private Label lblMeanValue;
    @FXML
    private LineChart lcDistribution;
    @FXML
    private Label lblPotWrongValues;
    @FXML
    private ListView lvPotWrongValues;

    private final TimeColumnStatistics statistics;

    public TimeColumnStatisticsController(TimeColumnStatistics statistics) {
        this.statistics = statistics;
    }

    public void initialize() {
        lblMinValue.setText(statistics.getMinimumValue().toString());
        lblMaxValue.setText(statistics.getMaximumValue().toString());

        bcRecordCount.getData().addAll(statistics.getRecordCountData());
        bcRecordCount.setTitle("Record Count");
        lblMeanValue.setText(statistics.getMean().toString());
        lblStdDev.setText("");

        lblPotWrongValues.visibleProperty().setValue(false);
        lvPotWrongValues.visibleProperty().setValue(false);
        lcDistribution.visibleProperty().setValue(false);

        if(!statistics.getPatternInformationData().isPresent()) {
            bcPatternInformation.visibleProperty().setValue(false);
            return;
        }

        bcPatternInformation.getData().addAll(statistics.getPatternInformationData().get());
        bcPatternInformation.setTitle("Top 10 values by occurrences");

        statistics.getDistributionData().ifPresent(d -> {
                lcDistribution.getData().addAll(d);
                lcDistribution.visibleProperty().setValue(true);
        });
        lcDistribution.setTitle("Distribution");

        if(statistics.getTopTenPotWrongValues().isPresent()) {
            lblPotWrongValues.visibleProperty().setValue(true);
            lvPotWrongValues.visibleProperty().setValue(true);

            lvPotWrongValues.setItems(FXCollections.observableArrayList(statistics.getTopTenPotWrongValues().get()));
        }
    }
}
