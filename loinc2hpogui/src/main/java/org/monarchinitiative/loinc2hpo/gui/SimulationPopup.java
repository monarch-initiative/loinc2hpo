package org.monarchinitiative.loinc2hpo.gui;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpo.fhir.FhirResourceFaker;
import org.monarchinitiative.loinc2hpo.fhir.FhirResourceFakerImpl;
import org.monarchinitiative.loinc2hpo.fhir.FhirResourceRetriever;
import org.monarchinitiative.loinc2hpo.loinc.LOINCEXAMPLE;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationPopup {
    private static final Logger logger = LogManager.getLogger();
    private Map<Patient, List<Observation>> simulatedData;
    private FhirResourceFaker generator;

    public SimulationPopup(Map<LoincId, LoincEntry> loincEntryMap) {
        this.generator = new FhirResourceFakerImpl(loincEntryMap);
    }

    public void displayWindow() {
        Stage window = new Stage();
        window.setTitle("Simulation Settings");
        window.setResizable(false);
        window.centerOnScreen();
        window.initStyle(StageStyle.UTILITY);
        window.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(5);

        //Label label = new Label();
        //label.setText("Specify number of patients and number of observations for each patient");
        //root.getChildren().add(label);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 20, 20, 20));


        gridPane.add(new Label("# Patient:"), 0, 0);
        TextField patientNum = new TextField();
        patientNum.setPromptText("# of unique patients");
        patientNum.setText("1");
        gridPane.add(patientNum, 1, 0);

        gridPane.add(new Label("# Observations:"), 0, 1);
        TextField observationsPerPatient = new TextField();
        observationsPerPatient.setPromptText("# observations per patient");
        observationsPerPatient.setText("10");
        observationsPerPatient.setEditable(false);
        gridPane.add(observationsPerPatient, 1, 1);

        gridPane.add(new Label("Upload to server?"), 0, 2);
        CheckBox checker = new CheckBox("Yes");
        checker.setSelected(false);
        gridPane.add(checker, 1, 2);

        //Label serverAddress = new Label("Test Server: " + Constants.HAPIFHIRTESTSERVER);

        Button cancel = new Button("Cancel");
        Button confirm = new Button("Confirm");
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.getChildren().addAll(cancel, confirm);

        cancel.setOnAction(e -> window.close());
        confirm.setOnAction(e -> {
            int numPatient, numObservationPerPatient;
            try {
                numPatient = Integer.parseInt(patientNum.getText());
                List<Patient> patientList = generator.fakePatients(numPatient);
                //numObservationPerPatient = Integer.parseInt(observationsPerPatient.getText());
                //by default, generate 10 observations per patient
                List<LoincId> loincIds = LOINCEXAMPLE.loincExamples();
                this.simulatedData = generator.fakeObservations(patientList, loincIds);
            } catch (Exception exp) {
                PopUps.showWarningDialog("Warning","Error message",
                        "Illegal input. Type in numbers only");
                return;
            }
            if (checker.isSelected() && this.simulatedData != null && !this.simulatedData.isEmpty()) {
                logger.trace("to upload to server");
                //bundlize(this.simulatedData)
                // .forEach(FhirResourceRetriever::upload);
                //keep patient information to model
                //we need to retrieve patient id through identifiers
                //then use patient id to get their associated observations
            } else {
                //we are done
            }
            window.close();
        });

        root.getChildren().addAll(gridPane, hbox);
        Scene scene = new Scene(root, 300, 300);
        window.setScene(scene);
        window.showAndWait();
    }

    private List<Bundle> bundlize(Map<Patient, List<Observation>> observations) {
        List<Bundle> bundleList = new ArrayList<>();


        return bundleList;
    }


    /**
     * Return simulated data
     * @return
     */
    public Map<Patient, List<Observation>> getSimulatedData() {
        if (this.simulatedData == null) {
            return null;
        }
        return new HashMap<>(this.simulatedData);
    }



}
