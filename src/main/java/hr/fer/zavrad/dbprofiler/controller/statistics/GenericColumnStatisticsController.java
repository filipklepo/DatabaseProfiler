package hr.fer.zavrad.dbprofiler.controller.statistics;

import hr.fer.zavrad.dbprofiler.model.statistics.GenericColumnStatistics;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class GenericColumnStatisticsController {

    @FXML
    private BarChart bcPatternInformation;
    @FXML
    private BarChart bcRecordCount;
    @FXML
    private LineChart lcDistribution;
    @FXML
    private Label lblMin;
    @FXML
    private Label lblMax;
    @FXML
    private Label lblMinValue;
    @FXML
    private Label lblMaxValue;
    @FXML
    private Label lblMean;
    @FXML
    private Label lblStdDev;
    @FXML
    private Label lblMeanValue;
    @FXML
    private Label lblStdDevValue;
    @FXML
    private Label lblPotWrongValues;
    @FXML
    private ListView lvPotWrongValues;

    private final GenericColumnStatistics statistics;

    public GenericColumnStatisticsController(GenericColumnStatistics statistics) {
        this.statistics = statistics;
    }

    public void initialize() {
        bcRecordCount.getData().addAll(statistics.getRecordCountData());
        bcRecordCount.setTitle("Record Count");

        lblMin.setText("");
        lblMax.setText("");
        lblMinValue.setText("");
        lblMaxValue.setText("");
        lblMean.setText("");
        lblStdDev.setText("");
        lblMeanValue.setText("");
        lblStdDevValue.setText("");
        lblPotWrongValues.setText("");
        lvPotWrongValues.visibleProperty().setValue(false);

        bcPatternInformation.visibleProperty().setValue(false);
        lcDistribution.visibleProperty().setValue(false);
    }
}
