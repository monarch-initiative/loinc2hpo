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
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.gui.PopUps;
import org.monarchinitiative.loinc2hpo.gui.WidthAwareTextFields;
import org.monarchinitiative.loinc2hpo.io.LoincOfInterest;
import org.monarchinitiative.loinc2hpo.io.OntologyModelBuilderForJena;
import org.monarchinitiative.loinc2hpo.loinc.AnnotatedLoincRangeTest;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.model.Model;
import org.monarchinitiative.loinc2hpo.util.HPO_Class_Found;
import org.monarchinitiative.loinc2hpo.util.LoincCodeClass;
import org.monarchinitiative.loinc2hpo.util.LoincLongNameParser;
import org.monarchinitiative.loinc2hpo.util.SparqlQuery;


import javax.annotation.Nullable;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Timer;
import java.util.regex.Pattern;


@Singleton
public class AnnotateTabController {
    private static final Logger logger = LogManager.getLogger();

    private Model model=null;
    /** Reference to the third tab. When the user adds a new annotation, we update the table, therefore, we need a reference. */
    @Inject private Loinc2HpoAnnotationsTabController loinc2HpoAnnotationsTabController;
    private ImmutableMap<String,LoincEntry> loincmap=null;


    @FXML private Button IntializeHPOmodelbutton;
    @FXML private Button initLOINCtableButton;
    @FXML private Button searchForLOINCIdButton;
    @FXML private Button createAnnotationButton;
    @FXML private TextField loincSearchTextField;
    @FXML private TextField loincStringSearchTextField;
    @FXML private Button filterLoincTableByList;
    @FXML private TextField LoincFilterField;
    @FXML private TextField userInputForManualQuery;

    //drag and drop to the following fields
    @FXML private TextField hpoLowAbnormalTextField;
    @FXML private TextField hpoNotAbnormalTextField;
    @FXML private TextField hpoHighAbnormalTextField;
    private HPO_Class_Found hpo_drag_and_drop;
    //private ImmutableMap<String, HPO_Class_Found> selectedHPOforAnnotation;

    private ImmutableMap<String,HpoTerm> termmap;

    @FXML private ListView hpoListView;



    @FXML private TableView<LoincEntry> loincTableView;
    @FXML private TableColumn<LoincEntry, String> loincIdTableColumn;
    @FXML private TableColumn<LoincEntry, String> componentTableColumn;
    @FXML private TableColumn<LoincEntry, String> propertyTableColumn;
    @FXML private TableColumn<LoincEntry, String> timeAspectTableColumn;
    @FXML private TableColumn<LoincEntry, String> methodTableColumn;
    @FXML private TableColumn<LoincEntry, String> scaleTableColumn;
    @FXML private TableColumn<LoincEntry, String> systemTableColumn;
    @FXML private TableColumn<LoincEntry, String> nameTableColumn;

    //candidate HPO classes found by Sparql query
    //@FXML private TableView<HPO_Class_Found> candidateHPOList;
    //@FXML private TableColumn<HPO_Class_Found, Integer> score;
    //@FXML private TableColumn<HPO_Class_Found, String> id;
    //@FXML private TableColumn<HPO_Class_Found, String> label;
    //@FXML private TableColumn<HPO_Class_Found, String> definition;

    @FXML private TreeView<HPO_TreeView> treeView;

    @FXML private CheckBox flagForAnnotation;
    @FXML private Circle createAnnotationSuccess;

    @FXML private void initialize() {
        if (model != null) {
            setModel(model);
        }
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
        hpoLowAbnormalTextField.setText("");
        hpoHighAbnormalTextField.setText("");
        hpoNotAbnormalTextField.setText("");
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
//        WidthAwareTextFields.bindWidthAwareAutoCompletion(hpoLowAbnormalTextField, termmap.keySet());
//        WidthAwareTextFields.bindWidthAwareAutoCompletion(hpoNotAbnormalTextField, termmap.keySet());
//        WidthAwareTextFields.bindWidthAwareAutoCompletion(hpoHighAbnormalTextField, termmap.keySet());
        logger.trace(String.format("Initializing term map to %d terms",termmap.size()));
    }


    private void initTableStructure() {
        loincIdTableColumn.setSortable(true);
        loincIdTableColumn.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getLOINC_Number())
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
                }
            });
            return row ;
        });
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
        });
        task.setOnFailed(x -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failured to create HPO model");
            alert.setContentText("Check whether hpo.owl is downloaded.");
            alert.showAndWait();
        });
        e.consume();

    }






    @FXML private void searchForSpecificLoincEntry(ActionEvent e) {
        e.consume();
        String s = this.loincSearchTextField.getText().trim();
        LoincEntry entry = this.loincmap.get(s);
        if (entry==null) {
            logger.error(String.format("Could not identify LOINC entry for \"%s\"",s));
            PopUps.showWarningDialog("LOINC Search", "No hits found", String.format("Could not identify LOINC entry for \"%s\"",s));
            return;
        } else {
            logger.trace(String.format("Searching table for term %s",entry.getLongName()));
        }
        if (termmap==null) initialize(); // set up the Hpo autocomplete if possible
        loincTableView.getItems().clear();
        loincTableView.getItems().add(entry);
    }

    @FXML private void searchLoincByString(ActionEvent e) {
        final String query = this.loincStringSearchTextField.getText().trim();
        if (query==null){
            logger.error("Null query string. Cowardly refusing to search LOINC entries");
            return;
        }
        logger.trace(String.format("Filter LOINC catalog by \"%s\"",query));
        List<LoincEntry> entrylist=new ArrayList<>();
        //The following implements "contains" in a case-insensitive fashion
        loincmap.values().stream().forEach( loincEntry -> {
           // if (Pattern.compile(Pattern.quote(loincEntry.getLongName()),Pattern.CASE_INSENSITIVE).matcher(query).find()){
            if (loincEntry.getLongName().contains(query)) {
                entrylist.add(loincEntry);
            }
        });

        loincTableView.getItems().clear();
        loincTableView.getItems().addAll(entrylist);
        e.consume();
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
            hpoLowAbnormalTextField.setText(e.getDragboard().getString());
        }
        hpoLowAbnormalTextField.setStyle("-fx-background-color: WHITE;");
        e.consume();
    }

    @FXML private void handleHPOHighAbnormality(DragEvent e){

        if (e.getDragboard().hasString()) {
            hpoHighAbnormalTextField.setText(e.getDragboard().getString());
        }
        hpoHighAbnormalTextField.setStyle("-fx-background-color: WHITE;");
        e.consume();

    }

    @FXML private void handleParentAbnormality(DragEvent e){
        if (e.getDragboard().hasString()) {
            hpoNotAbnormalTextField.setText(e.getDragboard().getString());
        }
        hpoNotAbnormalTextField.setStyle("-fx-background-color: WHITE;");
        e.consume();
    }


    @FXML private void createLoinc2HpoAnnotation(ActionEvent e) {
        e.consume();
        String hpoLo,hpoNormal,hpoHi;
        //String loincCode=this.loincSearchTextField.getText();
        String loincCode = loincTableView.getSelectionModel().getSelectedItem
                ().getLOINC_Number();
        String loincScale = loincTableView.getSelectionModel().getSelectedItem().getScale();
        hpoLo = hpoLowAbnormalTextField.getText();
        hpoNormal = hpoNotAbnormalTextField.getText();
        hpoHi= hpoHighAbnormalTextField.getText();


        //We don't have to force every loinc code to have three phenotypes
        HpoTerm low = termmap.get(hpoLo);
        HpoTerm normal = termmap.get(hpoNormal);
        HpoTerm high = termmap.get(hpoHi);

        //Warning user that there is something wrong
        //it happens when something is wrong with hpo termmap (a name could not be mapped)
        if (!hpoLo.trim().isEmpty() && low==null) {
            logger.error(hpoLo + "cannot be mapped to a term");
            createAnnotationSuccess.setFill(Color.RED);
            showErrorOfMapping(hpoLo);
            return;
        }

        if (!hpoHi.trim().isEmpty() && high==null) {
            logger.error(hpoHi + "cannot be mapped to a term");
            createAnnotationSuccess.setFill(Color.RED);
            showErrorOfMapping(hpoHi);
            return;
        }

        if (!hpoNormal.trim().isEmpty() && normal==null) {
            logger.error(hpoNormal + "cannot be mapped to a term");
            createAnnotationSuccess.setFill(Color.RED);
            showErrorOfMapping(hpoNormal);
            return;
        }

        AnnotatedLoincRangeTest test =
                new AnnotatedLoincRangeTest(loincCode,loincScale, low,normal,high, flagForAnnotation.isSelected());
        this.model.addLoincTest(test);
        loinc2HpoAnnotationsTabController.refreshTable();
        createAnnotationSuccess.setFill(Color.GREEN);

        //showSuccessOfMapping("Go to next loinc code!");

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

        hpoHighAbnormalTextField.setStyle("-fx-background-color: LIGHTBLUE;");
        event.consume();

    }

    @FXML
    void handleDragEnterLowAbnorm(DragEvent event) {
        hpoLowAbnormalTextField.setStyle("-fx-background-color: LIGHTBLUE;");
        event.consume();

    }

    @FXML
    void handleDragEnterParentAbnorm(DragEvent event) {
        hpoNotAbnormalTextField.setStyle("-fx-background-color: LIGHTBLUE;");
        event.consume();

    }

    @FXML
    void handleDragExitHighAbnorm(DragEvent event) {

        hpoHighAbnormalTextField.setStyle("-fx-background-color: WHITE;");
        event.consume();

    }

    @FXML
    void handleDragExitLowAbnorm(DragEvent event) {
        hpoLowAbnormalTextField.setStyle("-fx-background-color: WHITE;");
        event.consume();
    }

    @FXML
    void handleDragExitParentAbnorm(DragEvent event) {
        hpoNotAbnormalTextField.setStyle("-fx-background-color: WHITE;");
        event.consume();
    }
    @FXML
    void handleFlagForAnnotation(ActionEvent event) {

    }

}
