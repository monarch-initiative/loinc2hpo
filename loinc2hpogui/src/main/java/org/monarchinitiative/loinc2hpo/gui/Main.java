package org.monarchinitiative.loinc2hpo.gui;

import com.genestalker.springscreen.core.DialogController;
import com.genestalker.springscreen.core.FXMLDialog;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.controller.InitializeResourcesController;
import org.monarchinitiative.loinc2hpo.gui.application.ApplicationConfig;
import org.monarchinitiative.loinc2hpo.gui.application.HRMDResourceManager;
import org.monarchinitiative.loinc2hpo.gui.application.ScreensConfig;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
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

    /**
     * Properties file containing configurable environment variables. Read once during app startup (creation of
     * ApplicationContext)
     */
    private static final String PROP_FILE_NAME = "hrmd-gui.properties";

    public static final String HRMD_RESOURCES = "hrmd-resources.json";

    private ConfigurableApplicationContext ctx;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage window) throws Exception {
        System.setProperty("propertiesPath", getPropertiesFilePath().toString());

        window.getIcons().add(new Image(getClass().getResourceAsStream("/img/app-icon.png")));
        initialize(window);
        ctx = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        ctx.getBeanFactory().registerSingleton("hostServices", getHostServices());
        ctx.registerShutdownHook();
        HRMDResourceManager resourceManager = ctx.getBean(HRMDResourceManager.class);
        ScreensConfig screensConfig = ctx.getBean(ScreensConfig.class);
        if (!resourceManager.isInitialized()) {
            screensConfig.setResourcesDialog().showAndWait();
        }
        screensConfig.setWindow(window);
        screensConfig.mainDialog().setTitle(WINDOW_TITLE);
        screensConfig.mainDialog().show();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() throws Exception {
        if (ctx != null) {
            ctx.close();
        }
    }

    /**
     * Checks before ApplicationContext initialization. Ensure that the resources have been initialized, otherwise the
     * GUI shuts down.
     *
     * @param stage primary Stage created by JavaFX application loader.
     */
    private static void initialize(Stage stage) throws Exception {
        File resources = new File(getJarFilePath().toFile(), HRMD_RESOURCES);
        HRMDResourceManager manager = new HRMDResourceManager(resources);
        if (manager.isInitialized()) {
            return;
        }
        URL entrez = new URL("ftp://ftp.ncbi.nih.gov/gene/DATA/GENE_INFO/Mammalia/Homo_sapiens.gene_info.gz");
        URL hpo = new URL("https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/hp.obo");
        InitializeResourcesController controller = new InitializeResourcesController(manager, entrez, hpo);
        setResourcesDialog(stage, controller).showAndWait();
    }


    /**
     * Get dialog window for setting resources and settings.
     *
     * @return {@link FXMLDialog} for setting resources and settings.
     */
    private static FXMLDialog setResourcesDialog(Stage stage, DialogController controller) {
        return new FXMLDialog.FXMLDialogBuilder()
                .setDialogController(controller)
                .setFXML(Main.class.getResource("/fxml/SetResourcesView.fxml"))
                .setOwner(stage)
                .setModality(Modality.WINDOW_MODAL)
                .setStageStyle(StageStyle.UNDECORATED)
                .build();
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
