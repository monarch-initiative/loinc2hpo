package org.monarchinitiative.loinc2hpo.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
//import javassist.CodeConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;
import org.monarchinitiative.loinc2hpo.loinc.HpoTermId4LoincTest;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.loinc.UniversalLoinc2HPOAnnotation;
import org.monarchinitiative.loinc2hpo.model.AdvantagedAnnotationTableComponent;
import org.monarchinitiative.loinc2hpo.model.Model;

import java.util.Map;

@Singleton
public class CurrentAnnotationController{
    private static final Logger logger = LogManager.getLogger();

    private Model model;

    private LoincEntry currentLoincEntry = null;
    private UniversalLoinc2HPOAnnotation currentAnnotation = null;

    @Inject AnnotateTabController annotateTabController;
    @Inject MainController mainController;

    @FXML private BorderPane currentAnnotationPane;

    @FXML private Label annotationTitle;
    @FXML private TextField internalCodingSystem;

    private ObservableList<AdvantagedAnnotationTableComponent> internalCodeAnnotations = FXCollections.observableArrayList();
    @FXML private TableView<AdvantagedAnnotationTableComponent> internalTableview;
    @FXML private TableColumn<AdvantagedAnnotationTableComponent, String> codeInternalTableview;
    @FXML private TableColumn<AdvantagedAnnotationTableComponent, String> hpoInternalTableview;
    @FXML private TableColumn<AdvantagedAnnotationTableComponent, Boolean> inversedInternalTableview;

    private ObservableList<AdvantagedAnnotationTableComponent> externalCodeAnnotations = FXCollections.observableArrayList();
    @FXML private TableView<AdvantagedAnnotationTableComponent> externalTableview;
    @FXML private TableColumn<AdvantagedAnnotationTableComponent, String> systemExternalTableview;
    @FXML private TableColumn<AdvantagedAnnotationTableComponent, String> codeExternalTableview;
    @FXML private TableColumn<AdvantagedAnnotationTableComponent, String> hpoExternalTableview;
    @FXML private TableColumn<AdvantagedAnnotationTableComponent, Boolean> inversedExternalTableview;

    private ObservableList<AdvantagedAnnotationTableComponent> interpretationCodeAnnotations = FXCollections.observableArrayList();
    @FXML private TableView<AdvantagedAnnotationTableComponent> interpretationTableview;
    @FXML private TableColumn<AdvantagedAnnotationTableComponent, String> systemInterpretTableview;
    @FXML private TableColumn<AdvantagedAnnotationTableComponent, String> codeInterpretTableview;
    @FXML private TableColumn<AdvantagedAnnotationTableComponent, String> hpoInterpretTableview;
    @FXML private TableColumn<AdvantagedAnnotationTableComponent, Boolean> inversedInterpretTableview;



    @FXML private void initialize() {


        internalCodingSystem.setText(Loinc2HPOCodedValue.CODESYSTEM);

        initTableStructure();

        populateTables();

    }

    private void initTableStructure() {
        codeInternalTableview.setSortable(true);
        codeInternalTableview.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getCode().getCode())
        );
        hpoInternalTableview.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getHpoTermId4LoincTest().getHpoTerm().getName()));
        inversedInternalTableview.setCellValueFactory(cdf ->
                new ReadOnlyBooleanWrapper(cdf.getValue().getHpoTermId4LoincTest().isNegated()));
        internalTableview.setItems(internalCodeAnnotations);

        systemExternalTableview.setSortable(true);
        systemExternalTableview.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getCode().getSystem()));
        codeExternalTableview.setSortable(true);
        codeExternalTableview.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getCode().getCode()));
        hpoExternalTableview.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getHpoTermId4LoincTest().getHpoTerm().getName()));
        inversedExternalTableview.setCellValueFactory(cdf ->
                new ReadOnlyBooleanWrapper(cdf.getValue().getHpoTermId4LoincTest().isNegated()));
        externalTableview.setItems(externalCodeAnnotations);

        systemInterpretTableview.setSortable(true);
        systemInterpretTableview.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getCode().getSystem()));
        codeInterpretTableview.setSortable(true);
        codeInterpretTableview.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getCode().getCode()));
        hpoInterpretTableview.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getHpoTermId4LoincTest().getHpoTerm().getName()));
        inversedInterpretTableview.setCellValueFactory(cdf ->
                new ReadOnlyBooleanWrapper(cdf.getValue().getHpoTermId4LoincTest().isNegated()));
        interpretationTableview.setItems(interpretationCodeAnnotations);
    }

    private void populateTables(){

        if (model == null) {
            logger.error("model is null.");
            return;
        }

        this.currentAnnotation = model.getCurrentAnnotation();
        LoincEntry currentLoincEntry = model.getLoincEntryMap().get(currentAnnotation.getLoincId());
        annotationTitle.setText(String.format("Annotations for Loinc: %s[%s]", currentLoincEntry.getLOINC_Number(), currentLoincEntry.getLongName()));


        internalCodeAnnotations.clear();
        currentAnnotation.getCandidateHpoTerms().entrySet()
            .stream()
            .filter(p -> p.getKey().getSystem().equals(Loinc2HPOCodedValue.CODESYSTEM))
            .map(p -> new AdvantagedAnnotationTableComponent(p.getKey(), p.getValue()))
            .forEach(internalCodeAnnotations::add);

        externalCodeAnnotations.clear();
        currentAnnotation.getCandidateHpoTerms().entrySet().stream()
                .filter(p -> !p.getKey().getSystem().equals(Loinc2HPOCodedValue.CODESYSTEM))
                .map(p -> new AdvantagedAnnotationTableComponent(p.getKey(), p.getValue()))
                .forEach(externalCodeAnnotations::add);

        interpretationCodeAnnotations.clear();
        for (Map.Entry<Code, Code> entry: CodeSystemConvertor.getCodeConversionMap().entrySet()) {
            logger.debug("key: " + entry.getKey() + "\nvalue: " + entry.getValue());
            HpoTermId4LoincTest result = currentAnnotation.loincInterpretationToHPO(entry.getValue());
            logger.debug("result is null? " + (result == null));
            if (result != null) {
                AdvantagedAnnotationTableComponent annotation = new AdvantagedAnnotationTableComponent(entry.getKey(), result);
                interpretationCodeAnnotations.add(annotation);
                logger.debug("interpretationCodeAnnotations size: " + interpretationCodeAnnotations.size());
            }
        }

        logger.debug("internalTableview is null: " + (internalTableview == null));
        logger.debug("internalCodeAnnotations is null: " + (internalTableview == null));
        logger.debug("internal annotation size: " + internalCodeAnnotations.size());
        internalCodeAnnotations.forEach(logger::info);


        logger.trace("exit initInternalTableview()");


    }
    private void ExternalTableview(){

    }
    private void initInterpretTableview(){

    }

    public void setModel(Model model) {
        if (model == null) {
            logger.trace("model for CurrentAnnotationController is set to null");
            return;
        }
        this.model = model;
        logger.info("model for CurrentAnnotationController is set");
    }


    public void setCurrentLoincEntry(LoincEntry currentLoinc) {
        this.currentLoincEntry = currentLoinc;
    }

    public void setCurrentAnnotation(UniversalLoinc2HPOAnnotation currentAnnotation) {
        this.currentAnnotation = currentAnnotation;
        logger.info("current annotation is set successfully for: " + this.currentAnnotation.getLoincId());
        initTableStructure();
    }

    @FXML
    void handleEdit(ActionEvent event) {

        logger.debug("user wants to edit current annotation");
        annotateTabController.editCurrentAnnotation(currentAnnotation);
        mainController.switchTab(MainController.TabPaneTabs.AnnotateTabe);

        Node source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
        event.consume();
    }

    @FXML
    void handleSave(ActionEvent event) {
        System.out.println("user wants to save the annotation after editing");
        event.consume();
    }

    @FXML
    void setReferences(ActionEvent event) {
        System.out.println("user wants to set references");
        event.consume();

    }
}

