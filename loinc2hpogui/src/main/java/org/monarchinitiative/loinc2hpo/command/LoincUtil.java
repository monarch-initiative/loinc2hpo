package org.monarchinitiative.loinc2hpo.command;


import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LoincUtil {
    private static final Logger logger = LogManager.getLogger();

    private final Window window;


    public LoincUtil(Window w) {
        window=w;
    }

    public String getPathToLoinc() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Indicate LOINC Core Table csv file");
        File file = chooser.showOpenDialog(this.window);
        DirectoryChooser dirChooser = new DirectoryChooser();
        if (file==null || file.getAbsolutePath().isEmpty()) {
            System.err.println("Error Could not get path to LOINC Core Table file.");
            return null;
        }
        return file.getAbsolutePath();
    }


    public static void initLoincAnnotationFile(File f) {
        String header= LoincEntry. getHeaderLine();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            writer.write(header + "\n");
            writer.close();

        } catch (IOException e) {
            logger.error("Could not initialize LOINC2HPO annotation file");
            logger.error(e.getMessage());
        }
    }

}
