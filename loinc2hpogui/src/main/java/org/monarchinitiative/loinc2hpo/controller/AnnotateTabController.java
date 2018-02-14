package org.monarchinitiative.loinc2hpo.controller;


//import apple.laf.JRSUIUtils;
import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.exception.NetPostException;
import org.monarchinitiative.loinc2hpo.github.GitHubLabelRetriever;
import org.monarchinitiative.loinc2hpo.github.GitHubPoster;
import org.monarchinitiative.loinc2hpo.gui.GitHubPopup;
import org.monarchinitiative.loinc2hpo.gui.Main;
import org.monarchinitiative.loinc2hpo.gui.PopUps;
import org.monarchinitiative.loinc2hpo.io.LoincOfInterest;
import org.monarchinitiative.loinc2hpo.io.OntologyModelBuilderForJena;
import org.monarchinitiative.loinc2hpo.loinc.*;
import org.monarchinitiative.loinc2hpo.model.Annotation;
import org.monarchinitiative.loinc2hpo.model.Model;
import org.monarchinitiative.loinc2hpo.util.HPO_Class_Found;
import org.monarchinitiative.loinc2hpo.util.LoincCodeClass;
import org.monarchinitiative.loinc2hpo.util.LoincLongNameParser;
import org.monarchinitiative.loinc2hpo.util.SparqlQuery;
import sun.nio.ch.Net;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


@Singleton
public class AnnotateTabController {
    private static final Logger logger = LogManager.getLogger();

    private Model model=null;
    /** Reference to the third tab. When the user adds a new annotation, we update the table, therefore, we need a reference. */
    @Inject private Loinc2HpoAnnotationsTabController loinc2HpoAnnotationsTabController;
    private ImmutableMap<LoincId,LoincEntry> loincmap=null;


    //private final Stage primarystage;

    @FXML private Button IntializeHPOmodelbutton;
    @FXML private Button initLOINCtableButton;
    @FXML private Button searchForLOINCIdButton;
    @FXML private Button createAnnotationButton;
    @FXML private TextField loincSearchTextField;
    @FXML private Button filterLoincTableByList;
    @FXML private TextField userInputForManualQuery;

    //drag and drop to the following fields
    private boolean advancedAnnotationModeSelected = false;
    @FXML private Label annotationLeftLabel;
    @FXML private Label annotationMiddleLabel;
    @FXML private Label annotationRightLabel;
    @FXML private TextField annotationTextFieldLeft;
    @FXML private TextField annotationTextFieldMiddle;
    @FXML private TextField annotationTextFieldRight;
    @FXML private CheckBox inverseChecker;
    private HPO_Class_Found hpo_drag_and_drop;
    //private ImmutableMap<String, HPO_Class_Found> selectedHPOforAnnotation;

    private ImmutableMap<String,HpoTerm> termmap;

    @FXML private ListView hpoListView;



    @FXML private Accordion accordion;
    @FXML private TitledPane loincTableTitledpane;
    @FXML private TableView<LoincEntry> loincTableView;
    @FXML private TableColumn<LoincEntry, String> loincIdTableColumn;
    @FXML private TableColumn<LoincEntry, String> componentTableColumn;
    @FXML private TableColumn<LoincEntry, String> propertyTableColumn;
    @FXML private TableColumn<LoincEntry, String> timeAspectTableColumn;
    @FXML private TableColumn<LoincEntry, String> methodTableColumn;
    @FXML private TableColumn<LoincEntry, String> scaleTableColumn;
    @FXML private TableColumn<LoincEntry, String> systemTableColumn;
    @FXML private TableColumn<LoincEntry, String> nameTableColumn;


    @FXML private Button modeButton;
    @FXML private TitledPane advancedAnnotationTitledPane;
    @FXML private TableView<Annotation> advancedAnnotationTable;
    @FXML private TableColumn<Annotation, String> advancedAnnotationSystem;
    @FXML private TableColumn<Annotation, String> advancedAnnotationCode;
    @FXML private TableColumn<Annotation, String> advancedAnnotationHpo;

    //candidate HPO classes found by Sparql query
    //@FXML private TableView<HPO_Class_Found> candidateHPOList;
    //@FXML private TableColumn<HPO_Class_Found, Integer> score;
    //@FXML private TableColumn<HPO_Class_Found, String> id;
    //@FXML private TableColumn<HPO_Class_Found, String> label;
    //@FXML private TableColumn<HPO_Class_Found, String> definition;

    @FXML private TreeView<HPO_TreeView> treeView;

    @FXML private CheckBox flagForAnnotation;
    @FXML private Circle createAnnotationSuccess;
    @FXML private TextArea annotationNoteField;


    @FXML private Button suggestHPOButton;

    @FXML private void initialize() {
        if (model != null) {
            setModel(model);
        }

        suggestHPOButton.setTooltip(new Tooltip("Suggest new HPO terms"));
    }


    private void noLoincEntryAlert(){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection ERROR");
            alert.setHeaderText("Select a row in Loinc table");
            alert.setContentText("A loinc code is required for ranking " +
                    "candidate HPO terms. Select one row in the loinc " +
                    "table and query again.");
            alert.showAndWait();
    }

    private void clearAbnormalityTextField(){
        annotationTextFieldLeft.setText("");
        annotationTextFieldRight.setText("");
        annotationTextFieldMiddle.setText("");
    }


    /** Initialize the Model reference and set up the HPO autocomplete if possible. */
    public void setModel(Model m) {
        logger.trace("Setting model in AnnotateTabeController");
        model=m;
        if (model.getPathToHpoOboFile()==null) {
            logger.error("Path to hp.obo file is null. Cannot initialize autocomplete");
            return;
        }
        model.parseOntology();
        termmap = model.getTermMap();
//        WidthAwareTextFields.bindWidthAwareAutoCompletion(annotationTextFieldLeft, termmap.keySet());
//        WidthAwareTextFields.bindWidthAwareAutoCompletion(annotationTextFieldMiddle, termmap.keySet());
//        WidthAwareTextFields.bindWidthAwareAutoCompletion(annotationTextFieldRight, termmap.keySet());
        logger.trace(String.format("Initializing term map to %d terms",termmap.size()));
    }


    private void initTableStructure() {
        loincIdTableColumn.setSortable(true);
        loincIdTableColumn.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getLOINC_Number().toString())
        );
        componentTableColumn.setSortable(true);
        componentTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getComponent()));
        propertyTableColumn.setSortable(true);
        propertyTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getProperty()));
        timeAspectTableColumn.setSortable(true);
        timeAspectTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getTimeAspect()));
        methodTableColumn.setSortable(true);
        methodTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getMethod()));
        scaleTableColumn.setSortable(true);
        scaleTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getScale()));
        systemTableColumn.setSortable(true);
        systemTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getSystem()));
        nameTableColumn.setSortable(true);
        nameTableColumn.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getLongName()));
        //hpoListView.setOrientation(Orientation.HORIZONTAL);

        loincTableView.setRowFactory( tv -> {
            TableRow<LoincEntry> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    LoincEntry rowData = row.getItem();
                    initHpoTermListView(rowData);
                    //clear text in abnormality text fields
                    clearAbnormalityTextField();
                    //inialize the flag field
                    flagForAnnotation.setIndeterminate(false);
                    flagForAnnotation.setSelected(false);
                    createAnnotationSuccess.setFill(Color.WHITE);
                    annotationNoteField.setText("");
                }
            });
            return row ;
        });

        accordion.setExpandedPane(loincTableTitledpane);
    }



    private void initHpoTermListView(LoincEntry entry) {
        if(SparqlQuery.model == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("HPO Model Undefined");
            alert.setHeaderText("Create HPO model first before querying");
            alert.setContentText("Click \"Initialize HPO model\" to create an" +
                    " HPO model for Sparql query. Click and query again.");
            alert.showAndWait();
            return;
        }
        String name = entry.getLongName();
        List<HPO_Class_Found> queryResults = SparqlQuery.query_auto(name);
        if (queryResults.size() != 0) {
            ObservableList<HPO_Class_Found> items = FXCollections.observableArrayList();
            for (HPO_Class_Found candidate: queryResults) {
                items.add(candidate);
            }
            this.hpoListView.setItems(items);
            //items.add("0 result is found. Try manual search with synonyms.");
        } else {
            ObservableList<String> items = FXCollections.observableArrayList();
            items.add("0 HPO class is found. Try manual search with " +
                    "alternative keys (synonyms)");
            this.hpoListView.setItems(items);
        }
    }

    @FXML private void handleAutoQueryButton(ActionEvent e){
        e.consume();
        LoincEntry entry = loincTableView.getSelectionModel()
                .getSelectedItem();
        if (entry == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection ERROR");
            alert.setHeaderText("Select a row in Loinc table");
            alert.setContentText("A loinc code is required for ranking " +
                    "candidate HPO terms. Select one row in the loinc " +
                    "table and query again.");
            alert.showAndWait();
            return;
        }
        logger.info(String.format("Start auto query for \"%s\"by pressing button",entry));
        initHpoTermListView(entry);
        //clear text in abnormality text fields
        clearAbnormalityTextField();
        //inialize the flag field
        flagForAnnotation.setIndeterminate(false);
        flagForAnnotation.setSelected(false);
        createAnnotationSuccess.setFill(Color.WHITE);
        annotationNoteField.setText("");
    }

    @FXML private void handleManualQueryButton(ActionEvent e) {

        e.consume();
        if(SparqlQuery.model == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("HPO Model Undefined");
            alert.setHeaderText("Create HPO model first before querying");
            alert.setContentText("Click \"Initialize HPO model\" to create an" +
                    " HPO model for Sparql query. Click and query again.");
            alert.showAndWait();
            return;
        }

        //for now, force user choose a loinc entry. TODO: user may or may not
        // choose a loinc term.
        LoincEntry entry = loincTableView.getSelectionModel().getSelectedItem();
        if (entry == null) {
            noLoincEntryAlert();
            return;
        }
        String userInput = userInputForManualQuery.getText();
        if (userInput == null || userInput.trim().length() < 2) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Input Error");
            alert.setHeaderText("Type in keys for manual query");
            alert.setContentText("Provide comma seperated keys for query. Do " +
                    "not use quotes(\"\"). Avoid non-specific words " +
                    "or numbers. Synonyms are strongly recommended if " +
                    "auto-query is not working.");
            alert.showAndWait();
            return;
        }
        String[] keys = userInput.split(",");
        List<String> keysInList = new ArrayList<>();
        for (String key: keys) {
            if (key.length() > 0) {
                keysInList.add(key);
            }
        }

        String name = entry.getLongName();
        LoincCodeClass loincCodeClass = LoincLongNameParser.parse(name);
        List<HPO_Class_Found> queryResults = SparqlQuery.query_manual
                (keysInList, loincCodeClass);
        if (queryResults.size() != 0) {
            ObservableList<HPO_Class_Found> items = FXCollections.observableArrayList();
            for (HPO_Class_Found candidate: queryResults) {
                items.add(candidate);
            }
            this.hpoListView.setItems(items);
            userInputForManualQuery.clear();
            //items.add("0 result is found. Try manual search with synonyms.");
        } else {
            ObservableList<String> items = FXCollections.observableArrayList();
            items.add("0 HPO class is found. Try manual search with " +
                    "alternative keys (synonyms)");
            this.hpoListView.setItems(items);
        }
        //clear text in abnormality text fields
        clearAbnormalityTextField();
        //inialize the flag field
        flagForAnnotation.setIndeterminate(false);
        flagForAnnotation.setSelected(false);
        createAnnotationSuccess.setFill(Color.WHITE);
        annotationNoteField.setText("");
    }

    @FXML private void initLOINCtableButton(ActionEvent e) {
        logger.trace("init LOINC table");
        initTableStructure();
        String loincCoreTableFile=model.getPathToLoincCoreTableFile();
        if (loincCoreTableFile==null) {
            logger.error("Could not get path to LOINC Core Table file");
            return;
        }
        this.loincmap = LoincEntry.getLoincEntryList(loincCoreTableFile);
        model.setLoincEntryMap(this.loincmap);
        int limit=Math.min(loincmap.size(),1000); // we will show just the first 1000 entries in the table.
        List<LoincEntry> lst = loincmap.values().asList().subList(0,limit);
        loincTableView.getItems().clear(); // remove any previous entries
        loincTableView.getItems().addAll(lst);
        loincTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        e.consume();
    }
/**
    @FXML private void initHPOmodelButton(ActionEvent e){

        //Remind user that this is a time consuming process
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Time Consuming step");
        alert.setHeaderText("Wait for the process to complete");
        alert.setContentText("This step will take about 2 minutes. It needs " +
                        "to be executed once for every session");
        alert.showAndWait();

        String pathToHPO = this.model.getPathToHpoOwlFile();
        logger.info("pathToHPO: " + pathToHPO);
        SparqlQuery.getOntologyModel(pathToHPO);

    }
**/
    @FXML private void initHPOmodelButton(ActionEvent e){

        String pathToHPO = this.model.getPathToHpoOwlFile();
        logger.info("pathToHPO: " + pathToHPO);
        //create a task to create HPO model
        Task<org.apache.jena.rdf.model.Model> task = new OntologyModelBuilderForJena(pathToHPO);
        new Thread(task).start();
        task.setOnSucceeded(x -> {
            SparqlQuery.setHPOmodel(task.getValue());
            IntializeHPOmodelbutton.setStyle("-fx-background-color: #00ff00");
            IntializeHPOmodelbutton.setText("HPO initialized");
        });
        task.setOnRunning(x -> {
            IntializeHPOmodelbutton.setStyle("-fx-background-color: #ffc0cb");
            IntializeHPOmodelbutton.setText("HPO initializing...");
        });
        task.setOnFailed(x -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failured to create HPO model");
            alert.setContentText("Check whether hpo.owl is downloaded.");
            alert.showAndWait();
        });
        e.consume();

    }


    @FXML private void search(ActionEvent e) {
        e.consume();
        String query = this.loincSearchTextField.getText().trim();
        if (query.isEmpty()) return;
        List<LoincEntry> entrylist=new ArrayList<>();
        try {
            LoincId loincId = new LoincId(query);
            entrylist.add(this.loincmap.get(loincId));
        } catch (MalformedLoincCodeException msg) {
            loincmap.values().stream()
                    .filter( loincEntry -> containedIn(query, loincEntry.getLongName()))
                    .forEach(loincEntry -> entrylist.add(loincEntry));
        }
        if (entrylist.isEmpty()) {
            logger.error(String.format("Could not identify LOINC entry for \"%s\"",query));
            PopUps.showWarningDialog("LOINC Search", "No hits found", String.format("Could not identify LOINC entry for \"%s\"",query));
            return;
        } else {
            logger.trace(String.format("Searching table for:  %s",query));
        }
        if (termmap==null) initialize(); // set up the Hpo autocomplete if possible
        loincTableView.getItems().clear();
        loincTableView.getItems().addAll(entrylist);
        accordion.setExpandedPane(loincTableTitledpane);
    }

    private boolean containedIn(String query, String text) {
        String [] keys = query.split("\\W");
        for (String key : keys) {
            if (!text.toLowerCase().contains(key.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    @FXML private void handleLoincFiltering(ActionEvent e){

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose File containing a list of interested Loinc " +
                "codes");
        File f = chooser.showOpenDialog(null);
        if (f != null) {
            String path = f.getAbsolutePath();
            try {
                HashSet<String> loincOfInterest = new LoincOfInterest(path).getLoincOfInterest();
                List<LoincEntry> entryOfInterest = new ArrayList<>();
                List<String> notFound = new ArrayList<>();
                for (String loinc : loincOfInterest) {
                    if (this.loincmap.containsKey(loinc)) {
                        entryOfInterest.add(this.loincmap.get(loinc));
                    } else {
                        notFound.add(loinc);
                    }
                }
                loincTableView.getItems().clear();
                loincTableView.getItems().addAll(entryOfInterest);

            } catch (FileNotFoundException excpt) {
                logger.error("unable to find the file for loinc of interest");
            }
        } else {
            logger.error("Unable to obtain path to LOINC of interest file");
        }
        e.consume();
    }




    /**
     * private class for showing HPO class in treeview.
     * Another reason to have this is to facilitate drag and draw from treeview.
     */
    private class HPO_TreeView{
        private HPO_Class_Found hpo_class_found;
        private HPO_TreeView() {
            this.hpo_class_found = null;
        }
        private HPO_TreeView(HPO_Class_Found hpo_class_found) {
            this.hpo_class_found = hpo_class_found;
        }

        public HPO_Class_Found getHpo_class_found() {
             return this.hpo_class_found;
        }

        @Override
        public String toString() {
            if (this.hpo_class_found == null) {
                return "root";
            }
            String stringRepretation = "";
            String[] id_words = this.hpo_class_found.getId().split("/");
            stringRepretation += id_words[id_words.length - 1];
            stringRepretation += "\n";
            stringRepretation += this.hpo_class_found.getLabel();
            return stringRepretation;
        }
    }

    @FXML private void handleCandidateHPODoubleClick(MouseEvent e){

        if (e.getClickCount() == 2 && hpoListView.getSelectionModel()
                .getSelectedItem() != null && hpoListView.getSelectionModel()
                .getSelectedItem() instanceof HPO_Class_Found) {
            HPO_Class_Found hpo_class_found = (HPO_Class_Found) hpoListView
                    .getSelectionModel().getSelectedItem();
            List<HPO_Class_Found> parents = SparqlQuery.getParents
                    (hpo_class_found.getId());
            List<HPO_Class_Found> children = SparqlQuery.getChildren
                    (hpo_class_found.getId());

            TreeItem<HPO_TreeView> rootItem = new TreeItem<>(new HPO_TreeView());
            rootItem.setExpanded(true);

            if (parents.size() > 0) {
                for (HPO_Class_Found parent : parents) {
                    TreeItem<HPO_TreeView> parentItem = new TreeItem<>(new
                            HPO_TreeView(parent));
                    rootItem.getChildren().add(parentItem);
                    TreeItem<HPO_TreeView> current = new TreeItem<>
                            (new HPO_TreeView(hpo_class_found));
                    parentItem.getChildren().add(current);
                    parentItem.setExpanded(true);
                    current.setExpanded(true);
                    if (children.size() > 0) {
                        for (HPO_Class_Found child : children) {
                            TreeItem<HPO_TreeView> childItem = new TreeItem<>
                                    (new HPO_TreeView(child));
                            current.getChildren().add(childItem);
                        }
                    }
                }
            }
            this.treeView.setRoot(rootItem);
        }
        e.consume();
    }

    @FXML private void handleCandidateHPODragged(MouseEvent e) {

        System.out.println("Drag event detected");
        Dragboard db = hpoListView.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        Object selectedCell = hpoListView.getSelectionModel().getSelectedItem();
        if (selectedCell instanceof HPO_Class_Found) {
            content.putString(((HPO_Class_Found) selectedCell).getLabel());
            db.setContent(content);
        } else {
            logger.info("Dragging something that is not a HPO term");
        }

        e.consume();
    }

    @FXML private void handleHPOLowAbnormality(DragEvent e){

        if (e.getDragboard().hasString()) {
            annotationTextFieldLeft.setText(e.getDragboard().getString());
        }
        annotationTextFieldLeft.setStyle("-fx-background-color: WHITE;");
        e.consume();
    }

    @FXML private void handleHPOHighAbnormality(DragEvent e){

        if (e.getDragboard().hasString()) {
            annotationTextFieldRight.setText(e.getDragboard().getString());
        }
        annotationTextFieldRight.setStyle("-fx-background-color: WHITE;");
        e.consume();

    }

    @FXML private void handleParentAbnormality(DragEvent e){
        if (e.getDragboard().hasString()) {
            annotationTextFieldMiddle.setText(e.getDragboard().getString());
        }
        annotationTextFieldMiddle.setStyle("-fx-background-color: WHITE;");
        e.consume();
    }

    private UniversalLoinc2HPOAnnotation tempLoinc2HPOAnnotation = null;
    private Map<String, String> tempTerms = new HashMap<>();

    /**
     * Record the terms for basic annotation
     * @param temp
     */
    private void recordTempTerms(Map<String, String> temp){
        String hpoLo = annotationTextFieldLeft.getText();
        if (hpoLo!= null && !hpoLo.trim().isEmpty())
            hpoLo = stripEN(hpoLo.trim());
        String hpoNormal = annotationTextFieldMiddle.getText();
        if (hpoNormal != null && !hpoNormal.trim().isEmpty())
            hpoNormal = stripEN(hpoNormal.trim());
        String hpoHi= annotationTextFieldRight.getText();
        if (hpoHi != null && !hpoHi.isEmpty()) hpoHi = stripEN(hpoHi.trim());

        if(hpoLo != null && !hpoLo.isEmpty()) temp.put("hpoLo", hpoLo);
        if(hpoNormal != null && !hpoNormal.isEmpty()) temp.put("hpoNormal", hpoNormal);
        if(hpoHi != null && !hpoHi.isEmpty()) temp.put("hpoHi", hpoHi);
    }


    /**
     * Create a temporary annotation object so that we can add advanced annotations later
     */
    @Deprecated
    private void createTempAnnotation(){

        LoincId loincCode = loincTableView.getSelectionModel().getSelectedItem().getLOINC_Number();
        LoincScale loincScale = LoincScale.string2enum(loincTableView.getSelectionModel().getSelectedItem().getScale());

        tempLoinc2HPOAnnotation = new UniversalLoinc2HPOAnnotation(loincCode, loincScale);

        if (model.getTestmap().containsKey(loincCode)) {
            boolean overwrite = PopUps.getBooleanFromUser(loincCode + " has already been annotated. Overwrite?",
                    "Annotation already exist", "Overwrite Warning");
            if (!overwrite) return;
        }
        //strip "@en" from all of terms, if they have it
        String hpoLo = stripEN(annotationTextFieldLeft.getText().trim());
        String hpoNormal = stripEN(annotationTextFieldMiddle.getText().trim());
        String hpoHi= stripEN(annotationTextFieldRight.getText().trim());

        //We don't have to force every loinc code to have three phenotypes
        HpoTerm low = termmap.get(hpoLo);
        HpoTerm normal = termmap.get(hpoNormal);
        HpoTerm high = termmap.get(hpoHi);

        //Warning user that there is something wrong
        //it happens when something is wrong with hpo termmap (a name could not be mapped)
        if (!hpoLo.trim().isEmpty() && low==null) {
            logger.error(hpoLo + " cannot be mapped to a term");
            createAnnotationSuccess.setFill(Color.RED);
            showErrorOfMapping(hpoLo);
            return;
        }

        if (!hpoHi.trim().isEmpty() && high==null) {
            logger.error(hpoHi + " cannot be mapped to a term");
            createAnnotationSuccess.setFill(Color.RED);
            showErrorOfMapping(hpoHi);
            return;
        }

        if (!hpoNormal.trim().isEmpty() && normal==null) {
            logger.error(hpoNormal + " cannot be mapped to a term");
            createAnnotationSuccess.setFill(Color.RED);
            showErrorOfMapping(hpoNormal);
            return;
        }

        Map<String, Boolean> qcresult = qcAnnotation(hpoLo, hpoNormal, hpoHi);
        if (qcresult.get("issueDetected") && !qcresult.get("userconfirmed")) {
            createAnnotationSuccess.setFill(Color.RED);
            return;
        } else {
            //String note = annotationNoteField.getText().isEmpty()? "\"\"":annotationNoteField.getText();
            try {
                Map<String, Code> internalCode = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);
                if (low != null) {
                    tempLoinc2HPOAnnotation.addAnnotation(internalCode.get("L"), new HpoTermId4LoincTest(low.getId(), false));
                }
                if (normal != null) {
                    tempLoinc2HPOAnnotation
                            .addAnnotation(internalCode.get("A"), new HpoTermId4LoincTest(normal.getId(), false));
                }
                if (normal != null && inverseChecker.isSelected()) {
                    tempLoinc2HPOAnnotation
                            .addAnnotation(internalCode.get("N"),  new HpoTermId4LoincTest(normal.getId(), true))
                            .addAnnotation(internalCode.get("NP"), new HpoTermId4LoincTest(normal.getId(), true));

                }
                if (high != null)
                tempLoinc2HPOAnnotation
                        .addAnnotation(internalCode.get("H"),  new HpoTermId4LoincTest(high.getId(), false))
                        .addAnnotation(internalCode.get("P"),  new HpoTermId4LoincTest(high.getId(), false));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
/**
    @FXML private void createLoinc2HpoAnnotation(ActionEvent e) {
        e.consume();

        if (tempLoinc2HPOAnnotation == null) createTempAnnotation();

        if (tempLoinc2HPOAnnotation != null && !tempAdvancedAnnotations.isEmpty()) {
            for (Annotation annotation : tempAdvancedAnnotations) {
                tempLoinc2HPOAnnotation.addAnnotation(annotation.getCode(), annotation.getHpo());
            }
        }
        if (tempLoinc2HPOAnnotation != null) {
            logger.info(tempLoinc2HPOAnnotation.getCodes().size() + " annotations");
            this.model.addLoincTest(tempLoinc2HPOAnnotation);
            tempLoinc2HPOAnnotation = null;
            switchToBasicAnnotationMode();
            advancedAnnotationModeSelected = false;

            loinc2HpoAnnotationsTabController.refreshTable();
            createAnnotationSuccess.setFill(Color.GREEN);
            changeColorLoincTableView();
        }
        //showSuccessOfMapping("Go to next loinc code!");
    }
**/

    @FXML private void createLoinc2HpoAnnotation(ActionEvent e) {
        e.consume();

        if(!advancedAnnotationModeSelected) recordTempTerms(tempTerms); //update terms for basic annotation
        tempTerms.values().stream().forEach(System.out::println);
        //if this function is called at advanced annotation mode, the terms for basic annotation was already saved
        String hpoLo = tempTerms.get("hpoLo");
        String hpoNormal = tempTerms.get("hpoNormal");
        String hpoHi = tempTerms.get("hpoHi");

        //We don't have to force every loinc code to have three phenotypes
        HpoTerm low = termmap.get(hpoLo);
        HpoTerm normal = termmap.get(hpoNormal);
        HpoTerm high = termmap.get(hpoHi);

        //Warning user that there is something wrong
        //it happens when something is wrong with hpo termmap (a name could not be mapped)
        if (hpoLo != null && low==null) {
            logger.error(hpoLo + " cannot be mapped to a term");
            createAnnotationSuccess.setFill(Color.RED);
            showErrorOfMapping(hpoLo);
            return;
        }

        if (hpoHi != null && high==null) {
            logger.error(hpoHi + " cannot be mapped to a term");
            createAnnotationSuccess.setFill(Color.RED);
            showErrorOfMapping(hpoHi);
            return;
        }

        if (hpoNormal !=null && normal==null) {
            logger.error(hpoNormal + " cannot be mapped to a term");
            createAnnotationSuccess.setFill(Color.RED);
            showErrorOfMapping(hpoNormal);
            return;
        }

        LoincId loincCode = loincTableView.getSelectionModel().getSelectedItem().getLOINC_Number();
        LoincScale loincScale = LoincScale.string2enum(loincTableView.getSelectionModel().getSelectedItem().getScale());

        tempLoinc2HPOAnnotation = new UniversalLoinc2HPOAnnotation(loincCode, loincScale);

        Map<String, Boolean> qcresult = qcAnnotation(hpoLo, hpoNormal, hpoHi);
        if (qcresult.get("issueDetected") && !qcresult.get("userconfirmed")) {
            createAnnotationSuccess.setFill(Color.RED);
            return;
        } else {
            //String note = annotationNoteField.getText().isEmpty()? "\"\"":annotationNoteField.getText();
            try {
                Map<String, Code> internalCode = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);
                if (hpoLo != null && low != null) {
                    tempLoinc2HPOAnnotation.addAnnotation(internalCode.get("L"), new HpoTermId4LoincTest(low.getId(), false));
                }
                if (hpoNormal != null && normal != null) {
                    tempLoinc2HPOAnnotation
                            .addAnnotation(internalCode.get("A"), new HpoTermId4LoincTest(normal.getId(), false));
                }
                if (hpoNormal != null && normal != null && inverseChecker.isSelected()) {
                    tempLoinc2HPOAnnotation
                            .addAnnotation(internalCode.get("N"),  new HpoTermId4LoincTest(normal.getId(), true))
                            .addAnnotation(internalCode.get("NP"), new HpoTermId4LoincTest(normal.getId(), true));

                }
                if (hpoHi != null && high != null)
                    tempLoinc2HPOAnnotation
                            .addAnnotation(internalCode.get("H"),  new HpoTermId4LoincTest(high.getId(), false))
                            .addAnnotation(internalCode.get("P"),  new HpoTermId4LoincTest(high.getId(), false));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }



        if (tempLoinc2HPOAnnotation != null && !tempAdvancedAnnotations.isEmpty()) {
            for (Annotation annotation : tempAdvancedAnnotations) {
                tempLoinc2HPOAnnotation.addAnnotation(annotation.getCode(), annotation.getHpo());
            }
        }
        if (tempLoinc2HPOAnnotation != null) {
            logger.info(tempLoinc2HPOAnnotation.getCodes().size() + " annotations");
            this.model.addLoincTest(tempLoinc2HPOAnnotation);
            tempLoinc2HPOAnnotation = null;
            advancedAnnotationModeSelected = false;
            tempTerms.clear();
            switchToBasicAnnotationMode();

            loinc2HpoAnnotationsTabController.refreshTable();
            createAnnotationSuccess.setFill(Color.GREEN);
            changeColorLoincTableView();
        }
        //showSuccessOfMapping("Go to next loinc code!");
    }



    /**
     * Do a qc of annotation, and ask user questions if there are potential issues
     * @param HpoLow
     * @param HpoNorm
     * @param HpoHigh
     * @return
     */
    private Map<String, Boolean> qcAnnotation(String HpoLow, String HpoNorm, String HpoHigh){

        boolean issueDetected = false;
        boolean userConfirmed = false;

        if ((HpoLow == null || HpoLow.trim().isEmpty()) &&
                (HpoNorm == null || HpoNorm.trim().isEmpty()) &&
                (HpoHigh == null || HpoHigh.trim().isEmpty())) {
            //popup an alert
            issueDetected = true;
            userConfirmed = PopUps.getBooleanFromUser("Are you sure you want to create an annotation without any HPO terms?",
                    "Annotation without HPO terms", "No HPO Alert");
        }

        if (HpoLow != null && HpoNorm != null && !HpoLow.trim().isEmpty() && stringEquals(HpoLow, HpoNorm)) {
            //alert: low and norm are same!
            issueDetected = true;
            userConfirmed = PopUps.getBooleanFromUser("Are you sure low and parent are the same HPO term?",
                    "Same HPO term for low and parent", "Duplicate HPO alert");
        }

        if (HpoLow != null && HpoHigh != null && !HpoLow.trim().isEmpty() && stringEquals(HpoLow, HpoHigh)) {
            //alert: low and high are same!
            issueDetected = true;
            userConfirmed = PopUps.getBooleanFromUser("Are you sure low and high are the same HPO term?",
                    "Same HPO term for low and high", "Duplicate HPO alert");
        }

        if (HpoNorm != null && HpoHigh != null && !HpoNorm.trim().isEmpty() && stringEquals(HpoNorm, HpoHigh)) {
            //alert: norm and high are the same!
            issueDetected = true;
            userConfirmed = PopUps.getBooleanFromUser("Are you sure parent and high are the same HPO term?",
                    "Same HPO term for parent and high", "Duplicate HPO alert");
        }
        HashMap<String, Boolean> results = new HashMap<>();
        results.put("issueDetected", issueDetected);
        results.put("userconfirmed", userConfirmed);
        return results;

    }

    private String stripEN(String hpoTerm) {
        if (hpoTerm.trim().toLowerCase().endsWith("@en")) {
            return hpoTerm.trim().substring(0, hpoTerm.length() - 3);
        } else {
            return hpoTerm.trim();
        }
    }

    /**
     * Determine whether two strings are identical (case insensitive, no space before and after string)
     * @param x
     * @param y
     * @return
     */
    private boolean stringEquals(String x, String y) {
        return x.trim().toLowerCase().equals(y.trim().toLowerCase());
    }

    
    private void showErrorOfMapping(String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Failure");
        errorAlert.setContentText(message + "could not be mapped. There is nothing to do from user. Contact developer.");
        errorAlert.showAndWait();
    }

    private void showSuccessOfMapping(String message) {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("Success");
        infoAlert.setContentText(message);
        infoAlert.showAndWait();
        long currentTime = System.currentTimeMillis();
        long delay = currentTime + 1000;
        while (currentTime < delay) {
            currentTime = System.currentTimeMillis();
        }
        infoAlert.close();
    }

    @FXML
    private void handleDragOver(DragEvent e){
        Dragboard db = e.getDragboard();
        if (db.hasString()) {
            e.acceptTransferModes(TransferMode.MOVE);
        }

        logger.info("Drag over. Nothing specific todo");
        e.consume();

    }

    @FXML
    private void handleDragDone(DragEvent e) {

        logger.info("Drag done. Nothing specific todo");

    }

    @FXML
    private void handleDragInTreeView(MouseEvent e) {
        System.out.println("Drag event detected");
        Dragboard db = treeView.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        Object selectedItem = treeView.getSelectionModel().getSelectedItem().getValue();
        if (selectedItem instanceof HPO_TreeView) {
            content.putString(((HPO_TreeView) selectedItem)
                    .getHpo_class_found().getLabel());
            db.setContent(content);
        } else {
            logger.info("Dragging something that is not a HPO term");
        }
        e.consume();
    }

    @FXML
    void handleDragEnterHighAbnorm(DragEvent event) {

        annotationTextFieldRight.setStyle("-fx-background-color: LIGHTBLUE;");
        event.consume();

    }

    @FXML
    void handleDragEnterLowAbnorm(DragEvent event) {
        annotationTextFieldLeft.setStyle("-fx-background-color: LIGHTBLUE;");
        event.consume();

    }

    @FXML
    void handleDragEnterParentAbnorm(DragEvent event) {
        annotationTextFieldMiddle.setStyle("-fx-background-color: LIGHTBLUE;");
        event.consume();

    }

    @FXML
    void handleDragExitHighAbnorm(DragEvent event) {

        annotationTextFieldRight.setStyle("-fx-background-color: WHITE;");
        event.consume();

    }

    @FXML
    void handleDragExitLowAbnorm(DragEvent event) {
        annotationTextFieldLeft.setStyle("-fx-background-color: WHITE;");
        event.consume();
    }

    @FXML
    void handleDragExitParentAbnorm(DragEvent event) {
        annotationTextFieldMiddle.setStyle("-fx-background-color: WHITE;");
        event.consume();
    }
    @FXML
    void handleFlagForAnnotation(ActionEvent event) {

    }

    //change the color of rows to green after the loinc code has been annotated
    protected void changeColorLoincTableView(){
/**
        loincIdTableColumn.setCellFactory(x -> new TableCell<LoincEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty){
                super.updateItem(item, empty);
                if(item != null && !empty) {
                    setText(item);
                    if(model.getTestmap().containsKey(item)) {
                        logger.info("model contains " + item);
                        logger.info("num of items in model " + model.getTestmap().size());
                        //TableRow<LoincEntry> currentRow = getTableRow();
                        //currentRow.setStyle("-fx-background-color: lightblue");
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-background-color: lightblue");
                    }
                }
            }

        });
 **/
    }

/**
 * The program never go to setRowFactory. WHy?
    //change the color of rows to green after the loinc code has been annotated
    protected void changeColorLoincTableView(){
        logger.debug("enter changeColorLoincTableView");
        logger.info("model size: " + model.getTestmap().size());

        loincTableView.setRowFactory(x -> new TableRow<LoincEntry>() {
            @Override
            protected void updateItem(LoincEntry item, boolean empty){
                super.updateItem(item, empty);
                logger.info("row loinc num: " + item.getLOINC_Number());

                //if(item != null && !empty && model.getTestmap().containsKey(item.getLOINC_Number())) {
                if(item != null && !empty) {
                        logger.info("model contains " + item);
                        logger.info("num of items in model " + model.getTestmap().size());
                        //TableRow<LoincEntry> currentRow = getTableRow();
                        setStyle("-fx-background-color: lightblue");

                }
            }

        });
        logger.debug("exit changeColorLoincTableView");
    }
**/




    private ObservableList<Annotation> tempAdvancedAnnotations = FXCollections.observableArrayList();
    @FXML
    private void handleAdvancedAnnotationButton(ActionEvent e) {
        e.consume();
        //createTempAnnotation();
        if (!advancedAnnotationModeSelected) recordTempTerms(tempTerms);//Important: Save annotation data at basic mode
        advancedAnnotationModeSelected = ! advancedAnnotationModeSelected;
        if (advancedAnnotationModeSelected) {
            switchToAdvancedAnnotationMode();
        } else {
            switchToBasicAnnotationMode();
        }

    }

    private void switchToAdvancedAnnotationMode(){
        //before switching to advanced mode, save any data in the basic mode

        annotationLeftLabel.setText("system");
        annotationMiddleLabel.setText("code");
        annotationRightLabel.setText("hpo term");
        annotationTextFieldLeft.clear();
        annotationTextFieldMiddle.clear();
        annotationTextFieldRight.clear();
        annotationTextFieldLeft.setPromptText("code system");
        annotationTextFieldMiddle.setPromptText("code");
        annotationTextFieldRight.setPromptText("candidate HPO");
        modeButton.setText("<<<basic");
        inverseChecker.setSelected(false);

    }

    private void switchToBasicAnnotationMode(){
        annotationLeftLabel.setText("<Low threshold");
        annotationMiddleLabel.setText("intermediate");
        annotationRightLabel.setText(">High threshold");
        annotationTextFieldLeft.clear();
        annotationTextFieldMiddle.clear();
        annotationTextFieldRight.clear();
        annotationTextFieldLeft.setPromptText("hpo for low value");
        annotationTextFieldMiddle.setPromptText("hpo for mid value");
        annotationTextFieldRight.setPromptText("hpo for high value");
        modeButton.setText("advanced>>>");
        inverseChecker.setSelected(true);
        if (!tempTerms.isEmpty()) {
            annotationTextFieldLeft.setText(tempTerms.get("hpoLo"));
            annotationTextFieldMiddle.setText(tempTerms.get("hpoNormal"));
            annotationTextFieldRight.setText(tempTerms.get("hpoHi"));
        }
    }

    @FXML
    private void handleAnnotateCodedValue(ActionEvent e){
        e.consume();

        if (!advancedAnnotationModeSelected) return; //do nothing if it is the basic mode

        Annotation annotation = null;
        String system = annotationTextFieldLeft.getText().trim().toLowerCase();
        String codeId = annotationTextFieldMiddle.getText().trim(); //case sensitive
        Code code = null;
        if (system != null && !system.isEmpty() && codeId != null && !codeId.isEmpty()) {
            code = Code.getNewCode().setSystem(system).setCode(codeId);
        }
        String candidateHPO = annotationTextFieldRight.getText();
        HpoTerm hpoterm = model.getTermMap().get(candidateHPO);
        if (hpoterm == null) logger.error("hpoterm is null");
        if (code != null && hpoterm != null) {
            annotation = new Annotation(code, candidateHPO, new HpoTermId4LoincTest(hpoterm.getId(), inverseChecker.isSelected()));
        }
        tempAdvancedAnnotations.add(annotation);
        //add annotated value to the advanced table view
        initadvancedAnnotationTable();
        accordion.setExpandedPane(advancedAnnotationTitledPane);
        inverseChecker.setSelected(false);
    }


    private void initadvancedAnnotationTable(){

        advancedAnnotationSystem.setSortable(true);
        advancedAnnotationSystem.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getCode().getSystem()));
        advancedAnnotationCode.setSortable(true);
        advancedAnnotationCode.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getCode().getCode()));
        advancedAnnotationHpo.setSortable(true);
        advancedAnnotationHpo.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getHpo_term()));

        advancedAnnotationTable.setItems(tempAdvancedAnnotations);
    }




    private String githubUsername;
    private String githubPassword;
    LoincId loincIdSelected=null;
    /**
     * For the GitHub new issues, we want to allow the user to choose a pre-existing label for the issue.
     * For this, we first go to GitHub and retrieve the labels with
     * {@link org.monarchinitiative.loinc2hpo.github.GitHubLabelRetriever}. We only do this
     * once per session though.
     */
    private void initializeGitHubLabelsIfNecessary() {
        if (model.hasLabels()) {
            return; // we only need to retrieve the labels from the server once per session!
        }
        GitHubLabelRetriever retriever = new GitHubLabelRetriever();
        List<String> labels = retriever.getLabels();
        if (labels == null) {
            labels = new ArrayList<>();
        }
        if (labels.size() == 0) {
            labels.add("new term request");
        }
        model.setGithublabels(labels);
    }

    /**
    private void suggestNewChildTerm(ActionEvent e) {
        if (getSelectedTerm() == null) {
            logger.error("Select a term before creating GitHub issue");
            PopUps.showInfoMessage("Please select an HPO term before creating GitHub issue",
                    "Error: No HPO Term selected");
            return;
        } else {
            selectedTerm = getSelectedTerm().getValue().term;
        }
        GitHubPopup popup = new GitHubPopup(selectedTerm, true);
        initializeGitHubLabelsIfNecessary();
        popup.setLabels(model.getGithublabels());
        popup.setupGithubUsernamePassword(githubUsername, githubPassword);
        popup.displayWindow(primarystage);
        String githubissue = popup.retrieveGitHubIssue();
        if (githubissue == null) {
            logger.trace("got back null github issue");
            return;
        }
        String title = String.format("Suggesting new child term of \"%s\"", selectedTerm.getName());
        postGitHubIssue(githubissue, title, popup.getGitHubUserName(), popup.getGitHubPassWord());
    }
     **/

    @FXML
    private void suggestNewTerm(ActionEvent e) {
        e.consume();
        initializeGitHubLabelsIfNecessary();
        LoincEntry loincEntrySelected = loincTableView.getSelectionModel().getSelectedItem();
        if (loincEntrySelected == null) {

            logger.error("Select a loinc code before making a suggestion");
            PopUps.showInfoMessage("Please select a loinc code before creating GitHub issue",
                    "Error: No HPO Term selected");
            return;
        }
        loincIdSelected = loincEntrySelected.getLOINC_Number();
        logger.info("Selected loinc to create github issue for: " + loincIdSelected);

        GitHubPopup popup = new GitHubPopup(loincEntrySelected);
        initializeGitHubLabelsIfNecessary();
        popup.setLabels(model.getGithublabels());
        popup.setupGithubUsernamePassword(githubUsername, githubPassword);
        popup.displayWindow(Main.getPrimarystage());
        String githubissue = popup.retrieveGitHubIssue();
        if (githubissue == null) {
            logger.trace("got back null github issue");
            return;
        }
        String title = String.format("Suggesting new term for Loinc:  \"%s\"", loincIdSelected);
        postGitHubIssue(githubissue, title, popup.getGitHubUserName(), popup.getGitHubPassWord());
    }

    private void postGitHubIssue(String message, String title, String uname, String pword) {
        GitHubPoster poster = new GitHubPoster(uname, pword, title, message);
        this.githubUsername = uname;
        this.githubPassword = pword;
        try {
            poster.postIssue();
        } catch (NetPostException he) {
            PopUps.showException("GitHub error", "Bad Request (400): Could not post issue", he);
        } catch (Exception ex) {
            PopUps.showException("GitHub error", "GitHub error: Could not post issue", ex);
            return;
        }
        String response = poster.getHttpResponse();
        PopUps.showInfoMessage(
                String.format("Created issue for %s\nServer response: %s", loincIdSelected.toString(), response), "Created new issue");

    }




}
