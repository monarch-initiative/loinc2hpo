package org.monarchinitiative.loinc2hpo.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.Constants;
import org.monarchinitiative.loinc2hpo.gui.PopUps;
import org.monarchinitiative.loinc2hpo.io.LoincAnnotationSerializationFactory;
import org.monarchinitiative.loinc2hpo.loinc.*;
import org.monarchinitiative.loinc2hpo.model.AppResources;
import org.monarchinitiative.loinc2hpo.model.AppTempData;
import org.monarchinitiative.loinc2hpo.io.LoincAnnotationSerializationFactory.SerializationFormat;
import org.monarchinitiative.loinc2hpo.util.AnnotationQC;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


@Singleton
public class Loinc2HpoAnnotationsTabController {
    private static final Logger logger = LogManager.getLogger();
    private AppTempData appTempData = null;
    /** Reference to the second tab. When the user adds a new annotation, we update the table, therefore, we need a reference. */
    @Inject
    private AnnotateTabController annotateTabController;
    @Inject
    private MainController mainController;

    private AppResources appResources;

    /** This is the message users will see if they open the analysis tab before they have entered the genes
     * and started the analysis of the viewpoints. */
    private static final String INITIAL_HTML_CONTENT = "<h3>LOINC2HPO Biocuration App</h3>";

   // private WebEngine contentWebEngine;

    @FXML private VBox vbox4wv;

    @FXML
    private TableView<LOINC2HpoAnnotationImpl> loincAnnotationTableView;
    @FXML private TableColumn<LOINC2HpoAnnotationImpl,String> loincNumberColumn;
    @FXML private TableColumn<LOINC2HpoAnnotationImpl,String> belowNormalHpoColumn;
    @FXML private TableColumn<LOINC2HpoAnnotationImpl,String> notAbnormalHpoColumn;
    @FXML private TableColumn<LOINC2HpoAnnotationImpl,String> aboveNormalHpoColumn;
    @FXML private TableColumn<LOINC2HpoAnnotationImpl, String> loincScaleColumn;
    @FXML private TableColumn<LOINC2HpoAnnotationImpl, String> loincFlagColumn;
    @FXML private TableColumn<LOINC2HpoAnnotationImpl, String> noteColumn;



    //@FXML private WebView wview;

    public void setAppTempData(AppTempData m) {
        appTempData = m;
    }

    public void setAppResources(AppResources appResources) {
        this.appResources = appResources;
        initializeTable();
        refreshTable();
    }

    public void initializeTable() {
logger.trace("Loinc2HpoAnnotationsTabController initialize() called");
        Map<TermId, Term> termMap = appResources.getTermidTermMap();
        loincAnnotationTableView.setEditable(false);
        loincNumberColumn.setSortable(true);
        loincNumberColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getLoincId().toString()));
        loincScaleColumn.setSortable(true);
        loincScaleColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getLoincScale().toString()));
        belowNormalHpoColumn.setSortable(true);
        //belowNormalHpoColumn.setCellValueFactory(cdf -> cdf.getValue().whenValueLow() == null ? new ReadOnlyStringWrapper("\" \"") : new ReadOnlyStringWrapper(termMap.get(cdf.getValue().whenValueLow()).getName()));
        belowNormalHpoColumn.setCellValueFactory(cdf -> {
            TermId termId = cdf.getValue().whenValueLow();
            if (termId == null) { //no annotation for low
                return new ReadOnlyStringWrapper("\" \"");
            } else if (!termMap.containsKey(termId)) { //annotation termid not found in current hpo
                return new ReadOnlyStringWrapper(termId.getValue());
            } else { //show term name
                return new ReadOnlyStringWrapper(termMap.get(termId).getName());
            }
        });
        notAbnormalHpoColumn.setSortable(true);
        //notAbnormalHpoColumn.setCellValueFactory(cdf -> cdf.getValue().whenValueNormalOrNegative() == null ? new ReadOnlyStringWrapper("\" \"")
        //        : new ReadOnlyStringWrapper(cdf.getValue().whenValueNormalOrNegative().getName()));
        notAbnormalHpoColumn.setCellValueFactory(cdf -> {
            TermId termId = cdf.getValue().whenValueNormalOrNegative();
            if (termId == null) { //no annotation
                return new ReadOnlyStringWrapper("\" \"");
            } else if (!termMap.containsKey(termId)){//previously annotated with a term not found in current hpo
                    return new ReadOnlyStringWrapper(termId.getValue());
            } else { //annotated with a term present in current hpo
                    return new ReadOnlyStringWrapper(termMap.get(termId).getName());
            }
        });
        aboveNormalHpoColumn.setSortable(true);
//        aboveNormalHpoColumn.setCellValueFactory(cdf -> cdf.getValue().whenValueHighOrPositive() == null ? new ReadOnlyStringWrapper("\" \"")
//        : new ReadOnlyStringWrapper(cdf.getValue().whenValueHighOrPositive().getName()));
        aboveNormalHpoColumn.setCellValueFactory(cdf -> {
            TermId termId = cdf.getValue().whenValueHighOrPositive();
            if (termId == null) { //no annotation
                return new ReadOnlyStringWrapper("\" \"");
            } else if (!termMap.containsKey(termId)){//previously annotated with a term not found in current hpo
                return new ReadOnlyStringWrapper(termId.getValue());
            } else { //annotated with a term present in current hpo
                return new ReadOnlyStringWrapper(termMap.get(termId).getName());
            }
        });
        loincFlagColumn.setSortable(true);
        loincFlagColumn.setCellValueFactory(cdf -> cdf.getValue() != null && cdf.getValue().getFlag() ?
                new ReadOnlyStringWrapper("Y") : new ReadOnlyStringWrapper(""));
        noteColumn.setSortable(true);
        noteColumn.setCellValueFactory(cdf -> cdf.getValue() == null ? new ReadOnlyStringWrapper("") :
                new ReadOnlyStringWrapper(cdf.getValue().getNote()));
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
        if (appTempData !=null) {
            return html + getLoincAnnotationData() + "</body></html>";
        } else {
            return html + "</body></html>";
        }


    }


    private String getLoincAnnotationData() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("<li>Number of HPO Terms: %d</li>",appResources.getHpo().countNonObsoleteTerms()));
        sb.append(String.format("<li>Number of annotation LOINC codes: %d</li>",appResources.getLoincAnnotationMap().size()));
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
        Map<LoincId,LOINC2HpoAnnotationImpl> testmap = appResources.getLoincAnnotationMap();
        Platform.runLater(() -> {
            loincAnnotationTableView.getItems().clear();
            loincAnnotationTableView.getItems().addAll(testmap.values());
        });

    }

    /**
     * This method set up the filename for annotations data and call the following function to import data
     */
    public void importLoincAnnotation() {
        logger.debug("Num of annotations in appTempData: " + appResources.getLoincAnnotationMap().size());

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TSV files (*.txt)", "*.tsv"));
        chooser.setTitle("Choose annotation file");
        File f = chooser.showOpenDialog(null);
        if (f != null) {
            String path = f.getAbsolutePath();

            try {
                Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap =
                        LoincAnnotationSerializationFactory.parseFromFile(path,
                                appResources.getTermidTermMap(),
                                SerializationFormat.TSVSingleFile);
                appResources.getLoincAnnotationMap().putAll(annotationMap);
                refreshTable();
            } catch (Exception e) {
                logger.error("ERROR!!!!!!!!");
                return;
            }


        }
        logger.debug("Num of annotations in appTempData: " + appResources.getLoincAnnotationMap().size());
        refreshTable();

        annotateTabController.changeColorLoincTableView();

    }

    public void importLoincAnnotation(String pathToOpen) {

        logger.debug("Num of annotations in appTempData: " + appResources.getLoincAnnotationMap().size());

        /**
        //if using the LoincAnnotationSerializerToTSVSingleFile for serialization
        String basicAnnotationsFilePath = pathToOpen + File.separator + Constants.TSVSeparateFilesBasic;
        LoincAnnotationSerializationFactory.setLoincEntryMap(appTempData.getLoincEntryMap());
        LoincAnnotationSerializationFactory.setHpoTermMap(appTempData.getTermMap2());
        if (new File(basicAnnotationsFilePath).exists()) {
            try {
                //import basic annotations
                Map<LoincId, LOINC2HpoAnnotationImpl> deserializedMap =
                        LoincAnnotationSerializationFactory.parseFromFile(pathToOpen, appTempData.getTermMap2(),
                                LoincAnnotationSerializationFactory.SerializationFormat.TSVSeparateFile);
                appTempData.getLoincAnnotationMap().putAll(deserializedMap);
            } catch (Exception e) {
                logger.error("error during deserialization");
            }

        }
         //end
        **/


        //if using the LoincAnnotationSerializerTSVSingleFile for serialization
        String tsvSingleFile = pathToOpen + File.separator
                + Constants.TSVSingleFileFolder + File.separator + Constants.TSVSingleFileName;
        Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap = new HashMap<>();

        try {
            annotationMap = LoincAnnotationSerializationFactory.parseFromFile(tsvSingleFile, appResources.getTermidTermMap(), SerializationFormat.TSVSingleFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (AnnotationQC.hasUnrecognizedTermId(annotationMap, appResources.getHpo())) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    //Alert alert = new Alert(Alert.AlertType.WARNING, "I Warn You!", ButtonType.OK, ButtonType.CANCEL);
                    Alert alert1 = new Alert(Alert.AlertType.WARNING);
                    alert1.setHeaderText("Error loading annotation data");
                    alert1.setContentText("This is typically due to that HPO is outdated. Update your local copy of HPO and restart this app.");
                    alert1.setTitle("Warning");
                    Stage stage = (Stage) alert1.getDialogPane().getScene().getWindow();
                    stage.setAlwaysOnTop(true);
                    stage.showAndWait();
                }
            });
        }

        appResources.getLoincAnnotationMap().putAll(annotationMap);
        logger.info("Num of annotations in appTempData: " + appResources.getLoincAnnotationMap().size());
        refreshTable();
        annotateTabController.changeColorLoincTableView();
    }

    @FXML
    private void handleReview(ActionEvent event) {

        if (appResources.getLoincEntryMap() == null || appResources.getLoincEntryMap().isEmpty()) {
            PopUps.showInfoMessage("The loinc number is not found. Try clicking \"Initialize LOINC Table\"", "Loinc Not Found");
            return;
        }
        if (appResources.getTermidTermMap() == null || appResources.getTermidTermMap().isEmpty()) {
            PopUps.showInfoMessage("Hpo is not imported yet. Try clicking \"Initialize HPO appTempData\" first.", "HPO not imported");
            return;
        }
        LOINC2HpoAnnotationImpl selected = loincAnnotationTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            annotateTabController.setLoincIdSelected(selected.getLoincId());
            annotateTabController.showAllAnnotations(event);
        }

    }

    @FXML
    private void handleEdit(ActionEvent event) {

        if (appResources.getLoincEntryMap() == null || appResources.getLoincEntryMap().isEmpty()) {
            PopUps.showInfoMessage("The loinc number is not found. Try clicking \"Initialize LOINC Table\"", "Loinc Not Found");
            return;
        }
        if (appResources.getTermidTermMap() == null || appResources.getTermidTermMap().isEmpty()) {
            PopUps.showInfoMessage("Hpo is not imported yet. Try clicking \"Initialize HPO appTempData\" first.", "HPO not imported");
            return;
        }

        LOINC2HpoAnnotationImpl toEdit = loincAnnotationTableView.getSelectionModel()
                .getSelectedItem();
        if (toEdit != null) {
            mainController.switchTab(MainController.TabPaneTabs.AnnotateTabe);
            annotateTabController.editCurrentAnnotation(toEdit);
        }
        event.consume();
    }

    @FXML
    private void handleDelete(ActionEvent event) {

        boolean confirmation = PopUps.getBooleanFromUser("Are you sure you want to delete the record?", "Confirm deletion request", "Deletion");
        if (confirmation) {
            LOINC2HpoAnnotationImpl toDelete = loincAnnotationTableView.getSelectionModel()
                    .getSelectedItem();
            if (toDelete != null) {
                loincAnnotationTableView.getItems().remove(toDelete);
                appResources.getLoincAnnotationMap().remove(toDelete.getLoincId());
                appTempData.setSessionChanged(true);
            }
        }
        event.consume();
    }

    protected void exportAnnotationsAsTSV() {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Specify file name");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TSV files (*.txt)", "*.tsv"));
        File f = chooser.showSaveDialog(null);
        boolean overwrite = false;
        String path;
        if (f != null) {
            path = f.getAbsolutePath();
            if (f.exists()) { //check if user wants to overwrite the existing file
                overwrite = PopUps.getBooleanFromUser("Overwrite?",
                        "File will be overwritten", null);
            }

            if (!f.exists() || overwrite) {
                try {
                    LoincAnnotationSerializationFactory.setHpoTermMap(appResources.getTermidTermMap());
                    LoincAnnotationSerializationFactory.setLoincEntryMap(appResources.getLoincEntryMap());
                    LoincAnnotationSerializationFactory.serializeToFile(appResources.getLoincAnnotationMap(),
                            LoincAnnotationSerializationFactory.SerializationFormat.TSVSingleFile, path);
                } catch (IOException e1) {
                    PopUps.showWarningDialog("Error message",
                            "Failure to Save Session Data" ,
                            String.format("An error occurred when trying to save data to %s. Try again!", path));
                    return;
                }
            }
        }
    }


    protected void saveAnnotations() {
        //@TODO: implement saving if necessary
        throw new UnsupportedOperationException();
    }


    protected void newAppend() {
        //@TODO: implement if necessary
        throw new UnsupportedOperationException();
    }

    protected void saveAnnotationsAs() {

        String path = null;
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose LOINC Core Table file");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TSV files (*.txt)", "*.tsv"));
        File f = chooser.showSaveDialog(null);
        if (f != null) {
            path = f.getAbsolutePath();
            logger.trace("Save annotation data to new file: {}",path);
        } else {
            logger.error("Unable to obtain path to a new file to save " +
                    "annotation data to");
            return;

        }

        //@TODO: implement if necessary
        throw new UnsupportedOperationException();

    }

    protected void clear() {
        appResources.getLoincAnnotationMap().clear();
        loincAnnotationTableView.getItems().clear();
    }

}
