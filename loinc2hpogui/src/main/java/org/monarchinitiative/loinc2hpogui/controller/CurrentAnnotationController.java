package org.monarchinitiative.loinc2hpogui.controller;

import com.google.inject.Singleton;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpocore.codesystems.InternalCodeSystem;
import org.monarchinitiative.loinc2hpocore.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpocore.loinc.LoincEntry;

import org.monarchinitiative.loinc2hpogui.model.AdvancedAnnotationTableComponent;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;
import java.util.function.Consumer;

@Singleton
public class CurrentAnnotationController{
    private static final Logger logger = LogManager.getLogger();

    private BooleanProperty dataReady;
    private LoincEntry currentLoinc;
    private LOINC2HpoAnnotationImpl currentAnnotation;
    private CodeSystemConvertor codeSystemConvertor = new CodeSystemConvertor();

    @FXML private BorderPane currentAnnotationPane;

    @FXML private Label annotationTitle;
    @FXML private TextField internalCodingSystem;

    private ObservableList<AdvancedAnnotationTableComponent> internalCodeAnnotations = FXCollections.observableArrayList();
    @FXML private TableView<AdvancedAnnotationTableComponent> internalTableview;
    @FXML private TableColumn<AdvancedAnnotationTableComponent, String> codeInternalTableview;
    @FXML private TableColumn<AdvancedAnnotationTableComponent, String> hpoInternalTableview;
    @FXML private TableColumn<AdvancedAnnotationTableComponent, Boolean> inversedInternalTableview;

    private ObservableList<AdvancedAnnotationTableComponent> externalCodeAnnotations = FXCollections.observableArrayList();
    @FXML private TableView<AdvancedAnnotationTableComponent> externalTableview;
    @FXML private TableColumn<AdvancedAnnotationTableComponent, String> systemExternalTableview;
    @FXML private TableColumn<AdvancedAnnotationTableComponent, String> codeExternalTableview;
    @FXML private TableColumn<AdvancedAnnotationTableComponent, String> hpoExternalTableview;
    @FXML private TableColumn<AdvancedAnnotationTableComponent, Boolean> inversedExternalTableview;

    private ObservableList<AdvancedAnnotationTableComponent> interpretationCodeAnnotations = FXCollections.observableArrayList();
    @FXML private TableView<AdvancedAnnotationTableComponent> interpretationTableview;
    @FXML private TableColumn<AdvancedAnnotationTableComponent, String> systemInterpretTableview;
    @FXML private TableColumn<AdvancedAnnotationTableComponent, String> codeInterpretTableview;
    @FXML private TableColumn<AdvancedAnnotationTableComponent, String> hpoInterpretTableview;
    @FXML private TableColumn<AdvancedAnnotationTableComponent, Boolean> inversedInterpretTableview;

    private Consumer<LOINC2HpoAnnotationImpl> editHook;
    //private Consumer<LOINC2HpoAnnotationImpl> saveHook;

//    @Inject
//    private AppResources appResources;

    Map<TermId, Term> termMap;

    public void setTermMap(Map<TermId, Term> loincTermMap) {
        termMap = loincTermMap;
    }

    @FXML private void initialize() {


        internalCodingSystem.setText(InternalCodeSystem.SYSTEMNAME);

        initTableStructure();

        dataReady = new SimpleBooleanProperty(false);
        dataReady.addListener((obj, oldvalue, newvalue) -> {
            if (newvalue) {
                populateTables();
            }
        });

    }

    private void initTableStructure() {
        codeInternalTableview.setSortable(true);
        codeInternalTableview.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getCode().getCode())
        );
        hpoInternalTableview.setCellValueFactory(cdf -> {
            TermId termId = cdf.getValue().getHpoTerm4TestOutcome().getId();
            if (termMap.containsKey(termId)) {
                return new ReadOnlyStringWrapper(termMap.get(termId).getName());
            } else {
                return new ReadOnlyStringWrapper(termId.getValue());
            }
        });

        inversedInternalTableview.setCellValueFactory(cdf ->
                new ReadOnlyBooleanWrapper(cdf.getValue().getHpoTerm4TestOutcome().isNegated()));
        internalTableview.setItems(internalCodeAnnotations);

        systemExternalTableview.setSortable(true);
        systemExternalTableview.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getCode().getSystem()));
        codeExternalTableview.setSortable(true);
        codeExternalTableview.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getCode().getCode()));
        hpoExternalTableview.setCellValueFactory(cdf -> {
            TermId termId = cdf.getValue().getHpoTerm4TestOutcome().getId();
            if (termMap.containsKey(termId)) {
                return new ReadOnlyStringWrapper(termMap.get(termId).getName());
            } else {
                return new ReadOnlyStringWrapper(termId.getValue());
            }
        });
        inversedExternalTableview.setCellValueFactory(cdf ->
                new ReadOnlyBooleanWrapper(cdf.getValue().getHpoTerm4TestOutcome().isNegated()));
        externalTableview.setItems(externalCodeAnnotations);

        systemInterpretTableview.setSortable(true);
        systemInterpretTableview.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getCode().getSystem()));
        codeInterpretTableview.setSortable(true);
        codeInterpretTableview.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getCode().getCode()));
        hpoInterpretTableview.setCellValueFactory(cdf -> {
            TermId termId = cdf.getValue().getHpoTerm4TestOutcome().getId();
            if (termMap.containsKey(termId)) {
                return new ReadOnlyStringWrapper(termMap.get(termId).getName());
            } else {
                return new ReadOnlyStringWrapper(termId.getValue());
            }
        });
        inversedInterpretTableview.setCellValueFactory(cdf ->
                new ReadOnlyBooleanWrapper(cdf.getValue().getHpoTerm4TestOutcome().isNegated()));
        interpretationTableview.setItems(interpretationCodeAnnotations);
    }

    private void populateTables(){

        annotationTitle.setText(String.format("Annotations for Loinc: %s[%s]", currentLoinc.getLOINC_Number(), currentLoinc.getLongName()));


        internalCodeAnnotations.clear();
        currentAnnotation.getCandidateHpoTerms().entrySet()
            .stream()
            .filter(p -> p.getKey().getSystem().equals(InternalCodeSystem.SYSTEMNAME))
            .map(p -> new AdvancedAnnotationTableComponent(p.getKey(), p.getValue()))
            .forEach(internalCodeAnnotations::add);

        externalCodeAnnotations.clear();
        currentAnnotation.getCandidateHpoTerms().entrySet().stream()
                .filter(p -> !p.getKey().getSystem().equals(InternalCodeSystem.SYSTEMNAME))
                .map(p -> new AdvancedAnnotationTableComponent(p.getKey(), p.getValue()))
                .forEach(externalCodeAnnotations::add);

        interpretationCodeAnnotations.clear();

        for (Map.Entry<Code, Code> entry: codeSystemConvertor.getCodeConversionmap().entrySet()) {
            HpoTerm4TestOutcome result = currentAnnotation.loincInterpretationToHPO(entry.getValue());
            if (result != null) {
                AdvancedAnnotationTableComponent annotation = new AdvancedAnnotationTableComponent(entry.getKey(), result);
                interpretationCodeAnnotations.add(annotation);
            }
        }
    }

    public void setData(LoincEntry loincEntry, LOINC2HpoAnnotationImpl currentAnnotation) {
        this.currentLoinc = loincEntry;
        this.currentAnnotation = currentAnnotation;
        dataReady.set(true);
        logger.info("current annotation is set successfully for: " + this.currentAnnotation.getLoincId());
    }

    public void setEditHook(Consumer<LOINC2HpoAnnotationImpl> edit) {
        this.editHook = edit;
    }

    @FXML
    void handleEdit(ActionEvent event) {
        this.editHook.accept(currentAnnotation);
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

