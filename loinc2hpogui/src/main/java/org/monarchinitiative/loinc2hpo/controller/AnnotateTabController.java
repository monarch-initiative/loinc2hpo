package org.monarchinitiative.loinc2hpo.controller;


import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.gui.PopUps;
import org.monarchinitiative.loinc2hpo.gui.WidthAwareTextFields;
import org.monarchinitiative.loinc2hpo.loinc.AnnotatedLoincRangeTest;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.model.Model;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


@Singleton
public class AnnotateTabController {
    private static final Logger logger = LogManager.getLogger();

    private Model model=null;
    /** Reference to the third tab. When the user adds a new annotation, we update the table, therefore, we need a reference. */
    @Inject private Loinc2HpoAnnotationsTabController loinc2HpoAnnotationsTabController;
    private ImmutableMap<String,LoincEntry> loincmap=null;

    private ImmutableMap<String,HpoTerm> termmap;

    @FXML private Button initLOINCtableButton;
    @FXML private Button searchForLOINCIdButton;
    @FXML private Button createAnnotationButton;
    @FXML private TextField loincSearchTextField;
    @FXML private TextField loincStringSearchTextField;

//    @FXML private TextField hpoLowAbnormalTextField;
//    @FXML private TextField hpoNotAbnormalTextField;
//    @FXML private TextField hpoHighAbnormalTextField;

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


    @FXML private void initialize() {
        if (model != null) {
            setModel(model);
        }
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
        hpoListView.setOrientation(Orientation.HORIZONTAL);

        loincTableView.setRowFactory( tv -> {
            TableRow<LoincEntry> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    LoincEntry rowData = row.getItem();
                    initHpoTermListView(rowData);
                }
            });
            return row ;
        });
    }



    private void initHpoTermListView(LoincEntry entry) {
        String name = entry.getLongName();
        ObservableList<String> items = FXCollections.observableArrayList (
                "A", "B", "C", "D",name);
        this.hpoListView.setItems(items);
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



    @FXML private void createLoinc2HpoAnnotation(ActionEvent e) {
        e.consume();
        String hpoLo,hpoNormal,hpoHi;
        String loincCode=this.loincSearchTextField.getText();
        hpoLo="?"; //hpoLowAbnormalTextField.getText();
        hpoNormal="?";//hpoNotAbnormalTextField.getText();
        hpoHi="?";//hpoHighAbnormalTextField.getText();

        HpoTerm low = termmap.get(hpoLo);
        if (low==null) {
            logger.error(String.format("Could not retrieve HPO Term for %s",hpoLo));
            return;
        }
        HpoTerm normal = termmap.get(hpoNormal);
        if (normal==null) {
            logger.error(String.format("Could not retrieve HPO Term for %s",hpoNormal));
            return;
        }
        HpoTerm high = termmap.get(hpoHi);
        if (high==null) {
            logger.error(String.format("Could not retrieve HPO Term for %s",hpoHi));
            return;
        }

        AnnotatedLoincRangeTest test =
                new AnnotatedLoincRangeTest(loincCode,low,normal,high);
        this.model.addLoincTest(test);
        loinc2HpoAnnotationsTabController.refreshTable();
    }





}
