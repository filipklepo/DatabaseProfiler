package hr.fer.zavrad.dbprofiler.controller;

import hr.fer.zavrad.dbprofiler.model.NumericColumnStatistics;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;

public class NumericColumnStatisticsController {

    @FXML
    private BarChart bcPatternInformation;
    @FXML
    private BarChart bcRecordCount;
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

    private final NumericColumnStatistics statistics;

    public NumericColumnStatisticsController(NumericColumnStatistics statistics) {
        this.statistics = statistics;
    }

    public void initialize() {
        lblMinValue.setText(statistics.getMinimumValue().toString());
        lblMaxValue.setText(statistics.getMaximumValue().toString());

        bcRecordCount.getData().addAll(statistics.getRecordCountData());
        bcRecordCount.setTitle("Record Count");

        if(!statistics.getPatternInformationData().isPresent()) {
            bcPatternInformation.visibleProperty().setValue(false);
            lblMean.setText("");
            lblStdDev.setText("");
            return;
        }

        bcPatternInformation.getData().addAll(statistics.getPatternInformationData().get());
        bcPatternInformation.setTitle("Pattern information");

        lblMeanValue.setText(String.format("%.3f", statistics.getMean()));
        lblStdDevValue.setText(String.format("%.3f", statistics.getStdDev()));
    }
}
