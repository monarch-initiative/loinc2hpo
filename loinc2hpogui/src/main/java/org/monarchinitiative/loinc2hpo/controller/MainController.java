package org.monarchinitiative.loinc2hpo.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.org.apache.bcel.internal.generic.POP;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.gui.HelpViewFactory;
import org.monarchinitiative.loinc2hpo.gui.PopUps;
import org.monarchinitiative.loinc2hpo.gui.SettingsViewFactory;
import org.monarchinitiative.loinc2hpo.io.Downloader;
import org.monarchinitiative.loinc2hpo.io.Loinc2HpoPlatform;
import org.monarchinitiative.loinc2hpo.loinc.UniversalLoinc2HPOAnnotation;
import org.monarchinitiative.loinc2hpo.model.Model;

import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.monarchinitiative.loinc2hpo.gui.PopUps.getStringFromUser;

@Singleton
public class MainController {
    private static final Logger logger = LogManager.getLogger();
    /** Download address for {@code hp.obo}. */
    private final static String HP_OBO_URL ="https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/hp.obo";
    //It appears Jena Sparql API does not like obo, so also download hp.owl
    private final static String HP_OWL_URL ="https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/hp.owl";
    private Model model=null;



    @Inject private SetupTabController setupTabController;
    @Inject private AnnotateTabController annotateTabController;
    @Inject private Loinc2HpoAnnotationsTabController loinc2HpoAnnotationsTabController;
    @Inject private Loinc2HpoConversionTabController loinc2HPOConversionTabController;
    @Inject private CurrentAnnotationController currentAnnotationController;

    @FXML private MenuBar loincmenubar;
    @FXML private MenuItem closeMenuItem;
    @FXML private MenuItem importAnnotationButton;
    @FXML private MenuItem newAnnotationFileButton;
    @FXML private Menu exportMenu;
    @FXML private MenuItem clearMenu;

    @FXML private TabPane tabPane;
    @FXML private Tab annotateTabButton;
    @FXML private Tab Loinc2HPOAnnotationsTabButton;
    @FXML private Tab Loinc2HpoConversionTabButton;


    @FXML private void initialize() {
        this.model = new Model();
        File settings = getPathToSettingsFileAndEnsurePathExists();
        model.setPathToSettingsFile(settings.getAbsolutePath());
        if (settings.exists()) {
            model.inputSettings(settings.getAbsolutePath());
        }
        if (setupTabController==null) {
            logger.error("setupTabController is null");
            return;
        }
        setupTabController.setModel(model);
        if (annotateTabController==null) {
            logger.error("annotate Controller is null");
            return;
        }
        annotateTabController.setModel(model);
        if (loinc2HpoAnnotationsTabController==null) {
            logger.error("loinc2HpoAnnotationsTabController is null");
            return;
        }
        if (loinc2HPOConversionTabController == null) {
            logger.error("loinc2HPOConversionTabController is null");
            return;
        }
        currentAnnotationController.setModel(model);
        if (model == null) {
            logger.error("main controller model is null");
            return;
        }



        loinc2HpoAnnotationsTabController.setModel(model);
        loinc2HPOConversionTabController.setModel(model);
        if (Loinc2HpoPlatform.isMacintosh()) {
            loincmenubar.useSystemMenuBarProperty().set(true);
        }


        //control how menu items should be shown
        importAnnotationButton.setDisable(true);
        exportMenu.setDisable(true);
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {

                if(newValue.equals(Loinc2HPOAnnotationsTabButton)) {
                    importAnnotationButton.setDisable(false);
                    exportMenu.setDisable(false);
                    clearMenu.setDisable(false);
                } else {
                    importAnnotationButton.setDisable(true);
                    exportMenu.setDisable(true);
                    clearMenu.setDisable(true);
                }


            }
        });
    }

    @FXML private void setPathToAutoSavedData(ActionEvent e) {
        e.consume();
        String path2DEFAULTDIRECTORY = Loinc2HpoPlatform.getLOINC2HPODir() + File.separator + "Data";
        File DEFAULTDIRECTORY = new File(path2DEFAULTDIRECTORY);

        String[] choices = new String[] {"Yes", "No"};
        Optional<String> choice = PopUps.getToggleChoiceFromUser(choices,
                "Default path: " + path2DEFAULTDIRECTORY, "Set Path to Auto-saved Data");
        if (choice.isPresent() && choice.get().equals("No")) { //manually create a directory for autosaved data
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose a directory to save session data");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            DEFAULTDIRECTORY  = directoryChooser.showDialog(null);
        }

        if (DEFAULTDIRECTORY == null) {
            return;
        }

        if (!DEFAULTDIRECTORY.exists()) {
            DEFAULTDIRECTORY.mkdir();
        }
        //Warn user if the above settings failed
        if (!DEFAULTDIRECTORY.exists()){
            PopUps.showWarningDialog("Error", "Failure to set the default folder",
                    "Try again, or manually set one");
            return;
        }
        model.setPathToAutoSavedFolder(DEFAULTDIRECTORY.getAbsolutePath());
        model.writeSettings();

    }

    @FXML public void downloadHPO(ActionEvent e) {
        String dirpath= Loinc2HpoPlatform.getLOINC2HPODir().getAbsolutePath();
        File f = new File(dirpath);
        if (f==null || ! (f.exists() && f.isDirectory())) {
            logger.trace("Cannot download hp.obo, because directory not existing at " + f.getAbsolutePath());
            return;
        }
        String BASENAME="hp.obo";
        String BASENAME_OWL = "hp.owl";

        ProgressIndicator pb = new ProgressIndicator();
        javafx.scene.control.Label label=new javafx.scene.control.Label("downloading hp.obo/.owl...");
        FlowPane root = new FlowPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);
        root.getChildren().addAll(label,pb);
        Scene scene = new Scene(root, 400, 100);
        Stage window = new Stage();
        window.setTitle("HPO download");
        window.setScene(scene);

        Task hpodownload = new Downloader(dirpath, HP_OBO_URL,BASENAME,pb);
        new Thread(hpodownload).start();
        hpodownload = new Downloader(dirpath, HP_OWL_URL, BASENAME_OWL, pb);
        new Thread(hpodownload).start();
        window.show();
        hpodownload.setOnSucceeded(event -> {
            window.close();
            logger.trace(String.format("Successfully downloaded hpo to %s",dirpath));
            String fullpath=String.format("%s%shp.obo",dirpath,File.separator);
            String fullpath_owl = String.format("%s%shp.owl", dirpath, File
                    .separator);
            model.setPathToHpOboFile(fullpath);
            model.setPathToHpOwlFile(fullpath_owl);
            model.writeSettings();
        });
        hpodownload.setOnFailed(event -> {
            window.close();
            logger.error("Unable to download HPO obo file");
        });
       // Thread thread = new Thread(hpodownload);
        //thread.start();

        e.consume();
    }

    @FXML public  void setPathToLoincCoreTableFile(ActionEvent e) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose LOINC Core Table file");
        File f = chooser.showOpenDialog(null);
        if (f != null) {
            String path = f.getAbsolutePath();
            model.setPathToLoincCoreTableFile(path);
            logger.trace(String.format("Setting path to LOINC Core Table file to %s",path));
        } else {
            logger.error("Unable to obtain path to LOINC Core Table file");
        }
        model.writeSettings();
        e.consume();
    }

    @FXML public void close(ActionEvent e) {
        //Should give user a warning if there is new annotation data
        //TODO: implement warning
        if (true) {
            boolean choice = PopUps.getBooleanFromUser("Exit without saving " +
                    "annotation data? You new annotation will be lost if you " +
                            "choose cancel",
                    "Data Unsaved", "Data Unsaved");
            if (!choice) {
                return;
            } else {
                Platform.exit();
                System.exit(0);
            }
        }
    }

    /**
     * This function will create the .loinc2hpo directory in the user's home directory if it does not yet exist.
     * Then it will return the path of the settings file.
     * @return
     */
    private File getPathToSettingsFileAndEnsurePathExists() {
        File loinc2HpoUserDir = Loinc2HpoPlatform.getLOINC2HPODir();
        if (!loinc2HpoUserDir.exists()) {
            File fck = new File(loinc2HpoUserDir.getAbsolutePath());
            if (!fck.mkdir()) { // make sure config directory is created, exit if not
                logger.fatal("Unable to create LOINC2HPO config directory.\n"
                        + "Even though this is a serious problem I'm exiting gracefully. Bye.");
                System.exit(1);
            }
        }
        String defaultSettingsPath = Loinc2HpoPlatform.getPathToSettingsFile();
        File settingsFile=new File(defaultSettingsPath);
       return settingsFile;
    }

    @FXML private void setBiocuratorID(ActionEvent e) {
        String prompText = (model.getBiocuratorID() == null || model.getBiocuratorID().isEmpty())
                ? "e.g., MGM:rrabbit" : model.getBiocuratorID();
        String bcid=getStringFromUser("Biocurator ID", prompText, "Enter biocurator ID");
        if (bcid!=null && bcid.indexOf(":")>0) {
            model.setBiocuratorID(bcid);
            model.writeSettings();
        } else {
            logger.error(String.format("Invalid biocurator ID; must be of the form MGM:rrabbit; you tried: \"%s\"",
                    bcid!=null?bcid:""));
        }
        e.consume();
    }

    /** Show the about message */
    @FXML private void aboutWindow(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("LOINC2HPO Biocuration tool");
        alert.setHeaderText("Loinc2Hpo");
        String s = "A tool for biocurating HPO mappings for LOINC laboratory codes.";
        alert.setContentText(s);
        alert.showAndWait();
        e.consume();
    }

    /** Open a help dialog */
    @FXML private void openHelpDialog() {
        HelpViewFactory.openHelpDialog();
    }

    /** Open a help dialog */
    @FXML private void openSettingsDialog() {
        SettingsViewFactory.openSettingsDialog(this.model);
    }


    private String autogenerateFileName() {

        String filename = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        filename = format.format(date);
        //Calendar cal = Calendar.getInstance();
        //filename = format.format(cal);

        return filename;
    }

    @FXML
    private void handleNewSession(ActionEvent e) {

        logger.trace(autogenerateFileName());
        String sessionFileName = autogenerateFileName();
        File sessionFolder = new File(model.getPathToAutoSavedFolder() + File.separator + sessionFileName);
        sessionFolder.mkdir();

        e.consume();
    }

    @FXML
    private void handleOpenSession(ActionEvent e){
        e.consume();
    }

    @FXML
    private void handleSaveSession(ActionEvent e) {

        e.consume();
    }




    //TODO: change this to handleSaveToNewFile

    /**
     * This method will save the current data in annotationTableView to the
     * file of import. (Set file path when trying importing annotation data).
     * If file path is not specified (no importing), create a new file
     * @param e
     */
    @FXML private void handleSave(ActionEvent e) {

        e.consume();
        logger.info("usr wants to save file");
        //loinc2HpoAnnotationsTabController.saveLoincAnnotation();
        loinc2HpoAnnotationsTabController.newSave();

    }


    /**
     * This method will save the current data in annotationTableView to a
     * specified file; overwrite if the specified file already exist
     * @param e
     */
    @FXML private void handleSaveAsButton(ActionEvent e){
        e.consume();
        logger.info("user wants to save to a new file");
        //loinc2HpoAnnotationsTabController.saveAsLoincAnnotation();
        loinc2HpoAnnotationsTabController.newSaveAs();
    }

    /**
     * This method will append the current data in annotationTableView to a
     * specified file; fail if the specified file does not exist
     * @param e
     */
    @FXML private void handleAppendToButton(ActionEvent e){
        e.consume();
        logger.info("usr wants to append to a file");
        //loinc2HpoAnnotationsTabController.appendLoincAnnotation()
        loinc2HpoAnnotationsTabController.newAppend();


    }

    @FXML private void handleImportAnnotationFile(ActionEvent event) {

        loinc2HpoAnnotationsTabController.importLoincAnnotation();
        logger.info("usr wants to import an annotation file");
        event.consume();
    }

    @FXML private void handleExportAsTSV(ActionEvent event) {
        logger.info("usr wants to export annotations to a TSV file");
        loinc2HpoAnnotationsTabController.exportAnnotationsAsTSV();
        event.consume();
    }

    @FXML private void clear(ActionEvent event) {
        logger.trace("user wants to clear the contents");
        event.consume();
        boolean choice = PopUps.getBooleanFromUser("Are you sure you want to clear all annotations?", "Confirmation",
                "Clear All Annotations");
        if (!choice) {
            return;
        }

        loinc2HpoAnnotationsTabController.clear();

    }

    public enum TabPaneTabs{
        AnnotateTabe,
        Loinc2HpoAnnotationsTab,
        Loinc2HpoConversionTab
    }

    public void switchTab(TabPaneTabs tab) {
        switch (tab) {
            case AnnotateTabe:
                tabPane.getSelectionModel().select(annotateTabButton);
            case Loinc2HpoAnnotationsTab:
                tabPane.getSelectionModel().select(Loinc2HPOAnnotationsTabButton);
            case Loinc2HpoConversionTab:
                tabPane.getSelectionModel().select(Loinc2HpoConversionTabButton);
            default:
                    tabPane.getSelectionModel().select(annotateTabButton);
        }

    }




}

