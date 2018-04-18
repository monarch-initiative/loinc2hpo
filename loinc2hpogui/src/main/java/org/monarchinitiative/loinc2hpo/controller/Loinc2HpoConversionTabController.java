package org.monarchinitiative.loinc2hpo.controller;


import ca.uhn.fhir.parser.DataFormatException;
import com.google.inject.Singleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.exceptions.FHIRException;
import org.monarchinitiative.loinc2hpo.exception.*;
import org.monarchinitiative.loinc2hpo.fhir.FhirObservationAnalyzer;
import org.monarchinitiative.loinc2hpo.fhir.FhirResourceRetriever;
import org.monarchinitiative.loinc2hpo.gui.PopUps;
import org.monarchinitiative.loinc2hpo.gui.SimulationPopup;
import org.monarchinitiative.loinc2hpo.model.Model;
import org.monarchinitiative.loinc2hpo.testresult.LabTestOutcome;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class Loinc2HpoConversionTabController {

    private static final Logger logger = LogManager.getLogger();
    private Model model = null;

    public void setModel(Model model) {
        this.model = model;
        logger.trace("model in loinc2HPOConversitonTabController is set");
    }


    @FXML private Button importFromLocalButton;
    @FXML private Button importFromServerButton;
    @FXML private Button simulateButton;
    @FXML private Button clearButton;

    @FXML
    private ListView<ObservationListViewComponent> patientRecordListView;
    private ObservableList<ObservationListViewComponent> observations = FXCollections.observableArrayList();

    @FXML
    private ListView<String> patientPhenotypeTableView;
    private ObservableList<String> displays = FXCollections.observableArrayList();

    @FXML
    private Button convertButton;


    @FXML private void initialize() {
        patientRecordListView.setItems(observations);
        patientPhenotypeTableView.setItems(displays);
        importFromLocalButton.setTooltip(new Tooltip("import observations from local file"));
        importFromServerButton.setTooltip(new Tooltip("import observations from FHIR server"));
        simulateButton.setTooltip(new Tooltip("simulate observations"));
        clearButton.setTooltip(new Tooltip("clear observations"));
        convertButton.setTooltip(new Tooltip("convert observation to HPO term"));
    }


    /**
     * Private class that defines how to display an observation resource
     */
    private class ObservationListViewComponent {

        private Observation observation;

        protected ObservationListViewComponent(Observation observation) {

            this.observation = observation;

        }

        @Override
        public String toString() {

            return FhirResourceRetriever.toJsonString(this.observation);

        }
    }

    @FXML
    void handleImportantPatientData(ActionEvent event) {
        event.consume();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose FHIR observation JSON file");
        List<File> files = chooser.showOpenMultipleDialog(null);
        if (files != null && !files.isEmpty()) {
            for (File file : files) {
                try {
                    Observation o = FhirResourceRetriever.parseJsonFile2Observation(file);
                    observations.add(new ObservationListViewComponent(o));
                } catch (IOException e) {
                    PopUps.showWarningDialog("Warning", "Error importing" + file.getName(),
                            "Try to import the file again." );
                } catch (DataFormatException e) {
                    PopUps.showWarningDialog("Warning", "Malformed Json File",
                            "Invalid file format: " + file.getName());
                }
            }

        } else {
            logger.trace("Unable to obtain path to FHIR observation JSON file");
            return;
        }
    }


    @FXML
    private void importFromServer(ActionEvent e) {
        e.consume();
        logger.trace("import from fhir server");
    }


    @FXML
    private void simulate(ActionEvent e) {
        e.consume();
        logger.trace("simulate patient observations");
        SimulationPopup popup = new SimulationPopup(model.getLoincEntryMap());
        //User will interact with the app in the popup window
        popup.displayWindow();
        if (popup.getSimulatedData() != null && !popup.getSimulatedData().isEmpty()) {
            observations.clear();
            popup.getSimulatedData().values().forEach(l -> //l is a list of observations for a patient
                    //for every observation in the list, map to the ObservationListViewComponent and add to list
                observations.addAll(l.stream().map(ObservationListViewComponent::new).collect(Collectors.toList())));
        }
    }

    @FXML
    private void handleClear(ActionEvent e) {
        e.consume();
        observations.clear();
        displays.clear();
    }

    @FXML
    void handleConvertButton(ActionEvent event) {
        event.consume();

        displays.clear();
        observations.forEach(o -> {
            convert(o.observation);
        });
    }


    private void convert(Observation o) {

        FhirObservationAnalyzer.setObservation(o);
        try {
            LabTestOutcome outcome = FhirObservationAnalyzer.getHPO4ObservationOutcome(model.getLoincIds(), model.getLoincAnnotationMap());
            displays.add(display(outcome));
        } catch (MalformedLoincCodeException e) {
            displays.add(o.getSubject().getReference() + " : failed to interpret [code 11]");
        } catch (UnsupportedCodingSystemException e) {
            displays.add(o.getSubject().getReference() + " : failed to interpret [code 12]");
        } catch (LoincCodeNotFoundException e) {
            displays.add(o.getSubject().getReference() + " : failed to interpret [code 13]");
        } catch (AmbiguousResultsFoundException e) {
            displays.add(o.getSubject().getReference() + " : failed to interpret [code 14]");
        } catch (AnnotationNotFoundException e) {
            displays.add(o.getSubject().getReference() + " : failed to interpret [code 15]");
        } catch (UnrecognizedCodeException e) {
            displays.add(o.getSubject().getReference() + " : failed to interpret [code 16]");
        } catch (LoincCodeNotAnnotatedException e) {
            displays.add(o.getSubject().getReference() + " : failed to interpret [code 17]");
        } catch (AmbiguousReferenceException e) {
            displays.add(o.getSubject().getReference() + " : failed to interpret [code 18]");
        } catch (ReferenceNotFoundException e) {
            displays.add(o.getSubject().getReference() + " : failed to interpret [code 19]");
        } catch (FHIRException e) {
            displays.add(o.getSubject().getReference() + " : failed to interpret [code 20]");
        }
    }

    private String display(LabTestOutcome testOutcome) {
        //It is fine to use the subject for display purposes, but for computation we should use identifier as subject is not guaranteed unique
        String subject = testOutcome.getSubjectReference() != null && testOutcome.getSubjectReference().getReference() != null ?
                testOutcome.getSubjectReference().getReference() : "Unknown subject";
        TermId termId = testOutcome.getOutcome() == null ? null : testOutcome.getOutcome().getId();
        String name = termId == null ? "failed to interpret" : model.termId2HpoName(termId);
        String id = termId == null ? " " : termId.getIdWithPrefix();

        String display;
        if (testOutcome.getOutcome().isNegated()) {
            display = String.format("%s: NOT %s [%s]",subject, name, id);
        } else {
            display = String.format("%s: %s [%s]", subject, name, id);
        }
        return display;
    }

}
