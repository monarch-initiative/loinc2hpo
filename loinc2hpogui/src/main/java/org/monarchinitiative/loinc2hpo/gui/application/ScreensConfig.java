package org.monarchinitiative.loinc2hpo.gui.application;

import com.genestalker.springscreen.core.FXMLDialog;
import javafx.scene.Parent;
import javafx.stage.*;
import org.monarchinitiative.loinc2hpo.controller.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.io.File;
import java.util.Optional;

/**
 * This class happen to contain (maybe somehow artificially) the definitions and service requests for beans that are
 * being used as the elements of GUI interface or that are required for GUI in some other way. They are processed by the
 * Spring container to be generated at runtime. <p> Use <code>{@literal @}{@link Import}(ScreensConfig.class)</code> to
 * include bean definitions into top-level ApplicationConfig. </p> Created by Daniel Danis on 7/16/17.
 */
@Configuration
@Lazy
public class ScreensConfig {

    /**
     * Primary {@link Stage} created by JavaFX start method.
     */
    private Stage window;

    /**
     * Ask user to choose a directory
     *
     * @param ownerWindow      - Stage with which the DirectoryChooser will be associated
     * @param initialDirectory - Where to start the search
     * @param title            - Title of PopUp window
     * @return {@link Optional} object with {@link File} representing the selected directory.
     */
    public static Optional<File> selectDirectory(Stage ownerWindow, File initialDirectory, String title) {
        final DirectoryChooser dirchooser = new DirectoryChooser();
        dirchooser.setInitialDirectory(initialDirectory);
        dirchooser.setTitle(title);
        return Optional.ofNullable(dirchooser.showDialog(ownerWindow));
    }

    /**
     * Ask user to provide path to a File
     *
     * @param ownerWindow      - Stage with which the FileChooser will be associated
     * @param initialDirectory - Where to start the search
     * @param title            - Title of PopUp window
     * @return {@link Optional} object with {@link File} representing the selected file.
     */
    public static Optional<File> selectFileToOpen(Stage ownerWindow, File initialDirectory, String title) {
        final FileChooser filechooser = new FileChooser();
        filechooser.setInitialDirectory(initialDirectory);
        filechooser.setTitle(title);
        return Optional.ofNullable(filechooser.showOpenDialog(ownerWindow));
    }

    /**
     * Ask user to select path where he wants to save a File
     *
     * @param ownerWindow      Parent Stage object
     * @param initialDirectory Where to start the search
     * @param title            Title of PopUp window
     * @return {@link Optional} object with {@link File} representing selected filepath.
     */
    public static Optional<File> selectFileToSave(Stage ownerWindow, File initialDirectory, String title, String
            initialFileName) {
        final FileChooser filechooser = new FileChooser();
        filechooser.setInitialDirectory(initialDirectory);
        filechooser.setInitialFileName(initialFileName);
        filechooser.setTitle(title);
        return Optional.ofNullable(filechooser.showSaveDialog(ownerWindow));
    }

    /**
     * Set the primary {@link Stage} created by JavaFX start method.
     *
     * @param window primary {@link Stage} to be set.
     */
    public void setWindow(Stage window) {
        this.window = window;
    }

    @Bean
    public MainController mainController() {
        return new MainController();
    }

    public FXMLDialog mainDialog() {
        return new FXMLDialog.FXMLDialogBuilder()
                .setDialogController(mainController())
                .setFXML(getClass().getResource("/fxml/MainView.fxml"))
                .setOwner(window)
                .build();
    }

//    /**
//     * Get controller for {@link #setResourcesDialog()} dialog.
//     *
//     * @return {@link SetResourcesController} instance.
//     */
    @Bean
    public SetResourcesController setResourcesController() {
        return new SetResourcesController();
    }

    /**
     * Get dialog window for setting resources and settings.
     *
     * @return {@link FXMLDialog} for setting resources and settings.
     */
    public FXMLDialog setResourcesDialog() {
        return new FXMLDialog.FXMLDialogBuilder()
                .setDialogController(setResourcesController())
                .setFXML(getClass().getResource("/fxml/SetResourcesView.fxml"))
                .setOwner(window)
                .setModality(Modality.APPLICATION_MODAL)
                .setStageStyle(StageStyle.UNDECORATED)
                .build();
    }

    @Bean
    public ShowResourcesController showResourcesController() {
        return new ShowResourcesController();
    }
//
    public FXMLDialog showResourcesDialog() {
        return new FXMLDialog.FXMLDialogBuilder()
                .setDialogController(showResourcesController())
                .setFXML(getClass().getResource("/fxml/ShowResourcesView.fxml"))
                .setOwner(window)
                .setModality(Modality.APPLICATION_MODAL)
                .setStageStyle(StageStyle.DECORATED)
                .build();
    }

    @Bean
    public DataController dataController() {
        return new DataController();
   }

    public Parent dataPane() {
       return FXMLDialog.loadParent(dataController(), getClass().getResource("/fxml/DataView.fxml"));
    }
//
//    @Bean
//    public ShowPublicationsController showPublicationsController() {
//        return new ShowPublicationsController();
//    }
//
//    public FXMLDialog showPublicationsDialog() {
//        return new FXMLDialog.FXMLDialogBuilder()
//                .setDialogController(showPublicationsController())
//                .setFXML(getClass().getResource("/fxml/ShowPublicationsView.fxml"))
//                .setOwner(window)
//                .setModality(Modality.APPLICATION_MODAL)
//                .build();
//    }
//
    @Bean
    public ShowHtmlContentController showHtmlContentController() {
        return new ShowHtmlContentController();
    }

    public FXMLDialog showHtmlContentDialog() {
        return new FXMLDialog.FXMLDialogBuilder()
                .setDialogController(showHtmlContentController())
                .setFXML(getClass().getResource("/fxml/ShowHtmlContentView.fxml"))
                .setOwner(window)
                .setModality(Modality.APPLICATION_MODAL)
                .setStageStyle(StageStyle.DECORATED)
                .build();
    }



}
