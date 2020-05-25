package org.monarchinitiative.loinc2hpogui.gui;

import ca.uhn.fhir.rest.client.exceptions.FhirClientConnectionException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpocore.fhir.FhirServer;
import org.monarchinitiative.loinc2hpocore.fhir.FhirServerDstu3Impl;
import org.monarchinitiative.loinc2hpogui.model.AppTempData;

import java.util.List;

public class FhirServerPopup {

    private static final Logger logger = LogManager.getLogger();
    private AppTempData appTempData;
    private String base;
    private FhirServer fhirServerl;
    private List<Patient> patientList;

    public FhirServerPopup(AppTempData appTempData) {
        this.appTempData = appTempData;
    }

    public void displayWindow() {
        Stage window = new Stage();
        window.setTitle("Server Settings");
        window.setResizable(false);
        window.centerOnScreen();
        window.initStyle(StageStyle.UTILITY);
        window.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(5);

        HBox baseUrlHBox = new HBox();
        baseUrlHBox.setSpacing(10);
        Label baseUrl = new Label("base URL");
        ComboBox<String> urlSelections = new ComboBox<>();
        urlSelections.getItems().addAll(appTempData.getFhirServers());
        urlSelections.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (observable != null && newValue != null) {
                    appTempData.setFhirServer(newValue);
                    fhirServerl = new FhirServerDstu3Impl(newValue);
                } else {
                    //do nothing
                }
            }
        });
        baseUrlHBox.getChildren().addAll(baseUrl, urlSelections);
        if (!urlSelections.getItems().isEmpty()) {
            urlSelections.getSelectionModel().select(0);
        }

        ComboBox<QueryMode> queryMode = new ComboBox<>();
        queryMode.setPromptText("Select one");
        queryMode.getItems().addAll(QueryMode.values());

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        Label modeLabel = new Label("query mode");
        gridPane.add(modeLabel, 0, 0);

        gridPane.add(queryMode, 1, 0);

        Label resourceId = new Label("resource id");
        gridPane.add(resourceId, 0, 1);

        TextField resourceText = new TextField();
        resourceText.setPromptText("comma separated");
        gridPane.add(resourceText, 1, 1);

        Label identifier = new Label("identifier");
        gridPane.add(identifier, 0, 2);
        TextField system = new TextField("");
        system.setPromptText("system");
        gridPane.add(system, 1, 2);
        TextField id_identifier = new TextField();
        id_identifier.setPromptText("id");
        gridPane.add(id_identifier, 1, 3);

        Label name = new Label("name");
        gridPane.add(name, 0, 4);
        TextField firstName = new TextField();
        firstName.setPromptText("first name");
        gridPane.add(firstName, 1, 4);
        TextField lastName = new TextField();
        lastName.setPromptText("last name");
        gridPane.add(lastName, 1, 5);

        Label other = new Label("other info");
        gridPane.add(other, 0, 6);
        TextField zipcodeField = new TextField();
        zipcodeField.setPromptText("zip code");
        gridPane.add(zipcodeField, 1, 6);
        TextField telcomField = new TextField();
        telcomField.setPromptText("phone or email");
        gridPane.add(telcomField, 1, 7);

        Region blank = new Region();
        blank.setMinHeight(20);
        blank.setPrefHeight(20);
        blank.setMinHeight(25);

        HBox hBox = new HBox();
        Button cancel = new Button("Cancel");
        Button confirm = new Button("Confirm");
        hBox.setSpacing(20);
        hBox.getChildren().addAll(cancel, confirm);

        root.getChildren().addAll(baseUrlHBox, gridPane, blank, hBox);
        Scene scene = new Scene(root, 300, 400);
        window.setScene(scene);

        queryMode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<QueryMode>() {
            @Override
            public void changed(ObservableValue<? extends QueryMode> observable, QueryMode oldValue, QueryMode newValue) {
                if (observable != null && newValue != null) {
                    switch (newValue) {
                        case RESOURCEID:
                            resourceText.setDisable(false);
                            system.setDisable(true);
                            id_identifier.setDisable(true);
                            firstName.setDisable(true);
                            lastName.setDisable(true);
                            zipcodeField.setDisable(true);
                            telcomField.setDisable(true);
                            break;
                        case IDENTIFIER:
                            resourceText.setDisable(true);
                            system.setDisable(false);
                            id_identifier.setDisable(false);
                            firstName.setDisable(true);
                            lastName.setDisable(true);
                            zipcodeField.setDisable(true);
                            telcomField.setDisable(true);
                            break;
                        case NAME:
                            resourceText.setDisable(true);
                            system.setDisable(true);
                            id_identifier.setDisable(true);
                            firstName.setDisable(false);
                            lastName.setDisable(false);
                            zipcodeField.setDisable(true);
                            telcomField.setDisable(true);
                            break;
                        case NAME_X:
                            resourceText.setDisable(true);
                            system.setDisable(true);
                            id_identifier.setDisable(true);
                            firstName.setDisable(false);
                            lastName.setDisable(false);
                            zipcodeField.setDisable(false);
                            telcomField.setDisable(false);
                            break;
                        default:
                            break;
                    }
                } else {
                    //do nothing
                }
            }
        });

        cancel.setOnAction(p -> {
            logger.trace("user cancels");
            window.close();

        });

        confirm.setOnAction(c -> {
            logger.trace("user confirms");
            QueryMode selected = queryMode.getSelectionModel().getSelectedItem();
            if (selected == null) {
                PopUps.showWarningDialog("Warning", "Unspecified query mode", "You have to choose a query mode");
                return;
            } else {
                try {
                    switch (selected) {
                        case RESOURCEID:
                            if (resourceText.getText().isEmpty()) {
                                break;
                            }
                            patientList = this.fhirServerl.getPatient(resourceText.getText());
                            break;
                        case NAME:
                            String firstN = firstName.getText();
                            String lastN = lastName.getText();
                            String birthday = "";
                            if (firstN.isEmpty() && lastN.isEmpty() && birthday.isEmpty()) {
                                break;
                            }
                            patientList = this.fhirServerl.getPatient(firstN, lastN);
                            break;
                        case IDENTIFIER:
                            String systemInput = system.getText();
                            String idInput = id_identifier.getText();
                            Identifier identifierKey = new Identifier();
                            identifierKey.setSystem(systemInput).setValue(idInput);
                            if (systemInput.isEmpty() && idInput.isEmpty()) {
                                break;
                            }
                            patientList = this.fhirServerl.getPatient(identifierKey);
                            break;
                        case NAME_X:
                            firstN = firstName.getText();
                            lastN = lastName.getText();
                            String zipcode = zipcodeField.getText();
                            String phoneOrEmail = telcomField.getText();
                            if (firstN.isEmpty() && lastN.isEmpty() && zipcode.isEmpty() && phoneOrEmail.isEmpty()) {
                                break;
                            }
                            patientList = this.fhirServerl.getPatient(firstN, lastN, phoneOrEmail, zipcode);
                            break;
                        default:
                            break;
                    }
                } catch (FhirClientConnectionException exception) {
                    PopUps.showWarningDialog("Server error", "Timeout", "Server error, try again later");
                }

            }
            window.close();
        });

        window.showAndWait();
    }

    enum QueryMode {
        RESOURCEID,
        IDENTIFIER,
        NAME,
        NAME_X
    }

    public List<Patient> getPatientList() {
        return this.patientList;
    }

    public List<Observation> getObservationsForPatient(Patient patient) {
        return fhirServerl.getObservation(patient);
    }

}
