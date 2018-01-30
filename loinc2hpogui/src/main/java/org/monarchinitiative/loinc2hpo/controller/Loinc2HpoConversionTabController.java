package org.monarchinitiative.loinc2hpo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phenomics.ontolib.ontology.data.TermId;
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
import org.monarchinitiative.loinc2hpo.fhir.FhirObservationRetriever;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.LoincTest;
import org.monarchinitiative.loinc2hpo.model.Model;
import org.monarchinitiative.loinc2hpo.testresult.TestResult;

import java.io.*;
import java.util.Map;

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
        String path = model.getPathToJsonFhirFile();
        ObjectMapper mapper = new ObjectMapper();
        File f = new File(path);
        try {
            FileInputStream fis = new FileInputStream(f);
            byte[] data = new byte[(int) f.length()];
            fis.read(data);
            fis.close();
            JsonNode node = mapper.readTree(data);
            Map<LoincId, LoincTest> testmap = model.getTestmap();
           //estmap=loincparser.getTestmap();
            TestResult res = FhirObservationRetriever.fhir2testrest(node,testmap);
            ObservableList<String> items = FXCollections.observableArrayList ();
            if (res==null) {
                items.add("Could not find test");
            } else {
                TermId id = res.getTermId();
                String name = model.termId2HpoName(id);
                String display = String.format("%s [%s]",name,id.getIdWithPrefix());
                if (res.isNegated()) {
                    display="NOT: "+display;
                }
                items.add(display);
            }
            patientPhenotypeTableView.setItems(items);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleImportantPatientData(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose FHIR observation JSON file");
        File f = chooser.showOpenDialog(null);
        String path=null;
        if (f != null) {
             path = f.getAbsolutePath();
             model.setFhirFilePath(path);
        } else {
            logger.error("Unable to obtain path to FHIR observation JSON file");
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
            e.printStackTrace();
        }
        logger.debug("user wants to import patient data");

    }

}
