package org.monarchinitiative.loinc2hpo.controller;

import com.google.inject.Singleton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.model.Model;

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
    private ListView<?> patientRecordTableView;

    @FXML
    private ListView<?> patientPhenotypeTableView;

    @FXML
    private Button convertButton;

    @FXML
    void handleConvertButton(ActionEvent event) {

    }

    @FXML
    void handleImportantPatientData(ActionEvent event) {

        logger.debug("user wants to import patient data");

    }

}
