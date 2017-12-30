package org.monarchinitiative.loinc2hpo.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.loinc.AnnotatedLoincRangeTest;
import org.monarchinitiative.loinc2hpo.model.Model;
import org.monarchinitiative.loinc2hpo.util.HPO_Class_Found;

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
    private static final String INITIAL_HTML_CONTENT = "<h3>LOINC2HPO Biocuration App</h3>";

   // private WebEngine contentWebEngine;

    @FXML private VBox vbox4wv;




    @FXML
    private TableView<AnnotatedLoincRangeTest> loincTableView;
    @FXML private TableColumn<AnnotatedLoincRangeTest,String> loincNumberColumn;
    @FXML private TableColumn<AnnotatedLoincRangeTest,String> belowNormalHpoColumn;
    @FXML private TableColumn<AnnotatedLoincRangeTest,String> notAbnormalHpoColumn;
    @FXML private TableColumn<AnnotatedLoincRangeTest,String> aboveNormalHpoColumn;



    //@FXML private WebView wview;

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
        updateSummary();

    }


    public void updateSummary() {
        Platform.runLater(()->{
            WebView wview = new WebView();
            WebEngine contentWebEngine = wview.getEngine();
            contentWebEngine.loadContent(getHTML());
            this.vbox4wv.getChildren().addAll(wview);
        });
    }



    private  String getHTML() {
        String html = "<html><body>\n" +
                inlineCSS() +
                "<h1>LOINC2HPO Biocuration: Summary</h1>";
        if (model!=null) {
            return html + getLoincAnnotationData() + "</body></html>";
        } else {
            return html + "</body></html>";
        }


    }


    private String getLoincAnnotationData() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("<li>Number of HPO Terms: %d</li>",model.getOntologyTermCount()));
        sb.append(String.format("<li>Number of annotation LOINC codes: %d</li>",model.getLoincAnnotationCount()));
        return String.format("<ul>%s</ul>",sb.toString());
    }


    private static String inlineCSS() {
        return "<head><style>\n" +
                "  html { margin: 0; padding: 0; }" +
                "body { font: 100% georgia, sans-serif; line-height: 1.88889;color: #001f3f; margin: 10; padding: 10; }"+
                "p { margin-top: 0;text-align: justify;}"+
                "h2,h3 {font-family: 'serif';font-size: 1.4em;font-style: normal;font-weight: bold;"+
                "letter-spacing: 1px; margin-bottom: 0; color: #001f3f;}"+
                "  </style></head>";
    }



    public void setData(String html) {
        WebView wview = new WebView();
        WebEngine contentWebEngine = wview.getEngine();
        contentWebEngine.loadContent(html);
        this.vbox4wv.getChildren().addAll(wview);
    }





    public void refreshTable() {
        Map<String,AnnotatedLoincRangeTest> testmap = model.getTestmap();
        Platform.runLater(() -> {
            loincTableView.getItems().clear();
            loincTableView.getItems().addAll(testmap.values());
        });


    }


}
