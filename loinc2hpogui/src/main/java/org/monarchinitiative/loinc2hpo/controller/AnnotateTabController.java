package org.monarchinitiative.loinc2hpo.controller;


import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.gui.WidthAwareTextFields;
import org.monarchinitiative.loinc2hpo.loinc.AnnotatedLoincRangeTest;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.model.Model;


import java.util.List;



@Singleton
public class AnnotateTabController {
    private static final Logger logger = LogManager.getLogger();

    private Model model=null;
    /** Reference to the third tab. When the user adds a new annotation, we update the table, therefore, we need a reference. */
    @Inject private Loinc2HpoAnnotationsTabController loinc2HpoAnnotationsTabController;
    private ImmutableMap<String,LoincEntry> loincmap=null;

    private ImmutableMap<String,HpoTerm> termmap;

    @FXML Button initLOINCtableButton;
    @FXML Button searchForLOINCIdButton;
    @FXML Button createAnnotationButton;
    @FXML TextField loincSearchTextField;

    @FXML private TextField hpoLowAbnormalTextField;
    @FXML private TextField hpoNotAbnormalTextField;
    @FXML private TextField hpoHighAbnormalTextField;
    @FXML private TextField lowRangeTextField;
    @FXML private TextField highRangeTextField;
    @FXML private TextField ageLowYearsTextField;
    @FXML private TextField ageLowMonthsTextField;
    @FXML private TextField ageLowDaysTextField;
    @FXML private TextField ageHighYearsTextField;
    @FXML private TextField ageHighMonthsTextField;
    @FXML private TextField ageHighDaysTextField;
    @FXML private TextField unitTextField;


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

        if (model != null && model.getPathToHpoOboFile()!=null) {
            model.parseOntology();
            termmap = model.getTermMap();
            logger.trace(String.format("Initialized term map with %d terms",termmap.size()));
            WidthAwareTextFields.bindWidthAwareAutoCompletion(hpoLowAbnormalTextField, termmap.keySet());
            WidthAwareTextFields.bindWidthAwareAutoCompletion(hpoNotAbnormalTextField, termmap.keySet());
            WidthAwareTextFields.bindWidthAwareAutoCompletion(hpoHighAbnormalTextField, termmap.keySet());
            logger.trace(String.format("Initializing term map to %d terms",termmap.size()));
        } else {
            logger.warn("Calling initialize but model or path to hp.obo file was null");
        }

    }



    public void setModel(Model m) {
        model=m;
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
            return;
        } else {
            logger.trace(String.format("Searching table for term %s",entry.getLongName()));
        }
        if (termmap==null) initialize(); // set up the Hpo autocomplete if possible
        loincTableView.getItems().clear();
        loincTableView.getItems().add(entry);
    }



    @FXML private void createLoinc2HpoAnnotation(ActionEvent e) {
        e.consume();
        String hpoLo,hpoNormal,hpoHi;
        Integer ageLoY, ageLoM,ageLoD,ageHiY,ageHiM,ageHiD;
        String rangeLo, rangeHi, rangeUnit;

        String loincCode=this.loincSearchTextField.getText();
        hpoLo=hpoLowAbnormalTextField.getText();
        hpoNormal=hpoNotAbnormalTextField.getText();
        hpoHi=hpoHighAbnormalTextField.getText();

        HpoTerm low = termmap.get(hpoLo);
        if (low==null) {
            logger.error(String.format("Could not retrive HPO Term for %s",hpoLo));
            return;
        }
        HpoTerm normal = termmap.get(hpoNormal);
        if (normal==null) {
            logger.error(String.format("Could not retrive HPO Term for %s",hpoNormal));
            return;
        }
        HpoTerm high = termmap.get(hpoHi);
        if (high==null) {
            logger.error(String.format("Could not retrive HPO Term for %s",hpoHi));
            return;
        }



        try {
            ageLoY = Integer.parseInt(ageLowYearsTextField.getText());
            ageLoM = Integer.parseInt(ageLowMonthsTextField.getText());
            ageLoD = Integer.parseInt(ageLowDaysTextField.getText());
            ageHiY = Integer.parseInt(ageHighYearsTextField.getText());
            ageHiM = Integer.parseInt(ageHighMonthsTextField.getText());
            ageHiD = Integer.parseInt(ageHighDaysTextField.getText());
        } catch (NumberFormatException nfe) {
            logger.error(String.format("Could not parse LOINC entry, number format exception %s",nfe.toString()));
            return;
        }
        rangeLo=lowRangeTextField.getText();
        rangeHi=highRangeTextField.getText();
        rangeUnit=unitTextField.getText();

        AnnotatedLoincRangeTest test =
                new AnnotatedLoincRangeTest(loincCode,low,normal,high,ageLoY,ageLoM,ageLoD,ageHiY,ageHiM,ageHiD,rangeLo,rangeHi,rangeUnit);
        this.model.addLoincTest(test);
        loinc2HpoAnnotationsTabController.refreshTable();
    }





}
