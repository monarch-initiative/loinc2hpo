package org.monarchinitiative.loinc2hpo.gui;

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
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpo.fhir.FHIRResourceGenerator;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationPopup {
    private static final Logger logger = LogManager.getLogger();
    private Map<Patient, List<Observation>> simulatedData;
    private FHIRResourceGenerator generator;

    public SimulationPopup(Map<LoincId, LoincEntry> loincEntryMap) {
        this.generator = new FHIRResourceGenerator(loincEntryMap);
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

        gridPane.add(new Label("Upload?"), 0, 2);
        CheckBox checker = new CheckBox();
        checker.setSelected(false);
        gridPane.add(checker, 1, 2);


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
                List<Patient> patientList = generator.generatePatient(numPatient);
                //numObservationPerPatient = Integer.parseInt(observationsPerPatient.getText());
                //by default, generate 10 observations per patient
                List<LoincId> loincIds = generator.loincExamples();
                this.simulatedData = generator.randPatientAndObservation(patientList, loincIds);
            } catch (Exception exp) {
                PopUps.showWarningDialog("Warning","Error message",
                        "Illegal input. Type in numbers only");
                return;
            }
            if (checker.isSelected()) {
                logger.trace("to upload to server");
            } else {
                //we are done
            }
            window.close();
        });

        root.getChildren().addAll(gridPane, hbox);
        Scene scene = new Scene(root, 450, 300);
        window.setScene(scene);
        window.showAndWait();
    }

    /**
     * Return simulated data
     * @return
     */
    public Map<Patient, List<Observation>> getSimulatedData() {
        return new HashMap<>(this.simulatedData);
    }



}
