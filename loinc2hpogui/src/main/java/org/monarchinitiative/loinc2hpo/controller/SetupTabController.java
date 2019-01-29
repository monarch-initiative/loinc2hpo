package org.monarchinitiative.loinc2hpo.controller;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.model.AppTempData;

@Singleton
/**
 * This class can be removed
 */
@Deprecated
public class SetupTabController {
    private static final Logger logger = LogManager.getLogger();

    private AppTempData appTempData =null;

    @FXML private TextFlow textflow;

    @Inject MainController mainController;

    @FXML private void initialize() {
        initTextFlow();
    }

    public void setAppTempData(AppTempData m) {
        appTempData =m;
    }


    private void initTextFlow() {
        Text text1 = new Text("\n\tLOINC2HPO: An app for biocuration LOINC to HPO mappings");
        text1.setFont(new Font(15));
        text1.setFill(Color.DARKSLATEBLUE);
        Text text2 = new Text("\n\n\t\t1. Use the edit menu to download hp.obo");
        Text text3 = new Text("\n\t\t2. Use the edit menu to tell the app where the LOINC file is located");
        Text text4 = new Text("\n\t\t3. Use the edit menu to indicate your biocurator id");
        Text text5 = new Text("\n\t\t4. Indicate location of loinc2hpo.tab curation file (usually the file from GitHub)");

        ObservableList list = textflow.getChildren();
        list.addAll(text1,text2,text3,text4,text5);
    }


}




