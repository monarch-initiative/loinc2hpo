package org.monarchinitiative.loinc2hpo.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.gui.PopUps;
import org.monarchinitiative.loinc2hpo.io.LoincMappingParser;
import org.monarchinitiative.loinc2hpo.io.WriteToFile;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.loinc.UniversalLoinc2HPOAnnotation;
import org.monarchinitiative.loinc2hpo.model.Model;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
    private TableView<UniversalLoinc2HPOAnnotation> loincAnnotationTableView;
    @FXML private TableColumn<UniversalLoinc2HPOAnnotation,String> loincNumberColumn;
    @FXML private TableColumn<UniversalLoinc2HPOAnnotation,String> belowNormalHpoColumn;
    @FXML private TableColumn<UniversalLoinc2HPOAnnotation,String> notAbnormalHpoColumn;
    @FXML private TableColumn<UniversalLoinc2HPOAnnotation,String> aboveNormalHpoColumn;
    @FXML private TableColumn<UniversalLoinc2HPOAnnotation, String> loincScaleColumn;
    @FXML private TableColumn<UniversalLoinc2HPOAnnotation, String> loincFlagColumn;
    @FXML private TableColumn<UniversalLoinc2HPOAnnotation, String> noteColumn;



    //@FXML private WebView wview;

    public void setModel(Model m) {
        model = m;
    }

    @FXML
    private void initialize() {
        logger.trace("Calling initialize");
        loincAnnotationTableView.setEditable(false);
        loincNumberColumn.setSortable(true);
        loincNumberColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getLoincId().toString()));
        loincScaleColumn.setSortable(true);
        loincScaleColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getLoincScale().toString()));
        belowNormalHpoColumn.setSortable(true);
        belowNormalHpoColumn.setCellValueFactory(cdf -> cdf.getValue()==null ? new ReadOnlyStringWrapper("\" \"") :
        new ReadOnlyStringWrapper(model.termId2HpoName(cdf.getValue().getBelowNormalHpoTermId())));
        notAbnormalHpoColumn.setSortable(true);
        notAbnormalHpoColumn.setCellValueFactory(cdf -> cdf.getValue() == null ? new ReadOnlyStringWrapper("\" \"")
                : new ReadOnlyStringWrapper(model.termId2HpoName(cdf.getValue().getNotAbnormalHpoTermName())));
        aboveNormalHpoColumn.setSortable(true);
        aboveNormalHpoColumn.setCellValueFactory(cdf -> cdf.getValue() == null ? new ReadOnlyStringWrapper("\" \"")
        : new ReadOnlyStringWrapper(model.termId2HpoName(cdf.getValue().getAboveNormalHpoTermName())));
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
        Map<LoincId,UniversalLoinc2HPOAnnotation> testmap = model.getLoincAnnotationMap();
        Platform.runLater(() -> {
            loincAnnotationTableView.getItems().clear();
            loincAnnotationTableView.getItems().addAll(testmap.values());
        });

    }


    public void importLoincAnnotation() {
        logger.debug("Num of annotations in model: " + model.getLoincAnnotationMap().size());
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose annotation file");
        File f = chooser.showOpenDialog(null);
        if (f != null) {
            String path = f.getAbsolutePath();
            LoincMappingParser parser = new LoincMappingParser(path, model.getOntology());
            Set<UniversalLoinc2HPOAnnotation> testset = parser.getTests();
            for (UniversalLoinc2HPOAnnotation test : testset) {
                model.addLoincTest(test);
            }
        }
        logger.debug("Num of annotations in model: " + model.getLoincAnnotationMap().size());
        refreshTable();
    }


    public void appendLoincAnnotation() {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose LOINC Core Table file");
        File f = chooser.showOpenDialog(null);
        if (f != null) {
            String path = f.getAbsolutePath();
            model.setPathToAnnotationFile(path);
            logger.trace(String.format("append annotation data to: ",path));
        } else {
            logger.error("Unable to obtain path to LOINC Core Table file");
        }
        String path = model.getPathToAnnotationFile();
        String annotationData = annotationDataToString();
        WriteToFile.appendToFile(annotationData, path);

    }

    public void saveLoincAnnotation() {

        String path = model.getPathToAnnotationFile();
        if (path == null) {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choose LOINC Core Table file");
            File f = chooser.showSaveDialog(null);
            if (f != null) {
                path = f.getAbsolutePath();
                model.setPathToAnnotationFile(path);
                logger.trace("Save annotation data to new file: ",path);
            } else {
                logger.error("Unable to obtain path to a new file to save " +
                        "annotation data to");
            }
        } else {
            logger.info("path to destination file: " + path);
        }
        String annotationData = annotationDataToString();
        WriteToFile.writeToFile(LoincEntry.getHeaderLine() + "\n", path);
        WriteToFile.appendToFile(annotationData, path);
    }

    public void saveAsLoincAnnotation(){

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose LOINC Core Table file");
        File f = chooser.showSaveDialog(null);
        if (f != null) {
            String path = f.getAbsolutePath();
            if (f.equals(model.getPathToAnnotationFile())) {
                PopUps.showInfoMessage("Cannot Save As an existing file",
                        "ERROR: File Already Existed");
                return;
            }
            model.setPathToAnnotationFile(path);
            logger.trace(String.format("Setting path to LOINC Core Table file to %s",path));
        } else {
            logger.error("Unable to obtain path to LOINC Core Table file");
        }
        //model.writeSettings();

        String path = model.getPathToAnnotationFile();
        String annotationData = annotationDataToString();
        WriteToFile.writeToFile(LoincEntry.getHeaderLine() + "\n", path);
        WriteToFile.appendToFile(annotationData, path);
    }




    public String annotationDataToString() {

        StringBuilder builder = new StringBuilder();
        if (loincAnnotationTableView.getItems().size() > 0) {

            List<UniversalLoinc2HPOAnnotation> annotations = loincAnnotationTableView
                    .getItems();
            for (UniversalLoinc2HPOAnnotation annotation : annotations) {
                boolean flag = annotation.getFlag();
                char flagString = flag ? 'Y' : 'N';
                builder.append(flagString + "\t");
                builder.append(annotation.getLoincId() + "\t");
                String scale = annotation.getLoincScale() == null  ? "NA" : annotation.getLoincScale().toString();
                builder.append(scale + "\t");
                String hpoL = annotation.getBelowNormalHpoTermId() == null ? "NA" : annotation.getBelowNormalHpoTermId().getIdWithPrefix();
                builder.append(hpoL + "\t");
                String hpoN = annotation.getNotAbnormalHpoTermName() == null ? "NA" :annotation.getNotAbnormalHpoTermName().getIdWithPrefix();
                builder.append(hpoN + "\t");
                String hpoH = annotation.getAboveNormalHpoTermName() == null ? "NA" : annotation.getAboveNormalHpoTermName().getIdWithPrefix();
                builder.append(hpoH + "\t");
                String note = (annotation==null || annotation.getNote()==null ||annotation.getNote().isEmpty()) ? "NA" : annotation.getNote();
                builder.append(note);
                builder.append("\n");
            }

        }

        return builder.toString();
    }



    @FXML
    private void deleteLoincAnnotation(ActionEvent event){
        UniversalLoinc2HPOAnnotation toDelete = loincAnnotationTableView.getSelectionModel()
                .getSelectedItem();
        if (toDelete != null) {
            loincAnnotationTableView.getItems().remove(toDelete);
            model.removeLoincTest(String.valueOf(toDelete.getLoincId()));
        }
        event.consume();

    }

    protected void exportAnnotationsAsTSV() {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Specify file name");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File f = chooser.showSaveDialog(null);
        if (f != null) {
            String path = f.getAbsolutePath();
            if (f.exists()) {
                boolean overwrite = PopUps.getBooleanFromUser("Overwrite?", "File will be overwritten", null);
                if (overwrite) {
                    try {
                        WriteToFile.toTSV(path, model.getLoincAnnotationMap());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    WriteToFile.toTSV(path, model.getLoincAnnotationMap());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
