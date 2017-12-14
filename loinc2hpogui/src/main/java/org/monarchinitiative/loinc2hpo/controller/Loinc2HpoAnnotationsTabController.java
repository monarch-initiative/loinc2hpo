package org.monarchinitiative.loinc2hpo.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.loinc.AnnotatedLoincRangeTest;
import org.monarchinitiative.loinc2hpo.model.Model;

import java.util.Map;


@Singleton
public class Loinc2HpoAnnotationsTabController {
    private static final Logger logger = LogManager.getLogger();
    private Model model = null;
    /** Reference to the second tab. When the user adds a new annotation, we update the table, therefore, we need a reference. */
    @Inject
    private AnnotateTabController annotateTabController;

    /** This is the message users will see if they open the analysis tab before they have entered the genes
     * and started the analysis of the viewpoints. */
    private static final String INITIAL_HTML_CONTENT = "<html><body><h3>LOINC2HPO Biocuration APp</h3><p>ToDo " +
            "output some interesting things here.</p></body></html>";

    private WebEngine contentWebEngine;




    @FXML
    private TableView<AnnotatedLoincRangeTest> loincTableView;
    @FXML private TableColumn<AnnotatedLoincRangeTest,String> loincNumberColumn;
    @FXML private TableColumn<AnnotatedLoincRangeTest,String> belowNormalHpoColumn;
    @FXML private TableColumn<AnnotatedLoincRangeTest,String> notAbnormalHpoColumn;
    @FXML private TableColumn<AnnotatedLoincRangeTest,String> aboveNormalHpoColumn;

    @FXML private WebView webview;

    public void setModel(Model m) {
        model = m;
    }

    @FXML
    private void initialize() {
        logger.trace("Calling initialize");
        loincTableView.setEditable(false);
        loincNumberColumn.setSortable(true);
        loincNumberColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getLoincNumber()));
        belowNormalHpoColumn.setSortable(true);
        belowNormalHpoColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getBelowNormalHpoTermName()));
        notAbnormalHpoColumn.setSortable(true);
        notAbnormalHpoColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getNotAbnormalHpoTermName()));
        aboveNormalHpoColumn.setSortable(true);
        aboveNormalHpoColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getAboveNormalHpoTermName()));


//        this.contentWebEngine = webview.getEngine();
//        this.contentWebEngine.loadContent(INITIAL_HTML_CONTENT);
    }



    public void setData(String html) {

    }





    public void refreshTable() {
        Map<String,AnnotatedLoincRangeTest> testmap = model.getTestmap();
        Platform.runLater(() -> {
            loincTableView.getItems().clear();
            loincTableView.getItems().addAll(testmap.values());
        });


    }


}
