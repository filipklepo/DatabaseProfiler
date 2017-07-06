package hr.fer.zavrad.dbprofiler.controller.statistics;

import hr.fer.zavrad.dbprofiler.model.statistics.NumericColumnStatistics;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.text.DecimalFormat;

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
    @FXML
    private LineChart lcDistribution;
    @FXML
    private Label lblPotWrongValues;
    @FXML
    private ListView lvPotWrongValues;

    private final NumericColumnStatistics statistics;

    public NumericColumnStatisticsController(NumericColumnStatistics statistics) {
        this.statistics = statistics;
    }

    public void initialize() {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        lblMinValue.setText(decimalFormat.format(statistics.getMinimumValue()));
        lblMaxValue.setText(decimalFormat.format(statistics.getMaximumValue()));

        bcRecordCount.getData().addAll(statistics.getRecordCountData());
        bcRecordCount.setTitle("Record Count");

        lblPotWrongValues.visibleProperty().setValue(false);
        lvPotWrongValues.visibleProperty().setValue(false);

        if(!statistics.getPatternInformationData().isPresent()) {
            bcPatternInformation.visibleProperty().setValue(false);
            lcDistribution.visibleProperty().setValue(false);
            lblMean.visibleProperty().setValue(false);
            lblStdDev.visibleProperty().setValue(false);
            return;
        }

        bcPatternInformation.getData().addAll(statistics.getPatternInformationData().get());
        bcPatternInformation.setTitle("Top 10 values by occurrences");

        lcDistribution.setTitle("Distribution");
        lcDistribution.getData().addAll(statistics.getDistributionData().get());

        lblMeanValue.setText(String.format("%.3f", statistics.getMean()));
        lblStdDevValue.setText(String.format("%.3f", statistics.getStdDev()));

        if(statistics.getTopTenPotWrongValues().isPresent()) {

            lblPotWrongValues.visibleProperty().setValue(true);
            lvPotWrongValues.visibleProperty().setValue(true);

            statistics.getTopTenPotWrongValues().get().stream()
                    .forEach(v -> lvPotWrongValues.getItems().add(decimalFormat.format(v)));
        }
    }
}
