package org.monarchinitiative.loinc2hpogui.controller;


import ca.uhn.fhir.parser.DataFormatException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.exceptions.FHIRException;
import org.monarchinitiative.loinc2hpocore.exception.*;
import org.monarchinitiative.loinc2hpocore.fhir2hpo.*;
import org.monarchinitiative.loinc2hpogui.gui.FhirServerPopup;
import org.monarchinitiative.loinc2hpogui.gui.PopUps;
import org.monarchinitiative.loinc2hpogui.gui.SimulationPopup;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.loinc2hpogui.model.AppResources;
import org.monarchinitiative.loinc2hpogui.model.AppTempData;
import org.monarchinitiative.loinc2hpocore.phenotypemodel.LabTest;
import org.monarchinitiative.loinc2hpocore.phenotypemodel.LabTestImpl;
import org.monarchinitiative.loinc2hpocore.phenotypemodel.LabTestOutcome;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Date;

@Singleton
public class Loinc2HpoConversionTabController {

    private static final Logger logger = LogManager.getLogger();
    private AppTempData appTempData = null;
    @Inject private AppResources appResources;

    public void setAppTempData(AppTempData appTempData) {
        this.appTempData = appTempData;
        logger.trace("appTempData in loinc2HPOConversitonTabController is set");
    }


    @FXML private Button importFromLocalButton;
    @FXML private Button importFromServerButton;
    @FXML private Button simulateButton;
    @FXML private Button clearButton;

    @FXML
    private ListView<ResourceListViewComponent> patientRecordListView;
    private ObservableList<ResourceListViewComponent> resources = FXCollections.observableArrayList();

    @FXML
    private ListView<String> patientPhenotypeTableView;
    private ObservableList<String> displays = FXCollections.observableArrayList();

    @FXML
    private Button convertButton;


    @FXML private void initialize() {
        patientRecordListView.setItems(resources);
        patientPhenotypeTableView.setItems(displays);
        importFromLocalButton.setTooltip(new Tooltip("import observations from local file"));
        importFromServerButton.setTooltip(new Tooltip("import observations from FHIR server"));
        simulateButton.setTooltip(new Tooltip("simulate observations"));
        clearButton.setTooltip(new Tooltip("clear observations"));
        convertButton.setTooltip(new Tooltip("convert observation to HPO term"));

        patientRecordListView.setOnMouseClicked(event -> {
            if (patientRecordListView.getSelectionModel().getSelectedItem() != null) {
                ResourceListViewComponent selected = patientRecordListView.getSelectionModel().getSelectedItem();
                if (event.getButton()== MouseButton.SECONDARY && selected.resource instanceof Patient) {
                    logger.trace("patient is selected");
                    MenuItem download = new MenuItem("download observations");
                    ContextMenu cmu = new ContextMenu();
                    cmu.getItems().addAll(download);
                    cmu.show(convertButton.getScene().getWindow());
                    download.setOnAction(e -> {
                        FhirServer server = new FhirServerDstu3Impl(appTempData.getFhirServer());
                        Patient selectedPatient = (Patient) selected.resource;
                        //FhirResourceParser parser = new FhirResourceParserDstu3();
                        //parser.setPrettyPrint(true);
                        //System.out.println(parser.toJson(selectedPatient));

                        List<Observation> observations = server.getObservation(selectedPatient);
                        logger.trace("num of observations: " + observations.size());
                        if (!observations.isEmpty()) {
                            resources.clear();
                            resources.addAll(
                                    observations.stream()
                                            .map(ResourceListViewComponent::new)
                                            .collect(Collectors.toList()));
                        }

                    });
                }
            }
        });
    }


    /**
     * Private class that defines how to display an observation resource
     */
    private class ResourceListViewComponent {

        private Resource resource;
        private FhirResourceParser parser = new FhirResourceParserDstu3();

        protected ResourceListViewComponent(Resource resource) {

            this.resource = resource;

        }

        @Override
        public String toString() {

            parser.setPrettyPrint(true);
            return parser.toJson(this.resource);

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
                    FhirResourceParser parser = new FhirResourceParserDstu3();
                    Observation o = (Observation) parser.parse(file);
                    resources.add(new ResourceListViewComponent(o));
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
        FhirServerPopup serverPopup = new FhirServerPopup(appTempData);
        serverPopup.displayWindow();
        resources.clear();
        if (serverPopup.getPatientList() != null && !serverPopup.getPatientList().isEmpty()) {
            resources.addAll(
                    serverPopup.getPatientList()
                            .stream()
                            .map(ResourceListViewComponent::new)
                            .collect(Collectors.toList())
            );
        }
    }


    @FXML
    private void simulate(ActionEvent e) {
        e.consume();
        logger.trace("simulate patient observations");
        SimulationPopup popup = new SimulationPopup(appResources.getLoincEntryMap());
        //User will interact with the app in the popup window
        popup.displayWindow();
        if (popup.uploadedToServer()) { //simulated data is requested to upload to server
            if (popup.getPatientUploadedToServer() != null && !popup.getPatientUploadedToServer().isEmpty()) {
                resources.clear();
                logger.trace("patient num: " + popup.getPatientUploadedToServer().size());
                resources.addAll(
                        popup.getPatientUploadedToServer()
                                .stream()
                                .map(ResourceListViewComponent::new)
                                .collect(Collectors.toList())
                );
            }
        } else { //simulated data is not requested to upload to server
            if (popup.getSimulatedData() != null && !popup.getSimulatedData().isEmpty()) {
                resources.clear();
                popup.getSimulatedData().values().forEach(l -> //l is a list of observations for a patient
                        //for every observation in the list, map to the ResourceListViewComponent and add to list
                        resources.addAll(l.stream().map(ResourceListViewComponent::new).collect(Collectors.toList())));
            }
        }

    }

    @FXML
    private void handleClear(ActionEvent e) {
        e.consume();
        resources.clear();
        displays.clear();
    }

    @FXML
    void handleConvertButton(ActionEvent event) {
        event.consume();

        displays.clear();
        resources.stream()
                .filter(o -> o.resource instanceof Observation)
                .map(o -> (Observation) o.resource)
                .forEach(o -> convert(o));
    }


    private void convert(Observation o) {

        FhirObservationAnalyzer.setObservation(o);
        try {
            LabTestOutcome outcome = FhirObservationAnalyzer.getHPO4ObservationOutcome(appResources.getLoincEntryMap().keySet(), appResources.getLoincAnnotationMap());
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

    //wrap basic test outcome in a complete labtest record
    private LabTest addTestMetaInfo(Observation o, LabTestOutcome basicOutcome, Map<Identifier, Patient> patientMap) throws FHIRException, MalformedLoincCodeException, UnsupportedCodingSystemException, LoincCodeNotFoundException {
        LabTest labTest;

        Patient subject = null;
        if (o.hasIdentifier()) {
            List<Patient> candidate = o.getIdentifier().stream()
                    .map(patientMap::get)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            if (candidate.size() == 1) { //candidate has to be unique
                subject = candidate.get(0);
            }
        }
        Date effectiveStartDate;
        Date effectiveEndDate;
        if (o.hasEffectiveDateTimeType()) {
        effectiveStartDate =  o.getEffectiveDateTimeType().getValue();
        effectiveEndDate = effectiveStartDate;
        } else if (o.hasEffectivePeriod()) {
        effectiveStartDate = o.getEffectivePeriod().getStart();
        effectiveEndDate = o.getEffectivePeriod().getEnd();
        } else {
        effectiveStartDate = null; //no test date
        effectiveEndDate = null;
        }
        FhirObservationAnalyzer.setObservation(o);
        LoincId loincId = FhirObservationAnalyzer.getLoincIdOfObservation();

        return new LabTestImpl.Builder()
                .effectiveStart(effectiveStartDate)
                .effectiveEnd(effectiveEndDate)
                .loincId(loincId)
                .outcome(basicOutcome)
                .resourceId(o.getId())
                .patient(subject)
                .build();
    }


    private String display(LabTestOutcome testOutcome) {
        //It is fine to use the subject for display purposes, but for computation we should use identifier as subject is not guaranteed unique
        String subject = testOutcome.getSubjectReference() != null && testOutcome.getSubjectReference().getReference() != null ?
                testOutcome.getSubjectReference().getReference() : "Unknown subject";
        TermId termId = testOutcome.getOutcome() == null ? null : testOutcome.getOutcome().getId();
        String name;
        if (termId != null) {
            name = appResources.getTermidTermMap().get(termId).getName();
        } else {
            name = "failed to interpret";
        }
        String id = termId == null ? " " : termId.getValue();

        String display;
        if (testOutcome.getOutcome().isNegated()) {
            display = String.format("%s: NOT %s [%s]",subject, name, id);
        } else {
            display = String.format("%s: %s [%s]", subject, name, id);
        }
        return display;
    }

}
