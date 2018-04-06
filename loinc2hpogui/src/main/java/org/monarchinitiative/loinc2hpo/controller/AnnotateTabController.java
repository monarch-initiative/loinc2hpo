package org.monarchinitiative.loinc2hpo.controller;


//import apple.laf.JRSUIUtils;
import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.*;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;
import org.monarchinitiative.loinc2hpo.exception.LoincCodeNotFoundException;
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.exception.NetPostException;
import org.monarchinitiative.loinc2hpo.github.GitHubLabelRetriever;
import org.monarchinitiative.loinc2hpo.github.GitHubPoster;
import org.monarchinitiative.loinc2hpo.gui.GitHubPopup;
import org.monarchinitiative.loinc2hpo.gui.Main;
import org.monarchinitiative.loinc2hpo.gui.PopUps;
import org.monarchinitiative.loinc2hpo.io.LoincOfInterest;
import org.monarchinitiative.loinc2hpo.io.OntologyModelBuilderForJena;
import org.monarchinitiative.loinc2hpo.io.WriteToFile;
import org.monarchinitiative.loinc2hpo.loinc.*;
import org.monarchinitiative.loinc2hpo.model.Annotation;
import org.monarchinitiative.loinc2hpo.model.Model;
import org.monarchinitiative.loinc2hpo.util.HPO_Class_Found;
import org.monarchinitiative.loinc2hpo.util.LoincLongNameComponents;
import org.monarchinitiative.loinc2hpo.util.LoincLongNameParser;
import org.monarchinitiative.loinc2hpo.util.SparqlQuery;


import java.io.*;
import java.time.LocalDateTime;
import java.util.*;


@Singleton
public class AnnotateTabController {
    private static final Logger logger = LogManager.getLogger();

    private Model model=null;
    private final String MISSINGVALUE = "NA";

    @Inject
    private Injector injector;

    /** Reference to the third tab. When the user adds a new annotation, we update the table, therefore, we need a reference. */
    @Inject private Loinc2HpoAnnotationsTabController loinc2HpoAnnotationsTabController;
    @Inject private MainController mainController;
    private ImmutableMap<LoincId,LoincEntry> loincmap=null;



    //private final Stage primarystage;
    @FXML private Button initLOINCtableButton;
    @FXML private Button IntializeHPOmodelbutton;
    @FXML private Button filterButton;
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
    @FXML private Button addCodedAnnotationButton;
    private HPO_Class_Found hpo_drag_and_drop;
    //private ImmutableMap<String, HPO_Class_Found> selectedHPOforAnnotation;

    private ImmutableMap<String,HpoTerm> termmap;

    @FXML private ListView hpoListView;
    private ObservableList<HPO_Class_Found> sparqlQueryResult = FXCollections.observableArrayList();



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
    private ObservableList<Annotation> tempAdvancedAnnotations = FXCollections.observableArrayList();

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
    @FXML private Button clearButton;
    @FXML private Button allAnnotationsButton;


    @FXML private Button suggestHPOButton;
    @FXML private ContextMenu contextMenu;

    @FXML private Button autoQueryButton;
    @FXML private Button manualQueryButton;

    @FXML private ContextMenu loincTableContextMenu;
    @FXML private Menu loincListsButton;
    @FXML private MenuItem backMenuItem;
    @FXML private MenuItem forwardMenuItem;
    @FXML private Menu userCreatedLoincListsButton;
    @FXML private Menu exportLoincListButton;
    @FXML private Menu importLoincGroupButton;
    final protected ObservableList<String> userCreatedLoincLists = FXCollections
            .observableArrayList();
    final private String LOINCWAITING4NEWHPO = "require_new_HPO_terms";
    final private String LOINCUNABLE2ANNOTATE = "unable_to_annotate";

    private BooleanProperty isPresentOrd = new SimpleBooleanProperty(false);


    @Inject private CurrentAnnotationController currentAnnotationController;

    @FXML private void initialize() {
        if (model != null) {   //weird line. model is set by main controller; this line never runs
            setModel(model);
            //currentAnnotationController.setModel(model); //let current annotation stage have access to model
        }
        //currentAnnotationController.setModel(model); //let current annotation stage have access to model
        suggestHPOButton.setTooltip(new Tooltip("Suggest new HPO terms"));
        filterButton.setTooltip(new Tooltip("Filter Loinc by providing a Loinc list in txt file"));
        addCodedAnnotationButton.setTooltip(new Tooltip("Add current annotation"));
        flagForAnnotation.setTooltip(new Tooltip("Check if you are not confident"));
        clearButton.setTooltip(new Tooltip("Clear all textfields"));
        allAnnotationsButton.setTooltip(new Tooltip("Display annotations for currently selected Loinc code"));
        initLOINCtableButton.setTooltip(new Tooltip("Initialize Loinc Core Table. Download it first."));
        IntializeHPOmodelbutton.setTooltip(new Tooltip("Load hp.owl as a RDF model for query"));
        searchForLOINCIdButton.setTooltip(new Tooltip("Search Loinc with a Loinc code or name"));
        modeButton.setTooltip(new Tooltip("Switch between basic and advanced annotation mode"));
        autoQueryButton.setTooltip(new Tooltip("Find candidate HPO terms with automatically generated keys"));
        manualQueryButton.setTooltip(new Tooltip("Find candidate HPO terms with manually typed keys"));

        hpoListView.setCellFactory(new Callback<ListView<HPO_Class_Found>, ListCell<HPO_Class_Found>>(){
            @Override
            public ListCell<HPO_Class_Found> call(ListView<HPO_Class_Found> param) {
                return new ListCell<HPO_Class_Found>() {
                    @Override
                    public void updateItem(HPO_Class_Found hpo, boolean empty){
                        super.updateItem(hpo, empty);
                        if (hpo != null) {
                            setText(hpo.toString());
                            Tooltip tooltip = new Tooltip(hpo.getDefinition());
                            tooltip.setPrefWidth(300);
                            tooltip.setWrapText(true);
                            setTooltip(tooltip);
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });


        treeView.setCellFactory(new Callback<TreeView<HPO_TreeView>, TreeCell<HPO_TreeView>>() {
            @Override
            public TreeCell<HPO_TreeView> call(TreeView<HPO_TreeView> param) {
                return new TreeCell<HPO_TreeView>() {
                    @Override
                    public void updateItem(HPO_TreeView hpo, boolean empty){
                        super.updateItem(hpo, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            if (hpo != null && hpo.hpo_class_found == null) {
                                setText("root");
                            }
                            if (hpo != null && hpo.hpo_class_found != null) {
                                setText(hpo.toString());
                                if (hpo.hpo_class_found.getDefinition() != null) {
                                    Tooltip tooltip = new Tooltip(hpo.hpo_class_found.getDefinition());
                                    tooltip.setPrefWidth(300);
                                    tooltip.setWrapText(true);
                                    setTooltip(tooltip);
                                }
                            }
                        }
                    }
                };
            }
        });

        //if user creates a new Loinc group, add two menuitems for it, and specify the actions when those menuitems are
        //clicked
        userCreatedLoincLists.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        logger.trace(c + " was added");
                        c.getAddedSubList()
                            .stream()
                            .filter(p -> !model.getUserCreatedLoincLists().containsKey(p))
                            .forEach(p -> {

                                model.addUserCreatedLoincList(p, new LinkedHashSet<>());

                                MenuItem newListMenuItem = new MenuItem(p);
                                userCreatedLoincListsButton.getItems().add(newListMenuItem);
                                newListMenuItem.setOnAction((event -> {
                                    logger.trace("action detected");
                                    if (loincTableView.getSelectionModel().getSelectedItem()!=null) {
                                        LoincId loincId = loincTableView.getSelectionModel()
                                                .getSelectedItem().getLOINC_Number();
                                        if (model.getUserCreatedLoincLists().get(p).contains(loincId)) {
                                            model.getUserCreatedLoincLists().get(p)
                                                    .remove(loincId);
                                            logger.trace(String.format("LOINC: %s removed from %s", loincId, p));
                                        } else {
                                            model.getUserCreatedLoincLists().get(p)
                                                    .add(loincId);
                                            logger.trace(String.format("LOINC: %s added to %s", loincId, p));
                                        }

                                        changeColorLoincTableView();
                                        model.setSessionChanged(true);
                                    }
                                }));

                                MenuItem newExportMenuItem = new MenuItem(p);
                                exportLoincListButton.getItems().add(newExportMenuItem);
                                newExportMenuItem.setOnAction((event -> {
                                    logger.trace("action detected");
                                    if (loincTableView.getSelectionModel().getSelectedItem()!=null) {
                                        Set<LoincId> loincIds = model.getUserCreatedLoincLists().get(p);
                                        if (loincIds.isEmpty()) {
                                            return;
                                        }
                                        FileChooser chooser = new FileChooser();
                                        chooser.setTitle("Save Loinc List: ");
                                        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TSV files (*.txt)", "*.txt"));
                                        chooser.setInitialFileName(p);
                                        File f = chooser.showSaveDialog(null);
                                        String filepath;
                                        if (f == null) {
                                            return;
                                        } else {
                                          filepath = f.getAbsolutePath();
                                        }

                                        StringBuilder builder = new StringBuilder();
                                        loincIds.forEach(l -> {
                                            builder.append (l);
                                            builder.append("\n");
                                        });

                                        WriteToFile.writeToFile(builder.toString().trim(), filepath);
                                    }
                                }));

                                MenuItem newImportMenuItem = new MenuItem(p);
                                importLoincGroupButton.getItems().add(newImportMenuItem);
                                newImportMenuItem.setOnAction((event) -> {
                                    logger.trace("user wants to import " + p);
                                    FileChooser chooser = new FileChooser();
                                    chooser.setTitle("Select file to import from");
                                    File f = chooser.showOpenDialog(null);
                                    if (f == null) {
                                        return;
                                    }
                                    List<String> malformed = new ArrayList<>();
                                    List<String> notFound = new ArrayList<>();
                                    try {
                                        LoincOfInterest loincSet = new LoincOfInterest(f.getAbsolutePath());
                                        Set<String> loincIds = loincSet.getLoincOfInterest();
                                        loincIds.forEach(l -> {
                                            LoincId loincId = null;
                                            try {
                                                loincId = new LoincId(l);
                                            } catch (MalformedLoincCodeException e) {
                                                malformed.add(l);
                                            }
                                            if (model.getLoincEntryMap().containsKey(loincId)) {
                                                model.getUserCreatedLoincLists().get(p).add(loincId);
                                            } else {
                                                notFound.add(l);
                                            }
                                            changeColorLoincTableView();

                                        });
                                    } catch (FileNotFoundException e) {
                                        logger.error("File not found. Should never happen");
                                    }
                                    if (!malformed.isEmpty() || !notFound.isEmpty()) {
                                        String malformedString = String.join("\n", malformed);
                                        String notFoundString = String.join("\n", notFound);
                                        PopUps.showInfoMessage(String.format("Malformed Loinc: %d\n%s\nNot Found: %d\n%s", malformed.size(), malformedString, notFound.size(), notFoundString), "Error during importing");
                                    }

                                });
                        });
                    } else {
                        logger.error("This should never happen");
                    }
                }
            }
        });

        initadvancedAnnotationTable();
        loincTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> isPresentOrd.setValue(newValue.isPresentOrd()));

        isPresentOrd.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                switchToBasicAnnotationMode();
            }
        });


    }

    protected void defaultStartUp() {
        initLOINCtable(null);
        Platform.runLater(()->initHPOmodelButton(null));
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

        accordion.setExpandedPane(loincTableTitledpane);
    }

    @FXML
    private void handleDoubleClickLoincTable(MouseEvent event) {
        if (event.getClickCount() == 2 ) {
            LoincEntry rowData = loincTableView.getSelectionModel().getSelectedItem();
            if (rowData == null) {
                return;
            }

            //disable further action if the user is not under Editing mode
            if(model.getLoincUnderEditing() != null && !model.getLoincUnderEditing().equals(rowData)){
                PopUps.showInfoMessage("You are currently editing " + rowData.getLOINC_Number() +
                                ". Save or cancel editing current loinc annotation before switching to others",
                        "Under Editing mode");
            } else {
                updateHpoTermListView(rowData);
            }

        }

        //clear text in abnormality text fields if not currently editing a term
        if (!createAnnotationButton.getText().equals("Save")) { //under saving mode
            clearAbnormalityTextField();
            //inialize the flag field
            flagForAnnotation.setIndeterminate(false);
            flagForAnnotation.setSelected(false);
            createAnnotationSuccess.setFill(Color.WHITE);
            annotationNoteField.setText("");
        }
        event.consume();
    }


    private void updateHpoTermListView(LoincEntry entry) {
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
        hpoListView.getItems().clear();
        sparqlQueryResult.clear();
        sparqlQueryResult.addAll(SparqlQuery.query_auto(name));
        sparqlQueryResult.sort((o1, o2) -> o2.getScore() - o1.getScore());
        //SparqlQuery.query_auto(name).stream().forEach(sparqlQueryResult::add);
        logger.trace("sparqlQueryResult size: " + sparqlQueryResult.size());
        if (sparqlQueryResult.size() == 0) {
            String noHPOfoundMessage = "0 HPO class is found. Try manual search with " +
                    "alternative keys (synonyms)";
            sparqlQueryResult.add(new HPO_Class_Found(noHPOfoundMessage, null, null, null));
        }
        hpoListView.setItems(sparqlQueryResult);
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
        if (model.getLoincUnderEditing() == null || //not under Editing mode
                //or query the loinc code under editing
                (model.getLoincUnderEditing() != null && model.getLoincUnderEditing().equals(entry))) {
            updateHpoTermListView(entry);
        } else {
            PopUps.showInfoMessage("You are currently editing " + model.getLoincUnderEditing().getLOINC_Number() +
                            ". Save or cancel editing current loinc annotation before switching to others",
                    "Under Editing mode");
            return;
        }


        //clear text in abnormality text fields if not currently editing a term
        if (!createAnnotationButton.getText().equals("Save")) {
            clearAbnormalityTextField();
            //inialize the flag field
            flagForAnnotation.setIndeterminate(false);
            flagForAnnotation.setSelected(false);
            createAnnotationSuccess.setFill(Color.WHITE);
            annotationNoteField.setText("");
        }
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


        if (model.getLoincUnderEditing() != null && !model.getLoincUnderEditing().equals(entry)) {

            PopUps.showInfoMessage("You are currently editing " + model.getLoincUnderEditing().getLOINC_Number() +
                            ". Save or cancel editing current loinc annotation before switching to others",
                    "Under Editing mode");
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
        LoincLongNameComponents loincLongNameComponents = LoincLongNameParser.parse(name);
        List<HPO_Class_Found> queryResults = SparqlQuery.query_manual
                (keysInList, loincLongNameComponents);
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
        //clear text in abnormality text fields if not currently editing a term
        if (!createAnnotationButton.getText().equals("Save")) {
            //Got user feedback that they do not want to clear the field when doing manual query
            //clearAbnormalityTextField();
            //inialize the flag field
            flagForAnnotation.setIndeterminate(false);
            flagForAnnotation.setSelected(false);
            createAnnotationSuccess.setFill(Color.WHITE);
            annotationNoteField.setText("");
        }
    }

    @FXML private void initLOINCtable(ActionEvent e) {
        logger.trace("init LOINC table");
        String loincCoreTableFile=model.getPathToLoincCoreTableFile();
        if (loincCoreTableFile==null) {
            PopUps.showWarningDialog("Error", "File not found", "Could not find LOINC Core Table file. Set the path first");
            return;
        }
        this.loincmap = LoincEntry.getLoincEntryList(loincCoreTableFile);
        model.setLoincEntryMap(this.loincmap);
        int limit=Math.min(loincmap.size(),1000); // we will show just the first 1000 entries in the table.
        List<LoincEntry> lst = loincmap.values().asList().subList(0,limit);
        loincTableView.getItems().clear(); // remove any previous entries
        loincTableView.getItems().addAll(lst);
        loincTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        initTableStructure();
        if (e != null) {
            e.consume();
        }

    }

    @FXML private void initHPOmodelButton(ActionEvent e){

        String pathToHPO = this.model.getPathToHpoOwlFile();
        logger.info("pathToHPO: " + pathToHPO);
        //org.apache.jena.rdf.model.Model hpoModel = SparqlQuery.getOntologyModel(pathToHPO);
        //SparqlQuery.setHPOmodel(hpoModel);
        // The following codes run nicely from IDE, but fails in Jar.
        //create a task to create HPO model
        Task<org.apache.jena.rdf.model.Model> task = new OntologyModelBuilderForJena(pathToHPO);
        //Platform.runLater(new Thread(task)::start);
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
            alert.setTitle("Failed to create HPO model");
            alert.setContentText("Check whether hpo.owl is downloaded. Path to hpo.owl is set to: " + pathToHPO);
            IntializeHPOmodelbutton.setStyle("-fx-background-color: #ff0000");
            IntializeHPOmodelbutton.setText("Retry");
            alert.showAndWait();
        });

        if (e != null) {
            e.consume();
        }


    }

    @FXML private void search(ActionEvent e) {
        e.consume();
        String query = this.loincSearchTextField.getText().trim();
        if (query.isEmpty()) return;
        List<LoincEntry> entrylist=new ArrayList<>();
        try {
            LoincId loincId = new LoincId(query);
            if (this.loincmap.containsKey(loincId)) {
                entrylist.add(this.loincmap.get(loincId));
                logger.debug(this.loincmap.get(loincId).getLOINC_Number() + " : " + this.loincmap.get(loincId).getLongName());
            } else { //correct loinc code form but not valid
                throw new LoincCodeNotFoundException();
            }
        } catch (Exception msg) { //catch all kind of exception
            loincmap.values().stream()
                    .filter( loincEntry -> containedIn(query, loincEntry.getLongName()))
                    .forEach(loincEntry -> {
                        entrylist.add(loincEntry);
                        logger.debug(loincEntry.getLOINC_Number() + " : " + loincEntry.getLongName());
                    });
                    //.forEach(loincEntry -> entryListInOrder.add(loincEntry));
        }
        if (entrylist.isEmpty()) {
        //if (entryListInOrder.isEmpty()){
            logger.error(String.format("Could not identify LOINC entry for \"%s\"",query));
            PopUps.showWarningDialog("LOINC Search",
                    "No hits found",
                    String.format("Could not identify LOINC entry for \"%s\"",query));
            return;
        } else {
            logger.trace(String.format("Searching table for:  %s",query));
            logger.trace("# of loinc entries found: " + entrylist.size());
            //logger.trace("# of loinc entries found: " + entryListInOrder.size());
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

        List<LoincEntry> entrylist=new ArrayList<>();
        String enlistName = null;
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose File containing a list of interested Loinc " +
                "codes");
        File f = chooser.showOpenDialog(null);
        List<String> notFoundList = new ArrayList<>();
        List<String> malformedList = new ArrayList<>();
        int malformedLoincCount = 0;
        if (f != null) {
            String path = f.getAbsolutePath();
            enlistName = f.getName();
            try {
                Set<String> loincOfInterest = new LoincOfInterest(path).getLoincOfInterest();
                //loincOfInterest.stream().forEach(System.out::print);
                for (String loincString : loincOfInterest) {
                    LoincId loincId = null;
                    LoincEntry loincEntry = null;
                    try {
                        loincId = new LoincId(loincString);
                        loincEntry = model.getLoincEntryMap().get(loincId);
                    } catch (MalformedLoincCodeException e2) {
                        //try to see whether user provided Loinc long common name
                        if (model.getLoincEntryMapWithName().get(loincString) != null) {
                            loincEntry = model.getLoincEntryMapWithName().get(loincString);
                        } else {
                            logger.error("Malformed loinc");
                            malformedList.add(loincString);
                            continue;
                        }
                    }
                    if (loincEntry != null) {
                        entrylist.add(loincEntry);
                    } else {
                        notFoundList.add(loincString);
                    }
                }
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }

            if (!malformedList.isEmpty() || !notFoundList.isEmpty()) {
                String malformed = String.join(",\n", malformedList);
                String notfound = String.join(",\n", notFoundList);
                String popupMessage = String.format("# malformed Loinc codes: %d\n %s\n\n# Loinc codes not found: %d\n%s",
                        malformedList.size(), malformed, notFoundList.size(), notfound);
                PopUps.showInfoMessage(popupMessage, "Incomplete import of Loinc codes");
            }
            if (entrylist.isEmpty()) {
                logger.error(String.format("Found 0 Loinc codes"));
                PopUps.showWarningDialog("LOINC filtering",
                        "No hits found",
                        "Could not find any loinc codes");
                return;
            } else {
                logger.trace("Loinc filtering result: ");
                logger.trace("# of loinc entries found: " + entrylist.size());
            }

            if (termmap==null) initialize(); // set up the Hpo autocomplete if possible
            loincTableView.getItems().clear();
            loincTableView.getItems().addAll(entrylist);
            model.addFilteredList(enlistName, new ArrayList<>(entrylist)); //keep a record in model
            //entrylist.forEach(p -> logger.trace(p.getLOINC_Number()));
            accordion.setExpandedPane(loincTableTitledpane);
        } else {
            logger.error("Unable to obtain path to LOINC of interest file");
            return;
        }
    }

    @FXML
    private void lastLoincList(ActionEvent e) {

        e.consume();
        List<LoincEntry> lastLoincList = model.previousLoincList();
        if (lastLoincList != null && !lastLoincList.isEmpty()) {
            loincTableView.getItems().clear();
            loincTableView.getItems().addAll(lastLoincList);
        }
    }

    @FXML
    private void nextLoincList(ActionEvent e) {
        e.consume();

        List<LoincEntry> nextLoincList = model.nextLoincList();
        if (nextLoincList != null && !nextLoincList.isEmpty()) {
            loincTableView.getItems().clear();
            loincTableView.getItems().addAll(nextLoincList);
        }
    }

    @FXML
    private void newLoincList(ActionEvent e) {

        e.consume();
        String nameOfList= PopUps.getStringFromUser("New Loinc List", "Type in the name", "name");
        if (nameOfList == null) {
            return;
        }
        //model.addUserCreatedLoincList(nameOfList, new LinkedHashSet<>());
        userCreatedLoincLists.add(nameOfList);

    }

    private void initializeUserCreatedLoincListsIfNecessary(){
        //execute the functionalities only once in each secession
        if (!model.getUserCreatedLoincLists().isEmpty()) {
            logger.trace("initializeUserCreatedLoincListsIfNecessary(): 1111");
            return;
        }
        //by default, there will be two user created lists
        List<String> initialListNames = new ArrayList<>();
        initialListNames.add(LOINCWAITING4NEWHPO);
        initialListNames.add(LOINCUNABLE2ANNOTATE);
        userCreatedLoincLists.addAll(initialListNames);
        logger.trace("initializeUserCreatedLoincListsIfNecessary(): 2222");
        /**
        //create a menuitem for each and add to two menus; also create a list to record data
        userCreatedLoincListsButton.getItems().clear();
        exportLoincListButton.getItems().clear();
        initialListNames.forEach(p -> {
            userCreatedLoincListsButton.getItems().add(new MenuItem(p));
            exportLoincListButton.getItems().add(new MenuItem(p));
            model.addUserCreatedLoincList(p, new ArrayList<>());
        });
         **/
    }


    private void initializeMenuItemsForFilteredLists() {
        if (!model.getFilteredLoincListsMap().isEmpty()) {
            loincListsButton.setDisable(false);
            loincListsButton.getItems().clear();
            List<MenuItem> menuItems = new ArrayList<>();
            model.getFilteredLoincListsMap().keySet().stream().forEach(p -> {
                MenuItem menuItem = new MenuItem(p);
                menuItems.add(menuItem);
            });
            loincListsButton.getItems().addAll(menuItems);
            logger.trace("menu items added");
            //loincListsButton.getItems().forEach(p -> logger.trace("current: " + p.getText()));
            loincListsButton.getItems().forEach(p -> p.setOnAction((event) -> {
                logger.trace(p.getText());
                List<LoincEntry> loincList = model.getLoincList(p.getText());
                if (loincList != null && !loincList.isEmpty()) {
                    loincTableView.getItems().clear();
                    loincTableView.getItems().addAll(loincList);
                }
            } ));

        } else {
            loincListsButton.setDisable(true);
        }
    }

    @FXML
    private void buildContextMenuForLoinc(Event e) {
        e.consume();
        logger.trace("context memu for loinc table requested");
        initializeMenuItemsForFilteredLists();
        initializeUserCreatedLoincListsIfNecessary();
        logger.trace("exit buildContextMenuForLoinc()");
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

            TreeItem<HPO_TreeView> rootItem = new TreeItem<>(new HPO_TreeView()); //dummy root node
            rootItem.setExpanded(true);
            TreeItem<HPO_TreeView> current = new TreeItem<>
                    (new HPO_TreeView(hpo_class_found));

            parents.stream() //add parent terms to root; add current to each parent term
                    .map(p -> new TreeItem<>(new HPO_TreeView(p)))
                    .forEach(p -> {
                        rootItem.getChildren().add(p);
                        p.getChildren().add(current);
                        p.setExpanded(true);
                    });
            current.setExpanded(true);
            children.stream() //add child terms to current
                    .map(p -> new TreeItem<>(new HPO_TreeView(p)))
                    .forEach(current.getChildren()::add);

            this.treeView.setRoot(rootItem);
        }
        e.consume();
    }

    @FXML private void doubleClickTreeView(MouseEvent e) {

        if (e.getClickCount() == 2
                && this.treeView.getRoot() != null) {
            TreeItem<HPO_TreeView> current = this.treeView.getSelectionModel().getSelectedItem();
            if (current == null || current.getValue() == null
                    || current.getValue().hpo_class_found == null) {
                return;
            }
            List<HPO_Class_Found> parents = SparqlQuery.getParents
                    (current.getValue().hpo_class_found.getId());
            List<HPO_Class_Found> children = SparqlQuery.getChildren
                    (current.getValue().hpo_class_found.getId());

            TreeItem<HPO_TreeView> rootItem = this.treeView.getRoot();
            rootItem.setExpanded(true);

            if (parents.size() > 0 || children.size() > 0) {
                rootItem.getChildren().clear();
            }

            parents.stream()
                    .map(p -> new TreeItem<>(new HPO_TreeView(p)))
                    .forEach(p -> {
                        rootItem.getChildren().add(p);
                        p.getChildren().add(current);
                        p.setExpanded(true);
                    });
            current.getChildren().clear();
            current.setExpanded(true);
            children.stream()
                    .map(p -> new TreeItem<>(new HPO_TreeView(p)))
                    .forEach(current.getChildren()::add);
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



    /**
     * Record the terms for basic annotation
     */
    private Map<String, String> recordTempTerms(){
        Map<String, String> temp = new HashMap<>();
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
        return temp;
    }

    private Map<String, String> recordAdvancedAnnotation(){
        Map<String, String> temp = new HashMap<>();
        String system = annotationTextFieldLeft.getText();
        if (system!= null && !system.trim().isEmpty())
            temp.put("system", system);
        String code = annotationTextFieldMiddle.getText();
        if (code != null && !code.trim().isEmpty())
            temp.put("code", code);
        String hpoTerm= annotationTextFieldRight.getText();
        if (hpoTerm != null && !hpoTerm.isEmpty()) {
            temp.put("hpoTerm", hpoTerm);
        }
        return temp;
    }

    private boolean recordInversed() {
        return inverseChecker.isSelected();
    }


    @FXML private void createLoinc2HpoAnnotation(ActionEvent e) {


        if (loincTableView.getSelectionModel().getSelectedItem() == null) {
            PopUps.showInfoMessage("No loinc entry is selected. Try clicking \"Initialize Loinc Table\"", "No Loinc selection Error");
            return;
        }
        //specify loinc for the annotation
        LoincEntry loincEntry = loincTableView.getSelectionModel().getSelectedItem();
        LoincId loincCode = loincEntry.getLOINC_Number();
        LoincScale loincScale = LoincScale.string2enum(loincEntry.getScale());

        //if the loinc is already annotated, warn user
        if (createAnnotationButton.getText().equals("Create annotation")
                && model.getLoincAnnotationMap().containsKey(loincCode)) {
            boolean toOverwrite = PopUps.getBooleanFromUser("Do you want to overwrite?",
                    loincCode + " is already annotated", "Overwrite warning");
            if (!toOverwrite) return;
        }

        //update annotations right before finalizing the record
        if(!advancedAnnotationModeSelected) { //we are last in basic mode, user might have changed data for basic annotation
            logger.trace("creating the annotation while at the basic mode");
            logger.trace("record changes for basic annotation");
            model.setTempTerms(recordTempTerms()); //update terms for basic annotation
            model.setInversedBasicMode(recordInversed());
        } else { //if we are last in the advanced mode, user might have added a new annotation, we add this annotation
            logger.trace("creating the annotation while at the advanced mode");
            handleAnnotateCodedValue(e);
        }

        //map hpo terms to internal codes
        Map<String, String> tempTerms = model.getTempTerms();
        String hpoLo = tempTerms.get("hpoLo");
        String hpoNormal = tempTerms.get("hpoNormal");
        String hpoHi = tempTerms.get("hpoHi");
        logger.debug(String.format("hpoLo: %s; hpoNormal: %s; hpoHi: %s", hpoLo, hpoNormal, hpoHi));

            //if no annotations for basic AND advanced, do not create it
        if ((hpoLo == null || hpoLo.isEmpty()) &&
                (hpoNormal == null || hpoNormal.isEmpty()) &&
                (hpoHi == null || hpoHi.isEmpty()) &&
                tempAdvancedAnnotations.isEmpty()) {
            PopUps.showInfoMessage("You have not mapped any HPO terms to this LOINC.", "Abort");
            logger.debug("tempAdvancedAnnotations size: " + tempAdvancedAnnotations.size());
            return;
        }

        //We don't have to force every loinc code to have three phenotypes
        HpoTerm low = termmap.get(hpoLo);
        HpoTerm normal = termmap.get(hpoNormal);
        HpoTerm high = termmap.get(hpoHi);
        //logger.debug((String.format("Terms found: lo- %s; normal- %s; hi- %s", low.getName(), normal.getName(), high.getName())));

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

        //start building the annotation
        UniversalLoinc2HPOAnnotation.Builder builder = new UniversalLoinc2HPOAnnotation.Builder();
        builder.setLoincId(loincCode)
                .setLoincScale(loincScale)
                .setNote(annotationNoteField.getText())
                .setFlag(flagForAnnotation.isSelected());
        //add the basic annotations
        if (loincScale == LoincScale.Qn) {
            builder.setLowValueHpoTerm(low)
                    .setIntermediateValueHpoTerm(normal)
                    .setHighValueHpoTerm(high)
                    .setIntermediateNegated(model.isInversedBasicMode());
        } else if (loincScale == LoincScale.Ord && loincTableView.getSelectionModel().getSelectedItem().isPresentOrd()) {
            builder.setNegValueHpoTerm(normal, model.isInversedBasicMode())
                    .setPosValueHpoTerm(high);
        } else { //
            boolean choice = PopUps.getBooleanFromUser("Current Loinc should be annotated in advanced mode. Click Yes if you still want to keep current annotations?", "LOINC type mismatch", "Warning");
            if (choice) {
                builder.setLowValueHpoTerm(low)
                        .setIntermediateValueHpoTerm(normal)
                        .setHighValueHpoTerm(high)
                        .setIntermediateNegated(model.isInversedBasicMode())
                        .setNegValueHpoTerm(normal, model.isInversedBasicMode())
                        .setPosValueHpoTerm(high);
            }
        }


        //add the advanced annotations
        if (!tempAdvancedAnnotations.isEmpty()) {
            tempAdvancedAnnotations.forEach(p ->
                    builder.addAdvancedAnnotation(p.getCode(), p.getHpoTermId4LoincTest()));
        }

        //add some meta data, such as date, created by, and version
        if (createAnnotationButton.getText().equals("Create annotation")) { //create for the first time
            builder.setCreatedBy(model.getBiocuratorID() == null? MISSINGVALUE:model.getBiocuratorID())
                .setCreatedOn(LocalDateTime.now().withNano(0))
                .setVersion(0.1);
        } else { //editing mode
            builder.setLastEditedBy(model.getBiocuratorID() == null? MISSINGVALUE:model.getBiocuratorID())
                    .setLastEditedOn(LocalDateTime.now().withNano(0))
                    .setCreatedBy(model.getLoincAnnotationMap().get(loincCode).getCreatedBy())
                    .setCreatedOn(model.getLoincAnnotationMap().get(loincCode).getCreatedOn())
                    .setVersion(model.getLoincAnnotationMap().get(loincCode).getVersion() + 0.1);

        }

        //complete the building process, build the object
        UniversalLoinc2HPOAnnotation loinc2HPOAnnotation = builder.build();
        model.addLoincTest(loinc2HPOAnnotation);

        //reset many settings
        advancedAnnotationModeSelected = false;
        model.setTempTerms(new HashMap<>());//clear the temp term in model
        model.setInversedBasicMode(false);
        model.setTempAdvancedAnnotation(new HashMap<>());
        model.setInversedAdvancedMode(false);
        tempAdvancedAnnotations.clear();
        switchToBasicAnnotationMode();
        flagForAnnotation.setSelected(false);
        annotationNoteField.clear();
        model.setSessionChanged(true);

        loinc2HpoAnnotationsTabController.refreshTable();
        createAnnotationSuccess.setFill(Color.GREEN);
        if (createAnnotationButton.getText().equals("Save")) {
            createAnnotationButton.setText("Create annotation");
            model.setLoincUnderEditing(null);
        }
        changeColorLoincTableView();

        e.consume();
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

        /**
        if ((HpoLow == null || HpoLow.trim().isEmpty()) &&
                (HpoNorm == null || HpoNorm.trim().isEmpty()) &&
                (HpoHigh == null || HpoHigh.trim().isEmpty())) {
            //popup an alert
            issueDetected = true;
            userConfirmed = PopUps.getBooleanFromUser("Are you sure you want to create an annotation without any HPO terms?",
                    "Annotation without HPO terms", "No HPO Alert");
        }
         **/

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
        logger.debug("enter changeColorLoincTableView");
        logger.info("model size: " + model.getLoincAnnotationMap().size());
        loincIdTableColumn.setCellFactory(x -> new TableCell<LoincEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty){
                super.updateItem(item, empty);
                if(item != null && !empty) {
                    setText(item);
                    try {
                        if(model.getLoincAnnotationMap().containsKey(new LoincId(item))) {
                            logger.info("model contains " + item);
                            logger.info("num of items in model " + model.getLoincAnnotationMap().size());
                            TableRow<LoincEntry> currentRow = getTableRow();
                            currentRow.setStyle("-fx-background-color: cyan");
                            //setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-background-color: lightblue");
                        } else if (model.getUserCreatedLoincLists().get(LOINCWAITING4NEWHPO) != null
                                && model.getUserCreatedLoincLists().get(LOINCWAITING4NEWHPO).contains(new LoincId(item))) {
                            logger.info("model usercreated list for LoincWaiting4NewHPO # items: " +
                                    model.getUserCreatedLoincLists().get(LOINCWAITING4NEWHPO).size());
                            TableRow<LoincEntry> currentRow = getTableRow();
                            currentRow.setStyle("-fx-background-color: deeppink");
                        } else if (model.getUserCreatedLoincLists().get(LOINCUNABLE2ANNOTATE) != null
                                && model.getUserCreatedLoincLists().get(LOINCUNABLE2ANNOTATE).contains(new LoincId(item))) {
                            TableRow<LoincEntry> currentRow = getTableRow();
                            currentRow.setStyle("-fx-background-color: lightcoral");
                            //@TODO: change color of other groups. tip: allow user to pick a color
                        } else{//for reasons I don't understand, this else block is critical to make it work!!!
                            TableRow<LoincEntry> currentRow = getTableRow();
                            currentRow.setStyle("");
                        }
                    } catch (MalformedLoincCodeException e) {
                        //do nothing
                    }
                } else {
                    setText(null);
                    getTableRow().setStyle("");
                }
            }

        });
        logger.debug("exit changeColorLoincTableView");
    }


    @FXML
    private void annotationModeSwitchButton(ActionEvent e) {
        e.consume();
        //createTempAnnotation();
        //Important: Save annotation current annotation data
        if (!advancedAnnotationModeSelected) { //current state: Basic mode
            model.setTempTerms(recordTempTerms());
            model.setInversedBasicMode(recordInversed());
        }
        if (advancedAnnotationModeSelected) { //current state: Advanced mode
            model.setTempAdvancedAnnotation(recordAdvancedAnnotation());
            model.setInversedAdvancedMode(recordInversed());
        }

        advancedAnnotationModeSelected = ! advancedAnnotationModeSelected; //switch mode
        if (advancedAnnotationModeSelected) {
            switchToAdvancedAnnotationMode(); //change display for advanced mode
        } else {
            switchToBasicAnnotationMode(); //change display for basic mode
        }

    }

    private void switchToAdvancedAnnotationMode(){
        //before switching to advanced mode, save any data in the basic mode

        annotationTextFieldLeft.setVisible(true);
        annotationLeftLabel.setVisible(true);
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
        if (!model.getTempAdvancedAnnotation().isEmpty()) { //if we have recorded temp data, display it accordingly
            annotationTextFieldLeft.setText(model.getTempAdvancedAnnotation().get("system"));
            annotationTextFieldMiddle.setText(model.getTempAdvancedAnnotation().get("code"));
            annotationTextFieldRight.setText(model.getTempAdvancedAnnotation().get("hpoTerm"));
            inverseChecker.setSelected(model.isInversedAdvancedMode());
        }
    }

    private void switchToBasicAnnotationMode(){
        if (isPresentOrd.get()) {
            annotationLeftLabel.setVisible(false);
            annotationTextFieldLeft.setVisible(false);
            annotationMiddleLabel.setText("Absence");
            annotationRightLabel.setText("Presence");
        } else {
            annotationLeftLabel.setVisible(true);
            annotationTextFieldLeft.setVisible(true);
            annotationLeftLabel.setText("<Low threshold");
            annotationMiddleLabel.setText("Normal");
            annotationRightLabel.setText(">High threshold");
        }

        annotationTextFieldLeft.clear();
        annotationTextFieldMiddle.clear();
        annotationTextFieldRight.clear();
        annotationTextFieldLeft.setPromptText("hpo for low value");
        annotationTextFieldMiddle.setPromptText("hpo for normal value");
        annotationTextFieldRight.setPromptText("hpo for high value");
        modeButton.setText("advanced>>>");
        inverseChecker.setSelected(true);
        if (!model.getTempTerms().isEmpty()) { //if we have recorded temp data, display it accordingly
            annotationTextFieldLeft.setText(model.getTempTerms().get("hpoLo"));
            annotationTextFieldMiddle.setText(model.getTempTerms().get("hpoNormal"));
            annotationTextFieldRight.setText(model.getTempTerms().get("hpoHi"));
            inverseChecker.setSelected(model.isInversedBasicMode());
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
        HpoTerm hpoterm = model.getTermMap().get(stripEN(candidateHPO));
        if (hpoterm == null) logger.error("hpoterm is null");
        if (code != null && hpoterm != null) {
            annotation = new Annotation(code, new HpoTermId4LoincTest(hpoterm, inverseChecker.isSelected()));
        }
        tempAdvancedAnnotations.add(annotation);
        //add annotated value to the advanced table view
        //initadvancedAnnotationTable();
        accordion.setExpandedPane(advancedAnnotationTitledPane);
        inverseChecker.setSelected(false);
        model.setTempAdvancedAnnotation(new HashMap<>());
        model.setInversedAdvancedMode(false);
    }

    @FXML
    private void handleDeleteCodedAnnotation(ActionEvent event) {
        event.consume();
        logger.debug("user wants to delete an annotation");
        logger.debug("tempAdvancedAnnotations size: " + tempAdvancedAnnotations.size());
        Annotation selectedToDelete = advancedAnnotationTable.getSelectionModel().getSelectedItem();
        if (selectedToDelete != null) {
            tempAdvancedAnnotations.remove(selectedToDelete);
        }
        logger.debug("tempAdvancedAnnotations size: " + tempAdvancedAnnotations.size());
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
        popup.setBiocuratorId(model.getBiocuratorID());
        logger.debug("get biocurator id from model: " + model.getBiocuratorID());
        popup.displayWindow(Main.getPrimarystage());
        String githubissue = popup.retrieveGitHubIssue();
        if (githubissue == null) {
            logger.trace("got back null github issue");
            return;
        }
        //String label = popup.getGitHubLabel();
        List<String> labels = popup.getGitHubLabels();
        //String title = String.format("NTR for Loinc %s:  \"%s\"", loincIdSelected, popup.retrieveSuggestedTerm());
        String title = String.format("Loinc %s:  \"%s\"", loincIdSelected, loincEntrySelected.getLongName());
        postGitHubIssue(githubissue, title, popup.getGitHubUserName(), popup.getGitHubPassWord(), labels);
    }

    @FXML
    private void suggestNewChildTerm(ActionEvent e) {
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

        HPO_Class_Found hpoSelected = (HPO_Class_Found) hpoListView.getSelectionModel().getSelectedItem();
        if (hpoSelected == null) {
            HPO_TreeView hpoSelectedInTree = treeView.getSelectionModel().getSelectedItem().getValue();
            hpoSelected = hpoSelectedInTree.hpo_class_found;
        }
        if (hpoSelected == null) {
            logger.error("Select a hpo term before making a suggestion");
            PopUps.showInfoMessage("Please select a hpo term before creating GitHub issue",
                    "Error: No HPO Term selected");
            return;
        }

        HpoTerm hpoTerm = model.getTermMap().get(hpoSelected.getLabel());

        GitHubPopup popup = new GitHubPopup(loincEntrySelected, hpoTerm, true);
        initializeGitHubLabelsIfNecessary();
        popup.setLabels(model.getGithublabels());
        popup.setupGithubUsernamePassword(githubUsername, githubPassword);
        popup.setBiocuratorId(model.getBiocuratorID());
        logger.debug("get biocurator id from model: " + model.getBiocuratorID());
        popup.displayWindow(Main.getPrimarystage());
        String githubissue = popup.retrieveGitHubIssue();
        if (githubissue == null) {
            logger.trace("got back null github issue");
            return;
        }
        List<String> labels = popup.getGitHubLabels();
        //String title = String.format("NTR for Loinc %s:  \"%s\"", loincIdSelected, popup.retrieveSuggestedTerm());
        String title = String.format("Loinc %s:  \"%s\"", loincIdSelected, loincEntrySelected.getLongName());
        postGitHubIssue(githubissue, title, popup.getGitHubUserName(), popup.getGitHubPassWord(), labels);
    }


    private void postGitHubIssue(String message, String title, String uname, String pword, List<String> chosenLabels) {
        GitHubPoster poster = new GitHubPoster(uname, pword, title, message);
        this.githubUsername = uname;
        this.githubPassword = pword;
        if (chosenLabels != null && !chosenLabels.isEmpty()) {
            logger.trace("Labels being chosen: ");
            chosenLabels.forEach(logger::trace);
            poster.setLabel(chosenLabels);
            logger.trace("Labels sent to poster: \t");
            logger.trace(poster.debugLabelsArray4Json());
        }
        try {
            logger.trace("Message sent to Github: \t" + poster.debugReformatpayloadWithLabel());
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

    @FXML
    private void getContextMenu4TreeView(ContextMenuEvent event) {
        event.consume();
        treeView.setContextMenu(contextMenu);
    }


    protected LoincEntry getLoincIdSelected() {
        return loincTableView.getSelectionModel().getSelectedItem();
    }

    protected void setLoincIdSelected(LoincEntry loincEntry) {
        loincTableView.getSelectionModel().select(loincEntry);
    }
    protected void setLoincIdSelected(LoincId loincId) {
        LoincEntry loincEntry = model.getLoincEntryMap().get(loincId);
        loincTableView.getSelectionModel().select(loincEntry);
    }

    @FXML
    protected void showAllAnnotations(ActionEvent event) {
        event.consume();

        LoincEntry loincEntry2Review = getLoincIdSelected();
        if (loincEntry2Review == null) {
            PopUps.showInfoMessage("There is no annotation to review. Select a loinc entry and try again",
                    "No content to show");
            return;
        }
        if (model.getLoincAnnotationMap().get(loincEntry2Review.getLOINC_Number()) != null) {
            logger.debug("The annotation to review is already added to the annotation map");
            //currentAnnotationController.setCurrentAnnotation(model.getLoincAnnotationMap().get(loincEntry2Review.getLOINC_Number()));
            model.setCurrentAnnotation(model.getLoincAnnotationMap().get(loincEntry2Review.getLOINC_Number()));
        } else {
            logger.debug("currently selected loinc has no annotation. A temporary annotation is being created for " + loincEntry2Review.getLOINC_Number());
            PopUps.showInfoMessage("Currently selected loinc code has not been annotated.",
                    "No content to show");
            return;
            //currentAnnotationController.setCurrentAnnotation(createCurrentAnnotation());
            //model.setCurrentAnnotation(createCurrentAnnotation());
        }


        Stage window = new Stage();
        window.setResizable(true);
        window.centerOnScreen();
        window.setTitle("All annotations for Loinc " + getLoincIdSelected().getLOINC_Number());
        window.initStyle(StageStyle.UTILITY);
        window.initModality(Modality.APPLICATION_MODAL);
        Parent root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/currentAnnotation.fxml"));

//            This sets the same controller factory (Callback) as above using method reference syntax (in single line)
//            fxmlLoader.setControllerFactory(injector::getInstance);

            fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
                 @Override
                 public Object call(Class<?> clazz) {
                     return injector.getInstance(clazz);
                 }
            });

            root = fxmlLoader.load();
            Scene scene = new Scene(root, 800, 600);

            window.setScene(scene);
            window.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from the pop up window
     * @param loincAnnotation passed from the pop up window
     */
    protected void editCurrentAnnotation(UniversalLoinc2HPOAnnotation loincAnnotation) {

        setLoincIdSelected(loincAnnotation.getLoincId());
        model.setLoincUnderEditing(model.getLoincEntryMap().get(loincAnnotation.getLoincId()));

        //populate annotation textfields for basic mode
        Map<String, Code> internalCode = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);
        Code codeLow = internalCode.get("L");
        Code codeHigh = internalCode.get("H");
        Code codeNormal = internalCode.get("N");
        Code codePos = internalCode.get("POS");
        Code codeNeg = internalCode.get("NEG");
        HpoTermId4LoincTest hpoLow = loincAnnotation.loincInterpretationToHPO(codeLow);
        HpoTermId4LoincTest hpoHigh = loincAnnotation.loincInterpretationToHPO(codeHigh);
        if (hpoHigh == null) {
            hpoHigh = loincAnnotation.loincInterpretationToHPO(codePos);
        }
        HpoTermId4LoincTest hpoNormal = loincAnnotation.loincInterpretationToHPO(codeNormal);
        if (hpoNormal == null) {
            hpoNormal = loincAnnotation.loincInterpretationToHPO(codeNeg);
        }

        if (hpoLow != null) {
            String hpoLowTermName = hpoLow.getHpoTerm().getName();
            annotationTextFieldLeft.setText(hpoLowTermName);
        }
        if (hpoHigh != null) {
            String hpoHighTermName = hpoHigh.getHpoTerm().getName();
            annotationTextFieldRight.setText(hpoHighTermName);
        }
        if (hpoNormal != null) {
            String hpoNormalTermName = hpoNormal.getHpoTerm().getName();
            boolean isnegated = hpoNormal.isNegated();
            annotationTextFieldMiddle.setText(hpoNormalTermName);
            inverseChecker.setSelected(isnegated);
        }

        //populated advanced annotations table view
        //remember: advanced annotation == not using internal codes
        for (Map.Entry<Code, HpoTermId4LoincTest> entry : loincAnnotation.getCandidateHpoTerms().entrySet()) {
            if (!entry.getKey().getSystem().equals(Loinc2HPOCodedValue.CODESYSTEM)) {
                tempAdvancedAnnotations.add(new Annotation(entry.getKey(), entry.getValue()));
            }
        }

        boolean flag = loincAnnotation.getFlag();
        flagForAnnotation.setSelected(flag);
        String comment = loincAnnotation.getNote();
        annotationNoteField.setText(comment);

        createAnnotationButton.setText("Save");
        clearButton.setText("Cancel");

    }


    @FXML
    private void handleClear(ActionEvent event) {
        annotationTextFieldLeft.clear();
        annotationTextFieldMiddle.clear();
        annotationTextFieldRight.clear();
        flagForAnnotation.setSelected(false);
        annotationNoteField.clear();
        tempAdvancedAnnotations.clear();
        switchToBasicAnnotationMode();
        if (clearButton.getText().equals("Cancel")) {
            clearButton.setText("Clear");
            model.setLoincUnderEditing(null);
        }
        createAnnotationButton.setText("Create annotation");

    }


}
