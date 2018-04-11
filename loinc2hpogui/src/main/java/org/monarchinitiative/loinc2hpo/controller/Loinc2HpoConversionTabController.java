package org.monarchinitiative.loinc2hpo.controller;


import com.google.inject.Singleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.loinc2hpo.fhir.FhirObservationAnalyzer;
import org.monarchinitiative.loinc2hpo.fhir.FhirResourceRetriever;
import org.monarchinitiative.loinc2hpo.model.Model;
import org.monarchinitiative.loinc2hpo.testresult.LabTestOutcome;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.*;

@Singleton
public class Loinc2HpoConversionTabController {

    private static final Logger logger = LogManager.getLogger();
    private Model model = null;

    public void setModel(Model model) {
        this.model = model;
        logger.trace("model in loinc2HPOConversitonTabController is set");
    }


    @FXML
    private Button importPatientDataButton;

    @FXML
    private ListView<String> patientRecordListView;

    @FXML
    private ListView<String> patientPhenotypeTableView;

    @FXML
    private Button convertButton;

    @FXML
    void handleConvertButton(ActionEvent event) {
        event.consume();
        String path = model.getPathToJsonFhirFile();
        Observation observation = FhirResourceRetriever.parseJsonFile2Observation(path);
        FhirObservationAnalyzer.setObservation(observation);
        LabTestOutcome res = FhirObservationAnalyzer.getHPO4ObservationOutcome(model.getLoincIds(), model.getLoincAnnotationMap());
        ObservableList<String> items = FXCollections.observableArrayList ();
        if (res == null) {
            items.add(observation.getId() + ": failed not interpret");
        } else {
            TermId id = res.getOutcome().getId();
            String name = model.termId2HpoName(id);
            //It is fine to use the subject for display purposes, but for computation we should use identifier as subject is not guaranteed unique
            String subject = res.getSubjectReference() != null && res.getSubjectReference().getReference() != null ?
                    res.getSubjectReference().getReference() : "Unknown subject";
            String display = String.format("%s: %s [%s]", subject, name,id.getIdWithPrefix());
            if (res.getOutcome().isNegated()) {
                display = String.format("%s: NOT %s [%s]",subject, name,id.getIdWithPrefix());
            }
            items.add(display);
        }
        patientPhenotypeTableView.setItems(items);
    }

    @FXML
    void handleImportantPatientData(ActionEvent event) {
        event.consume();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose FHIR observation JSON file");
        File f = chooser.showOpenDialog(null);
        String path=null;
        if (f != null) {
             path = f.getAbsolutePath();
             model.setFhirFilePath(path);
        } else {
            logger.trace("Unable to obtain path to FHIR observation JSON file");
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            ObservableList<String> items = FXCollections.observableArrayList ();
            String line;
            while ((line=br.readLine())!= null) {
                items.add(line);
            }
            patientRecordListView.setItems(items);
        } catch (IOException e) {
            logger.error("An error happened during reading Json file: " + path);
        }
    }

}
