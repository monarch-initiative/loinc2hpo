package org.monarchinitiative.loinc2hpo.gui.loinc2hpomain;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.command.GetPathToLoincFileCommand;
import org.monarchinitiative.loinc2hpo.model.Model;


import java.net.URL;
import java.util.ResourceBundle;

public class MainPresenter implements Initializable {
    private static final Logger logger = LogManager.getLogger();

    private Model model=null;

    @FXML
    AnchorPane pane;

    @FXML
    MenuItem downloadOrSpecifyLoincCoreTableMenuItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.trace("initialize() called");
    }



    public Pane getRootPane() { return this.pane; }
    public void setModel(Model m) { this.model=m; }


    /**
     * Get the path to the LOINC Core table file from the user.
     * @param e
     */
    @FXML public void downloadOrSpecifyLoincCoreTable(ActionEvent e) {
        Window mainWindow = pane.getScene().getWindow();
        GetPathToLoincFileCommand command = new GetPathToLoincFileCommand(mainWindow);
        String path=command.getPathToLoinc();
        model.setPathToLoincCoreTableFile(path);
        e.consume();
    }
}
