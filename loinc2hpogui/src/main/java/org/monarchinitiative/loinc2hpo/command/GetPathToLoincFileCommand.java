package org.monarchinitiative.loinc2hpo.command;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.monarchinitiative.loinc2hpo.gui.popup.PopupFactory;

import java.io.File;

public class GetPathToLoincFileCommand {


    private final Window window;


    public GetPathToLoincFileCommand(Window w) {
        window=w;
    }

    public String getPathToLoinc() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Indicate LOINC Core Table csv file");
        File file = chooser.showOpenDialog(this.window);
        DirectoryChooser dirChooser = new DirectoryChooser();
        if (file==null || file.getAbsolutePath().isEmpty()) {
            PopupFactory.displayError("Error","Could not get path to LOINC Core Table file.");
            return null;
        }
        return file.getAbsolutePath();
    }

}
