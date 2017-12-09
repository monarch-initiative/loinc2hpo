package org.monarchinitiative.loinc2hpo.gui;

import com.genestalker.springscreen.core.DialogController;
import com.genestalker.springscreen.core.FXMLDialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The driver class of the HRMD gui app.
 */

public class Main extends Application {

    private static final String WINDOW_TITLE = "Human Regulatory Mutation Database GUI";

    private static final Logger logger = LogManager.getLogger();


    private Parent rootNode;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws IOException {
//
//        ClassLoader classLoader = Main.class.getClassLoader();
//        String fxmlpath = classLoader.getResource("fxml/main.fxml").getFile();
//        logger.trace(String.format("Path %s",fxmlpath));
//       // logger.trace(String.format("Getting resource %s",getClass().getResource("fxml/main.fxml").getPath()));
//       FXMLLoader fxmlLoader = new FXMLLoader(classLoader.getResource("fxml/main.fxml"));
//
//        rootNode = fxmlLoader.load();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        rootNode = fxmlLoader.load();
    }


    /**
     * Properties file containing configurable environment variables. Read once during app startup (creation of
     * ApplicationContext)
     */
    private static final String PROP_FILE_NAME = "hrmd-gui.properties";

    public static final String HRMD_RESOURCES = "hrmd-resources.json";




    @Override
    public void start(Stage window) throws Exception {
        window.setScene(new Scene(rootNode));
        window.show();
        window.setOnCloseRequest(e -> Platform.exit());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() throws Exception {

    }



    private static Path getPropertiesFilePath() {
        // We'll search for properties file on these paths to allow to run the app from terminal, distribution or IDE.
        String[] possiblePaths = {getJarFilePath().toString() + File.separator + "classes" + File.separator +
                PROP_FILE_NAME,
                getJarFilePath().toString() + File.separator + PROP_FILE_NAME};

        for (String possiblePath : possiblePaths) {
            Path p = Paths.get(possiblePath);
            if (Files.exists(p) && Files.isRegularFile(p)) {
                logger.info(String.format("Using properties file %s", p.toString()));
                return p;
            }
        }
        String joined = Arrays.stream(possiblePaths).collect(Collectors.joining(", "));
        logger.error(String.format("Unable to find properties file on these paths %s", joined));
        throw new RuntimeException(String.format("Unable to find properties file on these paths %s", joined));
    }

    private static Path getJarFilePath() {
        CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();
        try {
            return Paths.get(codeSource.getLocation().toURI()).getParent();
        } catch (URISyntaxException ex) {
            logger.error("Unable to find jar file", ex);
            throw new RuntimeException("Unable to find jar file", ex);
        }
    }

}
