package org.monarchinitiative.loinc2hpo.controller;


import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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
import org.monarchinitiative.loinc2hpo.gui.*;
import org.monarchinitiative.loinc2hpo.io.LoincOfInterest;
import org.monarchinitiative.loinc2hpo.io.OntologyModelBuilderForJena;
import org.monarchinitiative.loinc2hpo.io.WriteToFile;
import org.monarchinitiative.loinc2hpo.loinc.*;
import org.monarchinitiative.loinc2hpo.model.AdvancedAnnotationTableComponent;
import org.monarchinitiative.loinc2hpo.model.AppResources;
import org.monarchinitiative.loinc2hpo.model.AppTempData;
import org.monarchinitiative.loinc2hpo.model.Settings;
import org.monarchinitiative.loinc2hpo.util.*;
import org.monarchinitiative.phenol.ontology.data.Term;


import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Singleton
public class AnnotateTabController {
    private static final Logger logger = LogManager.getLogger();

    private AppTempData appTempData =null;
    private final String MISSINGVALUE = "NA";

    @Inject
    private Injector injector;

    @Inject
    private Settings settings;

    @Inject
    private AppResources appResources;

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

    private Map<String,Term> termmap;
    // TODO currently, this list is taking both HPO_CLass_Found and String at different parts of the app.
    @FXML private ListView<HPO_Class_Found> hpoListView;
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
    @FXML private CheckMenuItem loincTableEnableMultiSelection;
    private LOINC2HpoAnnotationImpl toCopy;
    @FXML private MenuItem pasteAnnotationButton;


    @FXML private Button modeButton;
    @FXML private TitledPane advancedAnnotationTitledPane;
    @FXML private TableView<AdvancedAnnotationTableComponent> advancedAnnotationTable;
    @FXML private TableColumn<AdvancedAnnotationTableComponent, String> advancedAnnotationSystem;
    @FXML private TableColumn<AdvancedAnnotationTableComponent, String> advancedAnnotationCode;
    @FXML private TableColumn<AdvancedAnnotationTableComponent, String> advancedAnnotationHpo;
    private ObservableList<AdvancedAnnotationTableComponent> tempAdvancedAnnotations = FXCollections.observableArrayList();

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
    @FXML private Menu groupUngroup2LoincListButton;
    @FXML private Menu exportLoincListButton;
    @FXML private Menu importLoincGroupButton;
    final ObservableList<String> userCreatedLoincLists = FXCollections
            .observableArrayList();
    final private String LOINCWAITING4NEWHPO = "require_new_HPO_terms";
    final private String LOINCUNABLE2ANNOTATE = "unable_to_annotate";
    final private String UNSPECIFIEDSPECIMEN = "unspecified_specimen";
    final private String LOINC4QC = "test_for_QC";

    private BooleanProperty isPresentOrd = new SimpleBooleanProperty(false);


    //@Inject private CurrentAnnotationController currentAnnotationController;

    @FXML private void initialize() {
        if (appTempData != null) {   //weird line. appTempData is set by main controller; this line never runs
            setAppTempData(appTempData);
            //currentAnnotationController.setAppTempData(appTempData); //let current annotation stage have access to appTempData
        }
        //currentAnnotationController.setAppTempData(appTempData); //let current annotation stage have access to appTempData
        suggestHPOButton.setTooltip(new Tooltip("Suggest new HPO terms"));
        filterButton.setTooltip(new Tooltip("Filter Loinc by providing a Loinc list in txt file"));
        addCodedAnnotationButton.setTooltip(new Tooltip("Add current annotation"));
        flagForAnnotation.setTooltip(new Tooltip("Check if you are not confident"));
        clearButton.setTooltip(new Tooltip("Clear all textfields"));
        allAnnotationsButton.setTooltip(new Tooltip("Display annotations for currently selected Loinc code"));
        initLOINCtableButton.setTooltip(new Tooltip("Initialize Loinc Core Table. Download it first."));
        IntializeHPOmodelbutton.setTooltip(new Tooltip("Load hp.owl as a RDF appTempData for query"));
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

        //if user creates a new Loinc group, add two menuitems for it, and specify the actions when those menuitems are clicked
        userCreatedLoincLists.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        logger.trace(c + " was added");
                        c.getAddedSubList()
                            .stream()
                            .filter(p -> !appTempData.getUserCreatedLoincLists().containsKey(p))
                            .forEach(p -> {

                                appTempData.addUserCreatedLoincList(p, new LinkedHashSet<>());

                                MenuItem newListMenuItem = new MenuItem(p);
                                groupUngroup2LoincListButton.getItems().add(newListMenuItem);
                                newListMenuItem.setOnAction((event -> {
                                    logger.trace("action detected");
                                    if (loincTableView.getSelectionModel().getSelectedItem()!=null) {
                                        LoincId loincId = loincTableView.getSelectionModel()
                                                .getSelectedItem().getLOINC_Number();
                                        if (appTempData.getUserCreatedLoincLists().get(p).contains(loincId)) {
                                            appTempData.getUserCreatedLoincLists().get(p)
                                                    .remove(loincId);
                                            logger.trace(String.format("LOINC: %s removed from %s", loincId, p));
                                        } else {
                                            appTempData.getUserCreatedLoincLists().get(p)
                                                    .add(loincId);
                                            logger.trace(String.format("LOINC: %s added to %s", loincId, p));
                                        }

                                        changeColorLoincTableView();
                                        appTempData.setSessionChanged(true);
                                    }
                                }));

                                MenuItem newExportMenuItem = new MenuItem(p);
                                exportLoincListButton.getItems().add(newExportMenuItem);
                                newExportMenuItem.setOnAction((event -> {
                                    logger.trace("action detected");
                                    if (loincTableView.getSelectionModel().getSelectedItem()!=null) {
                                        Set<LoincId> loincIds = appTempData.getUserCreatedLoincLists().get(p);
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
                                            if (appResources.getLoincEntryMap().containsKey(loincId)) {
                                                appTempData.getUserCreatedLoincLists().get(p).add(loincId);
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
        //track what is selected in the loincTable. If currently selected LOINC is a Ord type with a Presence/Absence outcome, change the listener isPresentOrd to true; otherwise false.
        loincTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                isPresentOrd.setValue(newValue.isPresentOrd());
            }
        });

        //if the currently selected LOINC is a Ord type with a Presence/Absence outcome, reset the basic annotation buttons
        isPresentOrd.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                switchToBasicAnnotationMode();
            }
        });


        loincTableEnableMultiSelection.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (observable != null) {
                    if (newValue) {
                        logger.trace("multi selection is enabled");
                        loincTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                    } else {
                        logger.trace("multi selection is not enabled");
                        loincTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                    }
                } else {
                    return;
                }
            }
        });
    }

    void defaultStartUp() {
        initLOINCtable();
        Platform.runLater(()->initHPOmodelButton(null));
        List<String> DEFAULTGROUPS = Arrays.asList(LOINCWAITING4NEWHPO, LOINCUNABLE2ANNOTATE, UNSPECIFIEDSPECIMEN, LOINC4QC);
        for(int i = 0; i < DEFAULTGROUPS.size(); i++) {
            if (!settings.getUserCreatedLoincListsColor()
                    .containsKey(DEFAULTGROUPS.get(i))) {
                appTempData.addOrUpdateUserCreatedLoincListColor(DEFAULTGROUPS.get(i),
                        appTempData.defaultColorList().get(i + 1));
                logger.info(DEFAULTGROUPS.get(i) + "::::" + appTempData.defaultColorList().get(i + 1));
            }
        }
        logger.info("default colors in appTempData:");
        //appTempData.defaultColorList().forEach(System.out::println);
        logger.trace("default color for LOINC lists is set");
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


    /** Initialize the AppTempData reference and set up the HPO autocomplete if possible. */
    public void setAppTempData(AppTempData m) {
        logger.trace("Setting appTempData in AnnotateTabeController");
        appTempData =m;
        //termmap = appTempData.getTermMap();
        termmap = appResources.getTermnameTermMap();
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
            if(appTempData.getLoincUnderEditing() != null && !appTempData.getLoincUnderEditing().equals(rowData)){
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
            alert.setTitle("HPO AppTempData Undefined");
            alert.setHeaderText("Create HPO appTempData first before querying");
            alert.setContentText("Click \"Initialize HPO appTempData\" to create an" +
                    " HPO appTempData for Sparql query. Click and query again.");
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

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No HPO Found");
            alert.setContentText("Try search with synonyms");
            alert.show();

            Task task = new Task() {
                @Override
                protected Object call() throws Exception {

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        //do nothing
                    } finally {
                        Platform.runLater(() -> {
                            alert.close();
                        });
                    }

                    return null;
                }
            };
            Thread alertThread = new Thread(task);
            alertThread.start();
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
        if (appTempData.getLoincUnderEditing() == null || //not under Editing mode
                //or query the loinc code under editing
                (appTempData.getLoincUnderEditing() != null && appTempData.getLoincUnderEditing().equals(entry))) {
            updateHpoTermListView(entry);
        } else {
            PopUps.showInfoMessage("You are currently editing " + appTempData.getLoincUnderEditing().getLOINC_Number() +
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
            alert.setTitle("HPO AppTempData Undefined");
            alert.setHeaderText("Create HPO appTempData first before querying");
            alert.setContentText("Click \"Initialize HPO appTempData\" to create an" +
                    " HPO appTempData for Sparql query. Click and query again.");
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


        if (appTempData.getLoincUnderEditing() != null && !appTempData.getLoincUnderEditing().equals(entry)) {

            PopUps.showInfoMessage("You are currently editing " + appTempData.getLoincUnderEditing().getLOINC_Number() +
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
//            for (HPO_Class_Found candidate: queryResults) {
//                items.add(candidate);
//            }
            items.addAll(queryResults);
            this.hpoListView.setItems(items);
            userInputForManualQuery.clear();
            //items.add("0 result is found. Try manual search with synonyms.");
        } else {
            ObservableList<String> items = FXCollections.observableArrayList();
            items.add("0 HPO class is found. Try manual search with " +
                    "alternative keys (synonyms)");
            //this.hpoListView.setItems(items);

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No HPO Found");
            alert.setContentText("Try search with synonyms");
            alert.show();

            Task task = new Task() {
                @Override
                protected Object call() throws Exception {

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        //do nothing
                    } finally {
                        Platform.runLater(() -> {
                            alert.close();
                        });
                    }

                    return null;
                }
            };
            Thread alertThread = new Thread(task);
            alertThread.start();
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
        initLOINCtable();
    }

    private void initLOINCtable() {
        logger.trace("init LOINC table");
        String loincCoreTableFile = settings.getLoincCoreTablePath();
        if (loincCoreTableFile==null) {
            PopUps.showWarningDialog("Error", "File not found", "Could not find LOINC Core Table file. Set the path first");
            return;
        }
        this.loincmap = LoincEntry.getLoincEntryList(loincCoreTableFile);
        int limit=Math.min(loincmap.size(),1000); // we will show just the first 1000 entries in the table.
        List<LoincEntry> lst = loincmap.values().asList().subList(0,limit);
        loincTableView.getItems().clear(); // remove any previous entries
        loincTableView.getItems().addAll(lst);
        loincTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        initTableStructure();

        if (this.loincmap.isEmpty()) {
            Platform.runLater(() -> {
                PopUps.showWarningDialog("No LOINC data was imported",
                        "Warning",
                        "We could not import any LOINC data - \n did you import the correct LOINC file?");
            });
        }

    }

    @FXML private void initHPOmodelButton(ActionEvent e){

        String pathToHPO = settings.getHpoOwlPath();
        logger.info("pathToHPO: " + pathToHPO);
        //org.apache.jena.rdf.appTempData.AppTempData hpoModel = SparqlQuery.getOntologyModel(pathToHPO);
        //SparqlQuery.setHPOmodel(hpoModel);
        // The following codes run nicely from IDE, but fails in Jar.
        //create a task to create HPO appTempData
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
            alert.setTitle("Failed to create HPO appTempData");
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
                        //logger.debug(loincEntry.getLOINC_Number() + " : " + loincEntry.getLongName());
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
        e.consume();
        List<LoincEntry> entrylist=new ArrayList<>();
        String enlistName;
        FileChooser chooser = new FileChooser();
        if (settings.getAnnotationFolder() != null) {
            chooser.setInitialDirectory(new File(settings.getAnnotationFolder()));
        }
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
                    LoincId loincId;
                    LoincEntry loincEntry;
                    try {
                        loincId = new LoincId(loincString);
                        loincEntry = appResources.getLoincEntryMap().get(loincId);
                    } catch (MalformedLoincCodeException e2) {
                        //try to see whether user provided Loinc long common name
                        loincEntry = appResources.getLoincEntryMapFromName().
                                get(loincString);
                        if (loincEntry == null) {
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
                logger.error("Found 0 Loinc codes");
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
            appTempData.addFilteredList(enlistName, new ArrayList<>(entrylist)); //keep a record in appTempData
            //entrylist.forEach(p -> logger.trace(p.getLOINC_Number()));
            accordion.setExpandedPane(loincTableTitledpane);
        } else {
            logger.info("Unable to obtain path to LOINC of interest file");
        }
    }

    @FXML
    private void lastLoincList(ActionEvent e) {

        e.consume();
        List<LoincEntry> lastLoincList = appTempData.previousLoincList();
        if (lastLoincList != null && !lastLoincList.isEmpty()) {
            loincTableView.getItems().clear();
            loincTableView.getItems().addAll(lastLoincList);
        }
    }

    @FXML
    private void nextLoincList(ActionEvent e) {
        e.consume();

        List<LoincEntry> nextLoincList = appTempData.nextLoincList();
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
        //appTempData.addUserCreatedLoincList(nameOfList, new LinkedHashSet<>());
        userCreatedLoincLists.add(nameOfList);
        Random rand = new Random();
        double[] randColorValues = rand.doubles(3, 0, 1).toArray();
        Color randColor = Color.color(randColorValues[0], randColorValues[1], randColorValues[2]);
        appTempData.addOrUpdateUserCreatedLoincListColor(nameOfList, ColorUtils.colorValue(randColor));
    }

    @FXML
    private void setLoincGroupColor(ActionEvent e) {
        logger.trace("user wants to set the color of LOINC groups");
        Stage window = new Stage();

        VBox root = new VBox();
        root.setSpacing(10);


        ToolBar toolBar = new ToolBar();
        final ComboBox<String> loincGroupCombo = new ComboBox();

        loincGroupCombo.getItems().addAll(userCreatedLoincLists);

        final ColorPicker colorPicker = new ColorPicker();
        loincGroupCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (observable != null) {
                    final String colorString = settings.getUserCreatedLoincListsColor().get(newValue);
                    if (colorString != null) {
                        final Color color = Color.web(colorString);
                        colorPicker.setValue(color);
                    }

                }
            }
        });
        loincGroupCombo.getSelectionModel().select(0);

        toolBar.getItems().addAll(loincGroupCombo, colorPicker);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(2));

        Map<String, TextField> gridPaneColors = new HashMap<>();
        for (int i = 0; i < userCreatedLoincLists.size(); i++) {
            TextField name = new TextField(userCreatedLoincLists.get(i));
            gridPane.add(name, 0, i);
            TextField color = new TextField();
            color.setBackground(new Background(new BackgroundFill(Color.web(settings.getUserCreatedLoincListsColor().get(name.getText())), null, null)));
            gridPane.add(color, 1, i);
            gridPaneColors.put(name.getText(), color);
            logger.trace("color" + color.getBackground().getFills().toString());
        }


        colorPicker.setOnAction(t -> {
            appTempData.addOrUpdateUserCreatedLoincListColor(loincGroupCombo.getSelectionModel().getSelectedItem(), colorPicker.getValue().toString());
            logger.trace("new color: " + settings.getUserCreatedLoincListsColor().get(loincGroupCombo.getSelectionModel().getSelectedItem()));
            gridPaneColors.get(loincGroupCombo.getSelectionModel().getSelectedItem()).setBackground(new Background(new BackgroundFill(colorPicker.getValue(), null, null)));
            changeColorLoincTableView();
            Settings.writeSettings(settings, Loinc2HpoPlatform.getPathToSettingsFile());
        });

        root.getChildren().addAll(toolBar, gridPane);
        Scene scene = new Scene(root, 400, 400);
        window.setScene(scene);
        window.showAndWait();
    }

    /**
     * This function is normally not useful because of MainController::openSession()
     */
    private void initializeUserCreatedLoincListsIfNecessary(){
        //execute the functionalities only once in each secession
        if (!appTempData.getUserCreatedLoincLists().isEmpty()) {
            logger.trace("initializeUserCreatedLoincListsIfNecessary(): 1111");
            return;
        }
        //by default, there will be two user created lists
        //This is not scaling well. @TODO: consider other ways
        //consider detecting existing lists by scanning the folder
        List<String> initialListNames = new ArrayList<>();
        initialListNames.add(LOINCWAITING4NEWHPO);
        initialListNames.add(LOINCUNABLE2ANNOTATE);
        initialListNames.add(UNSPECIFIEDSPECIMEN);
        initialListNames.add(LOINC4QC);
        userCreatedLoincLists.addAll(initialListNames);
        logger.trace("initializeUserCreatedLoincListsIfNecessary(): 2222");
        /*
        //create a menuitem for each and add to two menus; also create a list to record data
        groupUngroup2LoincListButton.getItems().clear();
        exportLoincListButton.getItems().clear();
        initialListNames.forEach(p -> {
            groupUngroup2LoincListButton.getItems().add(new MenuItem(p));
            exportLoincListButton.getItems().add(new MenuItem(p));
            appTempData.addUserCreatedLoincList(p, new ArrayList<>());
        });
         */
    }


    private void initializeMenuItemsForFilteredLists() {
        if (!appTempData.getFilteredLoincListsMap().isEmpty()) {
            loincListsButton.setDisable(false);
            loincListsButton.getItems().clear();
            List<MenuItem> menuItems = new ArrayList<>();
            appTempData.getFilteredLoincListsMap().keySet().forEach(p -> {
                MenuItem menuItem = new MenuItem(p);
                menuItems.add(menuItem);
            });
            loincListsButton.getItems().addAll(menuItems);
            logger.trace("menu items added");
            //loincListsButton.getItems().forEach(p -> logger.trace("current: " + p.getText()));
            loincListsButton.getItems().forEach(p -> p.setOnAction((event) -> {
                logger.trace(p.getText());
                List<LoincEntry> loincList = appTempData.getLoincList(p.getText());
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
        //initializeUserCreatedLoincListsIfNecessary(); //usually not run
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

        HPO_Class_Found getHpo_class_found() {
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

        //System.out.println("Drag event detected");
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

    //ensure that at least one LOINC entry is selected
    private boolean checkSelectedLoinc() {
        switch (loincTableView.getSelectionModel().getSelectionMode()) {
            case SINGLE:
                if (loincTableView.getSelectionModel().getSelectedItem() != null &&
                        loincTableView.getSelectionModel().getSelectedItem().getScale().equals(toCopy.getLoincScale().toString())) {
                    return true;
                }
            case MULTIPLE:
                if (loincTableView.getSelectionModel().getSelectedItems() != null &&
                        !loincTableView.getSelectionModel().getSelectedItems().isEmpty()) {
                    List<String> scaleTypes = loincTableView.getSelectionModel().getSelectedItems()
                            .stream().map(LoincEntry::getScale)
                            .distinct().collect(Collectors.toList());
                    if (scaleTypes.size() == 1 && scaleTypes.get(0).equals(toCopy.getLoincScale().toString())) {
                        return true;
                    }
                }
            default:
                return false;
        }
    }

    @FXML private void copyAnnotation(ActionEvent event) {
        logger.trace("copy loincAnnotation to other LOINCs");
        LoincEntry selectedLoinc = loincTableView.getSelectionModel().getSelectedItem();
        if (selectedLoinc == null) {
            logger.error("Select a LOINC entry to copy");
            return;
        }
        if (!appResources.getLoincAnnotationMap().containsKey(selectedLoinc.getLOINC_Number())) {
            PopUps.showWarningDialog("Error Selection", "LOINC does not have annotation", "Select a LOINC code that has already been annotated");
            logger.error("Annotation does not exist for " + selectedLoinc.getLOINC_Number());
            return;
        }
        toCopy = appResources.getLoincAnnotationMap().get(selectedLoinc.getLOINC_Number());
        loincTableEnableMultiSelection.setSelected(true);
        //loincTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML private void pasteAnnotation(ActionEvent e) {
        logger.trace("paste annotation");
        if (toCopy == null) {
            PopUps.showWarningDialog("Error Selection", "Unspecified item to copy", "Select a LOINC code to copy annotation from in the LOINC table");
            return;
        }
        if (!checkSelectedLoinc()) { //make sure there is at least one valid LOINC entry selection
            PopUps.showWarningDialog("Error Selection", "Possible Errors:", "1) No LOINC entry is selected;\n2) >= 1 selected LOINC entry does match the scale of origin LOINC");
            return;
        }

        if (!loincTableEnableMultiSelection.isSelected()) {
            pasteAnnotationTo(loincTableView.getSelectionModel().getSelectedItem().getLOINC_Number());
        } else {
            loincTableView.getSelectionModel().getSelectedItems().stream()
                    .forEach(loinc -> {
                        logger.trace("copy to: " + loinc.getLongName());
                        pasteAnnotationTo(loinc.getLOINC_Number());
                    });
        }

        //refresh annotation tab
        loinc2HpoAnnotationsTabController.refreshTable();
        changeColorLoincTableView();
        appTempData.setSessionChanged(true);

        //reset
        toCopy = null;
        //loincTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        loincTableEnableMultiSelection.setSelected(false);

    }

    private void pasteAnnotationTo(LoincId loincId) {
        if (appResources.getLoincAnnotationMap().containsKey(loincId)) {
            logger.trace("Overwrite: " + loincId);
        }
        String comments = toCopy.getNote()==null ? "" : "@original comment: " + toCopy.getNote();
        String copyInfo = String.format("copied from: %s %s", toCopy.getLoincId().toString(), comments);

        LOINC2HpoAnnotationImpl.Builder builder = new LOINC2HpoAnnotationImpl.Builder()
                .setLoincId(loincId)
                .setLoincScale(toCopy.getLoincScale())
                .setCreatedBy(toCopy.getCreatedBy())
                .setCreatedOn(toCopy.getCreatedOn())
                .setLastEditedBy(toCopy.getLastEditedBy())
                .setLastEditedOn(toCopy.getLastEditedOn())
                .setNote(copyInfo)
                .setFlag(toCopy.getFlag())
                .setVersion(toCopy.getVersion());
        toCopy.getCandidateHpoTerms().entrySet().stream()
                .forEach(entry -> builder.addAdvancedAnnotation(entry.getKey(), entry.getValue()));

        appResources.getLoincAnnotationMap().put(loincId, builder.build());

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
                && appResources.getLoincAnnotationMap().containsKey(loincCode)) {
            boolean toOverwrite = PopUps.getBooleanFromUser("Do you want to overwrite?",
                    loincCode + " is already annotated", "Overwrite warning");
            if (!toOverwrite) return;
        }

        //update annotations right before finalizing the record
        if(!advancedAnnotationModeSelected) { //we are last in basic mode, user might have changed data for basic annotation
            logger.trace("creating the annotation while at the basic mode");
            logger.trace("record changes for basic annotation");
            appTempData.setTempTerms(recordTempTerms()); //update terms for basic annotation
            appTempData.setInversedBasicMode(recordInversed());
        } else { //if we are last in the advanced mode, user might have added a new annotation, we add this annotation
            logger.trace("creating the annotation while at the advanced mode");
            handleAnnotateCodedValue(e);
        }

        //map hpo terms to internal codes
        Map<String, String> tempTerms = appTempData.getTempTerms();
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
        Term low = termmap.get(hpoLo);
        Term normal = termmap.get(hpoNormal);
        Term high = termmap.get(hpoHi);
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
        LOINC2HpoAnnotationImpl.Builder builder = new LOINC2HpoAnnotationImpl.Builder();
        builder.setLoincId(loincCode)
                .setLoincScale(loincScale)
                .setNote(annotationNoteField.getText())
                .setFlag(flagForAnnotation.isSelected());
        //add the basic annotations
        if (loincScale == LoincScale.Qn) {
            builder.setLowValueHpoTerm(low)
                    .setIntermediateValueHpoTerm(normal)
                    .setHighValueHpoTerm(high)
                    .setIntermediateNegated(appTempData.isInversedBasicMode());
        } else if (loincScale == LoincScale.Ord && loincTableView.getSelectionModel().getSelectedItem().isPresentOrd()) {
            builder.setNegValueHpoTerm(normal, appTempData.isInversedBasicMode())
                    .setPosValueHpoTerm(high);
        } else { //
            boolean choice = PopUps.getBooleanFromUser("Current Loinc should be annotated in advanced mode. Click Yes if you still want to keep current annotations?", "LOINC type mismatch", "Warning");
            if (choice) {
                builder.setLowValueHpoTerm(low)
                        .setIntermediateValueHpoTerm(normal)
                        .setHighValueHpoTerm(high)
                        .setIntermediateNegated(appTempData.isInversedBasicMode())
                        .setNegValueHpoTerm(normal, appTempData.isInversedBasicMode())
                        .setPosValueHpoTerm(high);
            }
        }


        //add the advanced annotations
        if (!tempAdvancedAnnotations.isEmpty()) {
            tempAdvancedAnnotations.forEach(p ->
                    builder.addAdvancedAnnotation(p.getCode(), p.getHpoTerm4TestOutcome()));
        }

        //add some meta data, such as date, created by, and version
        if (createAnnotationButton.getText().equals("Create annotation")) { //create for the first time
            builder.setCreatedBy(settings.getBiocuratorID() == null? MISSINGVALUE:settings.getBiocuratorID())
                .setCreatedOn(LocalDateTime.now().withNano(0))
                .setVersion(0.1);
        } else { //editing mode
            builder.setLastEditedBy(settings.getBiocuratorID() == null? MISSINGVALUE:settings.getBiocuratorID())
                    .setLastEditedOn(LocalDateTime.now().withNano(0))
                    .setCreatedBy(appResources.getLoincAnnotationMap().get(loincCode).getCreatedBy())
                    .setCreatedOn(appResources.getLoincAnnotationMap().get(loincCode).getCreatedOn())
                    .setVersion(appResources.getLoincAnnotationMap().get(loincCode).getVersion() + 0.1);

        }

        //complete the building process, build the object
        LOINC2HpoAnnotationImpl loinc2HPOAnnotation = builder.build();
        appResources.getLoincAnnotationMap().put(loincCode, loinc2HPOAnnotation);

        //reset many settings
        advancedAnnotationModeSelected = false;
        appTempData.setTempTerms(new HashMap<>());//clear the temp term in appTempData
        appTempData.setInversedBasicMode(false);
        appTempData.setTempAdvancedAnnotation(new HashMap<>());
        appTempData.setInversedAdvancedMode(false);
        tempAdvancedAnnotations.clear();
        switchToBasicAnnotationMode();
        flagForAnnotation.setSelected(false);
        annotationNoteField.clear();
        appTempData.setSessionChanged(true);
        loincTableEnableMultiSelection.setSelected(false);

        loinc2HpoAnnotationsTabController.refreshTable();
        createAnnotationSuccess.setFill(Color.GREEN);
        if (createAnnotationButton.getText().equals("Save")) {
            createAnnotationButton.setText("Create annotation");
            appTempData.setLoincUnderEditing(null);
        }
        changeColorLoincTableView();

        e.consume();
    }


    /**
     * Do a qc of annotation, and ask user questions if there are potential issues
     * @param HpoLow  HPO term for below normal limits
     * @param HpoNorm HPO term for within normal limits
     * @param HpoHigh HPO term for above normal limits
     * @return
     */
    private Map<String, Boolean> qcAnnotation(String HpoLow, String HpoNorm, String HpoHigh){

        boolean issueDetected = false;
        boolean userConfirmed = false;

        /*
        if ((HpoLow == null || HpoLow.trim().isEmpty()) &&
                (HpoNorm == null || HpoNorm.trim().isEmpty()) &&
                (HpoHigh == null || HpoHigh.trim().isEmpty())) {
            //popup an alert
            issueDetected = true;
            userConfirmed = PopUps.getBooleanFromUser("Are you sure you want to create an annotation without any HPO terms?",
                    "AdvancedAnnotationTableComponent without HPO terms", "No HPO Alert");
        }
        */

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
     * @param x first string
     * @param y second string
     * @return true iff both strings are identical (except for possibly white space before/after string and case insensitive)
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
        //System.out.println("Drag event detected");
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
    void changeColorLoincTableView(){
        logger.debug("enter changeColorLoincTableView");
        logger.info("appTempData size: " + appResources.getLoincAnnotationMap().size());
        loincIdTableColumn.setCellFactory(x -> new TableCell<LoincEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty){
                super.updateItem(item, empty);
                //if(item != null && !empty) {
                if(!empty) {
                    setText(item);
                    try {
                        if(appResources.getLoincAnnotationMap().containsKey(new LoincId(item))) {
                            TableRow<LoincEntry> currentRow = getTableRow();
                            currentRow.setStyle("-fx-background-color: cyan");
                        } else {
                            TableRow<LoincEntry> currentRow = getTableRow();
                            LoincId loincId = new LoincId(item);
                            List<String> inList = appTempData.getUserCreatedLoincLists().entrySet()
                                    .stream()
                                    .filter(entry -> entry.getValue().contains(loincId))
                                    .map(entry -> entry.getKey())
                                    .collect(Collectors.toList());
                            if (!inList.isEmpty()) {
                                List<Color> colors = inList.stream()
                                    .map(l -> settings.getUserCreatedLoincListsColor().get(l))
                                    .filter(Objects::nonNull)
                                    .map(Color::web)
                                    .collect(Collectors.toList());
                                if (colors.isEmpty()) {
                                    currentRow.setStyle("");
                                } else {
                                    String backgroundColorValue = colors.get(0).toString(); //just use the first color
                                    logger.trace(backgroundColorValue);
                                    logger.trace(String.format("#%s", backgroundColorValue.substring(2,8).toUpperCase()));
                                    currentRow.setStyle("-fx-background-color: " + String.format("#%s", backgroundColorValue.substring(2,8).toUpperCase()));
                                    //Cannot use set background. It DOES NOT work!
                                    //currentRow.setBackground(new Background(new BackgroundFill(colors.get(0), null, null)));
                                }

                            } else {
                                currentRow.setStyle("");
                            }
                        }
                    } catch (MalformedLoincCodeException e) {
                        //do nothing
                        logger.error("should never happen:xdeide");
                    }
                } else {
                    setText(null);
                    getTableRow().setStyle("");
                    //logger.trace("changecolor:44444");
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
            appTempData.setTempTerms(recordTempTerms());
            appTempData.setInversedBasicMode(recordInversed());
        }
        if (advancedAnnotationModeSelected) { //current state: Advanced mode
            appTempData.setTempAdvancedAnnotation(recordAdvancedAnnotation());
            appTempData.setInversedAdvancedMode(recordInversed());
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
        if (!appTempData.getTempAdvancedAnnotation().isEmpty()) { //if we have recorded temp data, display it accordingly
            annotationTextFieldLeft.setText(appTempData.getTempAdvancedAnnotation().get("system"));
            annotationTextFieldMiddle.setText(appTempData.getTempAdvancedAnnotation().get("code"));
            annotationTextFieldRight.setText(appTempData.getTempAdvancedAnnotation().get("hpoTerm"));
            inverseChecker.setSelected(appTempData.isInversedAdvancedMode());
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
        if (!appTempData.getTempTerms().isEmpty()) { //if we have recorded temp data, display it accordingly
            annotationTextFieldLeft.setText(appTempData.getTempTerms().get("hpoLo"));
            annotationTextFieldMiddle.setText(appTempData.getTempTerms().get("hpoNormal"));
            annotationTextFieldRight.setText(appTempData.getTempTerms().get("hpoHi"));
            inverseChecker.setSelected(appTempData.isInversedBasicMode());
        }
    }

    @FXML
    private void handleAnnotateCodedValue(ActionEvent e){
        e.consume();

        if (!advancedAnnotationModeSelected) return; //do nothing if it is the basic mode

        AdvancedAnnotationTableComponent annotation = null;
        String system = annotationTextFieldLeft.getText().trim().toLowerCase();
        String codeId = annotationTextFieldMiddle.getText().trim(); //case sensitive
        Code code = null;
        if ( !system.isEmpty() && !codeId.isEmpty()) {
            code = Code.getNewCode().setSystem(system).setCode(codeId);
        }
        String candidateHPO = annotationTextFieldRight.getText();
        Term hpoterm = appResources.getTermnameTermMap().get(stripEN(candidateHPO));
        if (hpoterm == null) logger.error("hpoterm is null");
        if (code != null && hpoterm != null) {
            annotation = new AdvancedAnnotationTableComponent(code, new HpoTerm4TestOutcome(hpoterm, inverseChecker.isSelected()));
        }
        tempAdvancedAnnotations.add(annotation);
        //add annotated value to the advanced table view
        //initadvancedAnnotationTable();
        accordion.setExpandedPane(advancedAnnotationTitledPane);
        inverseChecker.setSelected(false);
        appTempData.setTempAdvancedAnnotation(new HashMap<>());
        appTempData.setInversedAdvancedMode(false);
    }

    @FXML
    private void handleDeleteCodedAnnotation(ActionEvent event) {
        event.consume();
        logger.debug("user wants to delete an annotation");
        logger.debug("tempAdvancedAnnotations size: " + tempAdvancedAnnotations.size());
        AdvancedAnnotationTableComponent selectedToDelete = advancedAnnotationTable.getSelectionModel().getSelectedItem();
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
    private LoincId loincIdSelected=null;
    /**
     * For the GitHub new issues, we want to allow the user to choose a pre-existing label for the issue.
     * For this, we first go to GitHub and retrieve the labels with
     * {@link org.monarchinitiative.loinc2hpo.github.GitHubLabelRetriever}. We only do this
     * once per session though.
     */
    private void initializeGitHubLabelsIfNecessary() {
        if (appTempData.hasLabels()) {
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
        appTempData.setGithublabels(labels);
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
        popup.setLabels(appTempData.getGithublabels());
        popup.setupGithubUsernamePassword(githubUsername, githubPassword);
        popup.setBiocuratorId(settings.getBiocuratorID());
        logger.debug("get biocurator id from appTempData: " + settings.getBiocuratorID());
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

        Term hpoTerm = appResources.getTermnameTermMap().get(hpoSelected.getLabel());

        GitHubPopup popup = new GitHubPopup(loincEntrySelected, hpoTerm, true);
        initializeGitHubLabelsIfNecessary();
        popup.setLabels(appTempData.getGithublabels());
        popup.setupGithubUsernamePassword(githubUsername, githubPassword);
        popup.setBiocuratorId(settings.getBiocuratorID());
        logger.debug("get biocurator id from appTempData: " + settings.getBiocuratorID());
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


    private LoincEntry getLoincIdSelected() {
        return loincTableView.getSelectionModel().getSelectedItem();
    }

    protected void setLoincIdSelected(LoincEntry loincEntry) {
        /**
         *
         * loincTableView.getSelectionModel().select(loincEntry);
         logger.debug("selected: " + loincTableView.getSelectionModel().getSelectedItem().getLOINC_Number().toString());
         * loincTableView.requestFocus();
        int focusindex = 0;
        for (int i = 0; i < loincTableView.getItems().size(); i++) {
            if (loincTableView.getSelectionModel().isSelected()) {
                focusindex = i;
            }
        }
         logger.debug("focusindex: " + focusindex);
         **/
        //@TODO: this is a lazy implementation. We should try to put selected item in view
        loincTableView.getItems().clear();
        loincTableView.getItems().addAll(loincEntry);
    }
    void setLoincIdSelected(LoincId loincId) {
        LoincEntry loincEntry = appResources.getLoincEntryMap().get(loincId);
        setLoincIdSelected(loincEntry);
    }

    @FXML
    protected void showAllAnnotations(ActionEvent event) {
        event.consume();
        LoincEntry loincEntry2Review;
        LOINC2HpoAnnotationImpl annotation2Review;

        loincEntry2Review = loincTableView.getSelectionModel().getSelectedItem();
        if (loincEntry2Review == null) {
            PopUps.showInfoMessage("There is no annotation to review. Select a loinc entry and try again",
                    "No content to show");
            return;
        } else {
            annotation2Review = appResources.getLoincAnnotationMap().
                    get(loincEntry2Review.getLOINC_Number());
        }


        if (annotation2Review == null) {
            logger.debug("currently selected loinc has no annotation. A temporary annotation is being created for " + loincEntry2Review.getLOINC_Number());
            PopUps.showInfoMessage("Currently selected loinc code has not been annotated.",
                    "No content to show");
            return;

        } else {
            logger.debug("The annotation to review is already added to the annotation map");

            appTempData.setCurrentAnnotation(appResources.getLoincAnnotationMap().get(loincEntry2Review.getLOINC_Number()));
            //currentAnnotationController.setCurrentAnnotation(createCurrentAnnotation());
            //appTempData.setCurrentAnnotation(createCurrentAnnotation());
        }


        Stage window = new Stage();
        window.setResizable(true);
        window.centerOnScreen();
        window.setTitle("All annotations for Loinc " + getLoincIdSelected().getLOINC_Number());
        window.initStyle(StageStyle.UTILITY);
        window.initModality(Modality.APPLICATION_MODAL);
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/currentAnnotation.fxml"));
            fxmlLoader.setControllerFactory(// Callback
                 (clazz) -> { return injector.getInstance(clazz); }
            );

            CurrentAnnotationController currentAnnotationController = injector.getInstance(CurrentAnnotationController.class);
            currentAnnotationController.setData(loincEntry2Review, annotation2Review);
            //tell the new window how to handle "edit" button
            Consumer<LOINC2HpoAnnotationImpl> edithook = (t) -> {
                editCurrentAnnotation(t);
                mainController.switchTab(MainController.TabPaneTabs.AnnotateTabe);
                window.close();
            };
            currentAnnotationController.setEditHook(edithook);

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
    void editCurrentAnnotation(LOINC2HpoAnnotationImpl loincAnnotation) {

        setLoincIdSelected(loincAnnotation.getLoincId());
        appTempData.setLoincUnderEditing(appResources.getLoincEntryMap().get(loincAnnotation.getLoincId()));

        //populate annotation textfields for basic mode
        Map<String, Code> internalCode = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);
        Code codeLow = internalCode.get("L");
        Code codeHigh = internalCode.get("H");
        Code codeNormal = internalCode.get("N");
        Code codePos = internalCode.get("POS");
        Code codeNeg = internalCode.get("NEG");
        HpoTerm4TestOutcome hpoLow = loincAnnotation.loincInterpretationToHPO(codeLow);
        HpoTerm4TestOutcome hpoHigh = loincAnnotation.loincInterpretationToHPO(codeHigh);
        if (hpoHigh == null) {
            hpoHigh = loincAnnotation.loincInterpretationToHPO(codePos);
        }
        HpoTerm4TestOutcome hpoNormal = loincAnnotation.loincInterpretationToHPO(codeNormal);
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
        for (Map.Entry<Code, HpoTerm4TestOutcome> entry : loincAnnotation.getCandidateHpoTerms().entrySet()) {
            if (!entry.getKey().getSystem().equals(Loinc2HPOCodedValue.CODESYSTEM)) {
                tempAdvancedAnnotations.add(new AdvancedAnnotationTableComponent(entry.getKey(), entry.getValue()));
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
            appTempData.setLoincUnderEditing(null);
        }
        createAnnotationButton.setText("Create annotation");

    }


}
