package org.monarchinitiative.loinc2hpo.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;
import org.monarchinitiative.loinc2hpo.gui.PopUps;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.UniversalLoinc2HPOAnnotation;
import org.monarchinitiative.loinc2hpo.model.Annotation;
import org.monarchinitiative.loinc2hpo.model.Model;

@Singleton
public class CurrentAnnotationController {
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private Model model;

    private LoincEntry currentLoincEntry = null;
    private UniversalLoinc2HPOAnnotation currentAnnotation = null;

    @FXML
    private BorderPane currentAnnotationPane;

    @FXML
    private Label annotationTitle;

    private ObservableList<Annotation> internalAnnotations = FXCollections.observableArrayList();
    @FXML
    private TableView<Annotation> internalTableview;

    @FXML
    private TableColumn<Annotation, String> codeInternalTableview;

    @FXML
    private TableColumn<Annotation, String> hpoInternalTableview;

    @FXML
    private TableColumn<Annotation, Boolean> inversedInternalTableview;

    @FXML
    private TableView<Annotation> externalTableview;

    @FXML
    private TableColumn<Annotation, String> systemExternalTableview;

    @FXML
    private TableColumn<Annotation, String> codeExternalTableview;

    @FXML
    private TableColumn<Annotation, String> hpoExternalTableview;

    @FXML
    private TableColumn<Annotation, String> inversedExternalTableview;

    @FXML
    private TableView<Annotation> interpretationTableview;

    @FXML
    private TableColumn<Annotation, String> systemInterpretTableview;

    @FXML
    private TableColumn<Annotation, String> codeInterpretTableview;

    @FXML
    private TableColumn<Annotation, String> hpoInterpretTableview;

    @FXML
    private TableColumn<Annotation, String> inversedInterpretTableview;

    @FXML private void initialize() {
        if (model != null) {
            setModel(model);
        }

        codeInternalTableview.setSortable(true);
        codeInternalTableview.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getCode().toString())
        );
        hpoInternalTableview.setCellValueFactory(cdf ->
                new ReadOnlyStringWrapper(cdf.getValue().getHpoTermId4LoincTest().getHpoTerm().getName()));
        inversedInternalTableview.setCellValueFactory(cdf ->
                new ReadOnlyBooleanWrapper(cdf.getValue().getHpoTermId4LoincTest().isNegated()));
        internalTableview.setItems(internalAnnotations);
        logger.debug("internalTableview is null: " + (internalTableview == null));
        logger.debug("internalAnnotations is null: " + (internalTableview == null));
    }

    private void initInternalTableview(){

        if (this.currentAnnotation == null) {
            PopUps.showInfoMessage("There is not annotation to review. Select a loinc entry and try again",
                    "No content to show");
            return;
        }

        currentAnnotation.getCandidateHpoTerms().entrySet()
        .stream()
        .filter(p -> p.getKey().getSystem().equals(Loinc2HPOCodedValue.CODESYSTEM))
        //.map(p -> new Annotation(p.getKey(), p.getValue()))
        .forEach(p -> internalAnnotations.add(new Annotation(p.getKey(), p.getValue())));

logger.debug("internal annotation size: " + internalAnnotations.size());

logger.debug("internalTableview is null: " + (internalTableview == null));
logger.debug("internalAnnotations is null: " + (internalTableview == null));
        internalTableview.setItems(internalAnnotations);



    }
    private void initExternalTableview(){

    }
    private void initInterpretTableview(){

    }

    public void setModel(Model model) {
        this.model = model;
    }


    public void setCurrentLoincEntry(LoincEntry currentLoinc) {
        this.currentLoincEntry = currentLoinc;
    }

    public void setCurrentAnnotation(UniversalLoinc2HPOAnnotation currentAnnotation) {
        this.currentAnnotation = currentAnnotation;
        logger.info("current annotation is set successfully for: " + this.currentAnnotation.getLoincId());
        initInternalTableview();
    }

    @FXML
    void handleEdit(ActionEvent event) {

    }

    @FXML
    void handleSave(ActionEvent event) {

    }

    @FXML
    void setReferences(ActionEvent event) {

    }

}

