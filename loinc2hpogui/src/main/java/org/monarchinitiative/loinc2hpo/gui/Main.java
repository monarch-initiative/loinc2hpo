package org.monarchinitiative.loinc2hpo.gui;


import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.controller.MainController;
import org.monarchinitiative.loinc2hpo.io.Loinc2HpoPlatform;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;


/**
 * The driver class of the LOINC2HPO biocuration app, which is intended to help annotate LOINC codes to the relevantHPO Terms.
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @author <a href="mailto:aaron.zhang@jax.org">Aaron Zhang</a>
 * @version 0.1.2
 */

public class Main extends Application {

    private static final String WINDOW_TITLE = "LOINC 2 HPO Biocuration App";

    private static final Logger logger = LogManager.getLogger();

    private static Stage primarystage;

    private Parent rootNode;

    public static void main(String[] args) {
        launch(args);
    }

    @Inject
    private MainController mainController;

    @Override
    public void init() throws IOException {
        final Injector injector = Guice.createInjector(new DepInjectionModule());
        final Callback<Class<?>, Object> guiceFactory = clazz -> injector.getInstance(clazz);
        rootNode = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"),
                null, //  The resource bundle, useful to internationalised apps. Null here.
                new JavaFXBuilderFactory(),
                // The controller factory that will be a Guice factory:
                // this Guice factory will manage the instantiation of the controllers and their dependency injections.
                guiceFactory);
        //the following two lines both works; not sure what the first line is
        //mainController = injector.getInstance(Key.get(MainController.class));
        mainController = injector.getInstance(MainController.class);

    }



    @Override
    public void start(Stage window) throws Exception {
        primarystage = window;
        window.setScene(new Scene(rootNode));
        Image image = new Image(Main.class.getResourceAsStream("/img/icon.jpg"));
        window.getIcons().add(image);
        window.setTitle(WINDOW_TITLE);
        if (Loinc2HpoPlatform.isMacintosh()) {
            try {
                URL iconURL = Main.class.getResource("/img/icon.jpg");
                java.awt.Image macimage = new ImageIcon(iconURL).getImage();
                com.apple.eawt.Application.getApplication().setDockIconImage(macimage);
                //stem.setProperty("org.monarchinitiative.loinc2hpo.gui.Main", "Loinc2Hpo");
            } catch (Exception e) {
                // Not for Windows or Linux. Just skip it!
            }
        }

        window.show();


        window.setOnCloseRequest(event -> {
            event.consume(); //important to consume it first; otherwise,
            //window will always close
            if (mainController.isSessionDataChanged()) {

                String[] choices = new String[] {"Yes", "No"};
                Optional<String> choice = PopUps.getToggleChoiceFromUser(choices,
                        "Session has been changed. Save changes? ", "Exit " +
                                "Confirmation");


                if (choice.isPresent() && choice.get().equals("Yes")) {
                    mainController.saveBeforeExit();
                    window.close();
                    Platform.exit();
                    System.exit(0);
                } else if (choice.isPresent() && choice.get().equals("No")) {
                    window.close();
                    Platform.exit();
                    System.exit(0);
                } else {
                    //hang on. No action required
                }
            } else {
                window.close();
                Platform.exit();
                System.exit(0);
            }

        });

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() throws Exception {

    }

    public static Stage getPrimarystage(){
        return primarystage;
    }

}
