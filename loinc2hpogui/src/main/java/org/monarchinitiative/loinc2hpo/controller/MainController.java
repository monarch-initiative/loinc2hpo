package org.monarchinitiative.loinc2hpo.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.javafx.runtime.SystemProperties;
import com.sun.javafx.stage.WindowCloseRequestHandler;
import com.sun.org.apache.bcel.internal.generic.POP;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableMapValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.jena.dboe.sys.Sys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.gui.HelpViewFactory;
import org.monarchinitiative.loinc2hpo.gui.Main;
import org.monarchinitiative.loinc2hpo.gui.PopUps;
import org.monarchinitiative.loinc2hpo.gui.SettingsViewFactory;
import org.monarchinitiative.loinc2hpo.io.Downloader;
import org.monarchinitiative.loinc2hpo.io.Loinc2HpoPlatform;
import org.monarchinitiative.loinc2hpo.io.LoincOfInterest;
import org.monarchinitiative.loinc2hpo.io.WriteToFile;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.UniversalLoinc2HPOAnnotation;
import org.monarchinitiative.loinc2hpo.model.Model;

import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.monarchinitiative.loinc2hpo.gui.PopUps.getStringFromUser;

@Singleton
public class MainController {
    private static final Logger logger = LogManager.getLogger();
    /** Download address for {@code hp.obo}. */
    private final static String HP_OBO_URL ="https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/hp.obo";
    //It appears Jena Sparql API does not like obo, so also download hp.owl
    private final static String HP_OWL_URL ="https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/hp.owl";
    private Model model=null;
    private final String LOINC_CATEGORY_folder = "LOINC CATEGORY";

    //use it to track whether configurations are complete
    //4 essential settings:
    //  path to Loinc Core Table
    //  path to HPO files
    //  path to auto-saved folder
    private ObservableMapValue<String, Boolean> configurationsState;
    private BooleanProperty configurationComplete = new SimpleBooleanProperty
            (false);

    //The following is the data that needs to be tracked
    private Map<LoincId, UniversalLoinc2HPOAnnotation> annotationMap_Copy;
    private Map<String, Set<LoincId>> loincCategories_Copy;

    @Inject private SetupTabController setupTabController;
    @Inject private AnnotateTabController annotateTabController;
    @Inject private Loinc2HpoAnnotationsTabController loinc2HpoAnnotationsTabController;
    @Inject private Loinc2HpoConversionTabController loinc2HPOConversionTabController;
    @Inject private CurrentAnnotationController currentAnnotationController;

    @FXML private BorderPane boardPane;

    @FXML private MenuBar loincmenubar;
    @FXML private MenuItem closeMenuItem;
    @FXML private MenuItem importAnnotationButton;
    @FXML private MenuItem newAnnotationFileButton;
    @FXML private Menu exportMenu;
    @FXML private MenuItem clearMenu;
    @FXML public Menu importLoincCategory;
    @FXML public Menu exportLoincCategory;

    @FXML private TabPane tabPane;
    @FXML private Tab annotateTabButton;
    @FXML private Tab Loinc2HPOAnnotationsTabButton;
    @FXML private Tab Loinc2HpoConversionTabButton;


    @FXML private void initialize() {
        Platform.runLater(() -> {
            annotateTabButton.setDisable(true);
            Loinc2HPOAnnotationsTabButton.setDisable(true);
            Loinc2HpoConversionTabButton.setDisable(true);
        });

        configurationComplete.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue.booleanValue()) {
                    Platform.runLater(() -> {
                        annotateTabButton.setDisable(false);
                        Loinc2HPOAnnotationsTabButton.setDisable(false);
                        Loinc2HpoConversionTabButton.setDisable(false);
                    });
                }
            }
        });

        this.model = new Model();
        //read in settings from file
        File settings = getPathToSettingsFileAndEnsurePathExists();
        model.setPathToSettingsFile(settings.getAbsolutePath());
        if (settings.exists()) {
            model.inputSettings(settings.getAbsolutePath());
            configurationComplete.setValue(isConfigurationCompleted());
        }
        //set auto-save file path if it is not specified
        //set it to the default path. user can still change it
        if (model.getPathToAutoSavedFolder() == null) {
            model.setPathToAutoSavedFolder(Loinc2HpoPlatform.getLOINC2HPODir()
                    + File.separator + "Data");
            File folder = new File(model.getPathToAutoSavedFolder());
            boolean created = false;
            if (!folder.exists()) {
                created = folder.mkdir();
            }
            if (created) {
                model.writeSettings();
            }
            configurationComplete.set(isConfigurationCompleted());
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

        if (configurationComplete.get()) {
            annotateTabController.defaultStartUp();
            defaultStartup();

            if (model.getPathToLastSession() != null) {
                openSession(model.getPathToLastSession());
            }
        }

        //@TODO: implement in future if necessary
        importLoincCategory.setVisible(false);
        exportLoincCategory.setVisible(false);
    }

    private boolean isConfigurationCompleted() {
        return model.getPathToLoincCoreTableFile() != null
                && model.getPathToHpoOwlFile() != null
                && model.getPathToHpoOboFile() != null
                && model.getPathToAutoSavedFolder() != null;
    }

    private void initializeAllDataSettings() {
        if (model.getPathToLoincCoreTableFile() == null
                || model.getPathToHpoOboFile() == null
                || model.getPathToHpoOwlFile() == null
                || model.getPathToAutoSavedFolder() == null) {
            Platform.runLater( () -> {
                annotateTabButton.setDisable(true);
                Loinc2HPOAnnotationsTabButton.setDisable(true);
                Loinc2HpoConversionTabButton.setDisable(true);
                return;
            });
        }
        annotateTabController.defaultStartUp();
        this.defaultStartup();
    }

    private void defaultStartup() {
        if (model.getPathToLastSession() != null) {
            openSession(model.getPathToLastSession());
        }
    }


    public void saveBeforeExit() {
        logger.trace("SaveBeforeExit() is called");
        if (isSessionDataChanged()) {
            handleSaveSession(null);
        } else {
            logger.trace("data not changed. exit safely");
        }
    }

    /**
     * @TODO: complete this feature in future when high efficiency is needed
     * Method: create a copy of session data when it starts; before exiting, compare data to see whether it changed
     */
    private void initTrackedData() {
        annotationMap_Copy = new LinkedHashMap<>(model.getLoincAnnotationMap());
        loincCategories_Copy = new LinkedHashMap<>(model.getUserCreatedLoincLists());
    }


    /**
     * The function determines whether the data in annotations map and loincCategories has changed
     * @return
     */
    public boolean isSessionDataChanged() {
        //Lazy implementation
        //whenever createAnnotation, saveAnnotation, group/ungroup loinc or create loinc list are called, it return true
        return model.isSessionChanged();
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
        configurationComplete.set(isConfigurationCompleted());

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
            configurationComplete.set(isConfigurationCompleted());
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
        configurationComplete.set(isConfigurationCompleted());
        e.consume();
    }

    @FXML public void close(ActionEvent e) {

        e.consume(); //important to consume it first; otherwise,
        //window will always close
        if (isSessionDataChanged()) {

            String[] choices = new String[] {"Yes", "No"};
            Optional<String> choice = PopUps.getToggleChoiceFromUser(choices,
                    "Session has been changed. Save changes? ", "Exit " +
                            "Confirmation");


            if (choice.isPresent() && choice.get().equals("Yes")) {
                saveBeforeExit();
                Platform.exit();
                System.exit(0);
                //window.close();
            } else if (choice.isPresent() && choice.get().equals("No")) {
                Platform.exit();
                System.exit(0);
                //window.close();
            } else {
                //hang on. No action required
            }
        } else {
            Platform.exit();
            System.exit(0);
            //window.close();
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

    private void createNewSession() {
        logger.trace(autogenerateFileName());
        String sessionFolderName = model.getPathToAutoSavedFolder() + File.separator + autogenerateFileName();

        //ask user if default file is okay
        String[] choices = new String[] {"Yes", "No"};
        Optional<String> choice = PopUps.getToggleChoiceFromUser(choices,
                "Default path: " + sessionFolderName, "Set Path to New Session");
        if (choice.isPresent() && choice.get().equals("No")) { //manually create a directory for autosaved data
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose a directory to new session data");
            directoryChooser.setInitialDirectory(new File(model.getPathToAutoSavedFolder()));
            File f  = directoryChooser.showDialog(null);
            if (f != null) {
                sessionFolderName = f.getAbsolutePath();
            } else {
                return;
            }
        }
        File sessionFolder = new File(sessionFolderName);
        if (!sessionFolder.exists()) {
            sessionFolder.mkdir();
        }
        //update last session infor in model
        model.setPathToLastSession(sessionFolder.getAbsolutePath());
        model.writeSettings();
    }

    @FXML
    /**
     * Create a new directory with the current data and time as its name
     */
    private void handleNewSession(ActionEvent e) {

        if (model.isSessionChanged()) {
            boolean toSave = PopUps.getBooleanFromUser("Session has been changed. Save changes? " +
                            "If you choose No, all current changes will be lost.",
                    "Save session data", "Confirmation");
            if (toSave) {
                handleSaveSession(null);
            }
        }
        model.getLoincAnnotationMap().clear();
        model.getUserCreatedLoincLists().values().forEach(p -> p.clear());
        createNewSession();
        model.setSessionChanged(false);
        e.consume();
    }

    @FXML
    private void handleOpenSession(ActionEvent e){
        e.consume();
        String pathToOpen = model.getPathToAutoSavedFolder();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose session folder");
        directoryChooser.setInitialDirectory(pathToOpen != null ?
                new File(pathToOpen) : new File(System.getProperty("user.home")));
        File f  = directoryChooser.showDialog(null);
        if (f != null) {
            pathToOpen = f.getAbsolutePath();
        } else {
            return;
        }

        openSession(pathToOpen);
        model.setPathToLastSession(pathToOpen);
        model.writeSettings();
    }

    protected void openSession(String pathToOpen) {

        //there should be one default file, "annotations.tsv",
        //one default folder "LOINC category", which should have two files "require_new_HPO_terms.txt", "unable_to_annotate.txt" by default (and possibility others)
        String annotationsFilePath = pathToOpen + File.separator + "annotations.tsv";
        if (new File(annotationsFilePath).exists()) {
            loinc2HpoAnnotationsTabController.importLoincAnnotation(annotationsFilePath);
        }

        File loinc_category_folder = new File(pathToOpen + File.separator + LOINC_CATEGORY_folder);
        if (!loinc_category_folder.exists() || !loinc_category_folder.isDirectory()) {
            return;
        }

        File[] files = loinc_category_folder.listFiles();
        if (files == null) {
            return;
        } else {
            for (File file : files) {
                try {
                    LoincOfInterest loincCategory = new LoincOfInterest(file.getAbsolutePath());
                    Set<String> loincIdStrings = loincCategory.getLoincOfInterest();
                    String categoryName = file.getName();
                    if (categoryName.endsWith(".txt")) {
                        categoryName = categoryName.substring(0, file.getName().length() - 4);
                    }

                    Set<LoincId> loincIds = loincIdStrings.stream().map(p -> {
                        try {
                            return new LoincId(p);
                        } catch (MalformedLoincCodeException e1) {
                            logger.error("This should never happen since the loincids are automatically saved");
                        }
                        return null;
                    }).collect(Collectors.toSet());
                    if (!annotateTabController.userCreatedLoincLists.contains
                            (categoryName)) {
                        annotateTabController.userCreatedLoincLists.add
                                (categoryName);
                    }
                    model.addUserCreatedLoincList(categoryName, loincIds);
                } catch (FileNotFoundException e1) {
                    logger.error("This should never happen since the folder is autogenerated.");
                }
            }
            annotateTabController.changeColorLoincTableView();
        }

    }

    @FXML
    private void handleSaveSession(ActionEvent e) {

        logger.trace("user wants to save a session");
        //Create a session if it is saved for the first time
        if (model.getPathToLastSession() == null) {
            createNewSession();
        }
        //save annotations to "annotations"
        String pathToAnnotations = model.getPathToLastSession() + File.separator + "annotations.tsv";
        try {
            WriteToFile.toTSV(pathToAnnotations, model.getLoincAnnotationMap());
        } catch (IOException e1) {
            PopUps.showWarningDialog("Error message",
                    "Failure to save annotations data",
                    "An error occurred. Try again!");
        }
        //@TODO: save all Loinc categories to a folder
        String pathToLoincCategory = model.getPathToLastSession() + File.separator + LOINC_CATEGORY_folder;
        if (!new File(pathToLoincCategory).exists()) {
            new File(pathToLoincCategory).mkdir();
        }
        model.getUserCreatedLoincLists().entrySet()
                .forEach(p -> {
                    String path = pathToLoincCategory + File.separator + p.getKey() + ".txt";
                    Set<LoincId> loincIds = model.getUserCreatedLoincLists().get(p.getKey());
                    StringBuilder builder = new StringBuilder();
                    loincIds.forEach(l -> {
                        builder.append (l);
                        builder.append("\n");
                    });
                    WriteToFile.writeToFile(builder.toString().trim(), path);
        });

        if (e != null) {
            e.consume();
        }

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

