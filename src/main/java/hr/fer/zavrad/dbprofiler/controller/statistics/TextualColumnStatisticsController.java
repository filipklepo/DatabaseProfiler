package hr.fer.zavrad.dbprofiler.controller;

import hr.fer.zavrad.dbprofiler.model.statistics.TextualColumnStatistics;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Label;

public class TextualColumnStatisticsController {

    @FXML
    private BarChart bcPatternInformation;
    @FXML
    private BarChart bcRecordCount;
    @FXML
    private Label lblMinLength;
    @FXML
    private Label lblMaxLength;
    @FXML
    private Label lblAverageLength;

    private final TextualColumnStatistics statistics;

    public TextualColumnStatisticsController(TextualColumnStatistics statistics) {
        this.statistics = statistics;
    }

    public void initialize() {
        bcRecordCount.getData().addAll(statistics.getRecordCountData());
        bcRecordCount.setTitle("Record Count");

        lblMinLength.setText(statistics.getMinimumLength().toString());
        lblMaxLength.setText(statistics.getMaximumLength().toString());
        lblAverageLength.setText(statistics.getAverageLength().toString());

        if(!statistics.getPatternInformationData().isPresent()) {
            bcPatternInformation.visibleProperty().setValue(false);
            return;
        }

        bcPatternInformation.getData().addAll(statistics.getPatternInformationData().get());
        bcPatternInformation.setTitle("Top 10 values by occurrences");

    }
}
