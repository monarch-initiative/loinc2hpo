package org.monarchinitiative.loinc2hpo.controller;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.wagon.CommandExecutionException;
import org.monarchinitiative.loinc2hpo.Constants;
import org.monarchinitiative.loinc2hpo.ResourceCollection;
import org.monarchinitiative.loinc2hpo.command.VersionCommand;
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.gui.*;
import org.monarchinitiative.loinc2hpo.io.*;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.model.AppResources;
import org.monarchinitiative.loinc2hpo.model.AppTempData;
import org.monarchinitiative.loinc2hpo.model.Settings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.monarchinitiative.loinc2hpo.gui.PopUps.getStringFromUser;

/**
 * The class provides an entry to the app. Its main responsibility is to manage settings and loading resources.
 */

@Singleton
public class MainController {
    private static final Logger logger = LogManager.getLogger();
    /** Download address for {@code hp.obo}. */
    private final static String HP_OBO_URL ="https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/hp.obo";
    //It appears Jena Sparql API does not like obo, so also download hp.owl
    private final static String HP_OWL_URL ="https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/hp.owl";

    private final String LOINC_CATEGORY_folder = "LOINC CATEGORY";

    //use it to track whether configurations are complete
    //4 essential settings:
    //  path to Loinc Core Table
    //  path to HPO files
    //  path to auto-saved folder
    private BooleanProperty configurationComplete = new SimpleBooleanProperty
            (false);

    @Inject private AnnotateTabController annotateTabController;
    @Inject private Loinc2HpoAnnotationsTabController loinc2HpoAnnotationsTabController;
    @Inject private Loinc2HpoConversionTabController loinc2HPOConversionTabController;
    @Inject private Injector injector;
    @Inject private AppTempData appTempData;
    //manages all resources

    @Inject private Settings settings;
    private AppResources appResources;


    @FXML private BorderPane boardPane;

    @FXML private MenuBar loincmenubar;
    @FXML private MenuItem closeMenuItem;
    @FXML private MenuItem importAnnotationButton;
    @FXML private MenuItem newAnnotationFileButton;
    @FXML private Menu exportMenu;
    @FXML private MenuItem clearMenu;
    @FXML public Menu importLoincCategory;
    @FXML public Menu exportLoincCategory;
    @FXML private MenuItem saveAnnotationsMenuItem;
    @FXML private MenuItem saveAnnotationsAsMenuItem;
    @FXML private MenuItem appendAnnotationsToMenuItem;

    @FXML private TabPane tabPane;
    @FXML private Tab annotateTabButton;
    @FXML private Tab Loinc2HPOAnnotationsTabButton;
    @FXML private Tab Loinc2HpoConversionTabButton;

    @FXML private MenuItem updateHpoButton;

    @FXML private void initialize() {

        annotateTabButton.setDisable(true);
        Loinc2HPOAnnotationsTabButton.setDisable(true);
        Loinc2HpoConversionTabButton.setDisable(true);

        //once configure is done, enable all tabs
        configurationComplete.addListener((observable, oldValue, newValue) -> {
            logger.info(String.format("configurationComplete state change. old: %s; new; %s", oldValue, newValue));
            if (observable != null && newValue) {
                logger.info("configuration is completed");
                annotateTabButton.setDisable(false);
                Loinc2HPOAnnotationsTabButton.setDisable(false);
                Loinc2HpoConversionTabButton.setDisable(false);

                appResources = injector.getInstance(AppResources.class);
                //@TODO: figure out how to control init after construction
                appResources.init();

                annotateTabController.setAppTempData(appTempData);
                loinc2HpoAnnotationsTabController.setAppTempData(appTempData);
                loinc2HPOConversionTabController.setAppTempData(appTempData);

                annotateTabController.defaultStartUp();
                openSession(settings.getAnnotationFolder());

            }
        });

        //read in settings from file
        File settingsFile = getPathToSettingsFileAndEnsurePathExists();
//        appTempData.setPathToSettingsFile(settingsFile.getAbsolutePath());
        try {
            Settings.loadSettings(settings, settingsFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        configurationComplete.set(settings.isCompleteProperty().getValue());
        configurationComplete.bind(settings.isCompleteProperty());

        if (Loinc2HpoPlatform.isMacintosh()) {
            loincmenubar.useSystemMenuBarProperty().set(true);
        }


        //control how menu items should be shown
        importAnnotationButton.setDisable(true);
        exportMenu.setDisable(true);
        tabPane.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {

            if(newValue.equals(Loinc2HPOAnnotationsTabButton)) {
                importAnnotationButton.setDisable(false);
                exportMenu.setDisable(false);
                clearMenu.setDisable(false);
            } else {
                importAnnotationButton.setDisable(true);
                exportMenu.setDisable(true);
                clearMenu.setDisable(true);
            }


        });

        //@TODO: to decide whether to remove the following menuitems
        importLoincCategory.setVisible(false);
        exportLoincCategory.setVisible(false);
        saveAnnotationsMenuItem.setVisible(false);
        saveAnnotationsAsMenuItem.setVisible(false);
        appendAnnotationsToMenuItem.setVisible(false);
        clearMenu.setVisible(false);
        updateHpoButton.setVisible(false);
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
     * The function determines whether the data in annotations map and loincCategories has changed
     * @return
     */
    public boolean isSessionDataChanged() {
        //Lazy implementation
        //whenever createAnnotation, saveAnnotation, group/ungroup loinc or create loinc list are called, it return true
        return appTempData.isSessionChanged();
    }

    @FXML private void setPathToAutoSavedData(ActionEvent e) {
        e.consume();
        File annotationDIRECTORY;
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose a directory to save annotation data");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        annotationDIRECTORY  = directoryChooser.showDialog(null);

        if (annotationDIRECTORY == null) {
            return;
        }

        settings.setAnnotationFolder(annotationDIRECTORY.getAbsolutePath());
        Settings.writeSettings(settings, Loinc2HpoPlatform.getPathToSettingsFile());
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
            settings.setHpoOboPath(fullpath);
            settings.setHpoOwlPath(fullpath_owl);
            Settings.writeSettings(settings, Loinc2HpoPlatform.getPathToSettingsFile());
        });
        hpodownload.setOnFailed(event -> {
            window.close();
            logger.error("Unable to download HPO obo file");
        });

        e.consume();
    }

    @FXML private void changeHpoOwlTo(ActionEvent e) {

        e.consume();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose HPO OWL file");
        File owl = chooser.showOpenDialog(null);
        if (owl != null) {
            settings.setHpoOwlPath(owl.getAbsolutePath());
            Settings.writeSettings(settings, Loinc2HpoPlatform.getPathToSettingsFile());
        }

    }

    @FXML private void changeHpoOboTo(ActionEvent e) {

        e.consume();
        logger.trace("changeHpoOboTo clicked");
        e.consume();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose HPO OBO file");
        File obo = chooser.showOpenDialog(null);
        if (obo != null) {
            settings.setHpoOboPath(obo.getAbsolutePath());
            Settings.writeSettings(settings, Loinc2HpoPlatform.getPathToSettingsFile());
        }

    }

    @FXML public  void setPathToLoincCoreTableFile(ActionEvent e) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose LOINC Core Table file");
        File f = chooser.showOpenDialog(null);
        if (f != null) {
            String path = f.getAbsolutePath();
            settings.setLoincCoreTablePath(path);
            Settings.writeSettings(settings, Loinc2HpoPlatform.getPathToSettingsFile());
            logger.trace(String.format("Setting path to LOINC Core Table file to %s",path));
        } else {
            logger.error("Unable to obtain path to LOINC Core Table file");
        }
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
        String current = settings.getBiocuratorID();
        String prompText = (current == null || current.isEmpty())
                ? "e.g., MGM:rrabbit" : current;
        String bcid=getStringFromUser("Biocurator ID", prompText, "Enter biocurator ID");
        if (bcid!=null && bcid.indexOf(":")>0) {
            settings.setBiocuratorID(bcid);
            Settings.writeSettings(settings, Loinc2HpoPlatform.getPathToSettingsFile());
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
        String s = "A tool for biocurating HPO mappings for LOINC laboratory codes.\n\nversion: " + VersionCommand.getVersion();
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
        SettingsViewFactory.openSettingsDialog(settings);
    }


    private String autogenerateFileName() {

        String filename = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        Date date = new Date();
        filename = format.format(date);
        //Calendar cal = Calendar.getInstance();
        //filename = format.format(cal);

        return "Loinc2HpoAnnotation_" + filename;
    }

    /**
     * Create new annotation folder, and populate with subdirectories
     */
    private void createNewSession() {
        logger.trace(autogenerateFileName());
        String sessionFolderName = System.getProperty("user.home") + File.separator + autogenerateFileName();

        //ask user if default file is okay
        String[] choices = new String[] {"Yes", "No"};
        Optional<String> choice = PopUps.getToggleChoiceFromUser(choices,
                "Default path: " + sessionFolderName, "Set Path to New Session");
        if (choice.isPresent() && choice.get().equals("No")) { //manually create a directory for autosaved data
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose a directory to new session data");
            File f  = directoryChooser.showDialog(null);
            if (f != null) {
                sessionFolderName = f.getAbsolutePath();
            } else {
                return;
            }
        }
        if (!Files.exists(Paths.get(sessionFolderName))) {
            try {
                Files.createDirectory(Paths.get(sessionFolderName));
            } catch (IOException e) {
                PopUps.showWarningDialog("Warning", "Failure to create folders", "No Annotation folder is created. Try a different folder.");
                return;
            }
        }

        try {
            //create "Data" subdirectory
            Files.createDirectory(Paths.get(sessionFolderName + File.separator + "Data"));
            //create "Data/LOINC CATEGORY
            Files.createDirectory(Paths.get(sessionFolderName + File.separator + "Data" + File.separator + "LOINC CATEGORY"));
            //Create "Data/LoincPanel
            Files.createDirectory(Paths.get(sessionFolderName + File.separator + "Data" + File.separator + "LoincPanel"));
            //Create "TSVSingleFile"
            Files.createDirectory(Paths.get(sessionFolderName + File.separator + "Data" + File.separator + "TSVSingleFile"));
            //Create "Task"
            Files.createDirectory(Paths.get(sessionFolderName + File.separator + "Task"));
        } catch (IOException e) {
            PopUps.showWarningDialog("Warning", "Failure to create subdirectories for annotation data", "No annotation folder is created");
            return;
        }

        //update last session infor in appTempData
        settings.setAnnotationFolder(sessionFolderName);//.setPathToAnnotationFolder(sessionFolderName);
        Settings.writeSettings(settings, Loinc2HpoPlatform.getPathToSettingsFile());
    }

    @FXML
    /**
     * Create a new directory with the current data and time as its name
     */
    private void handleNewSession(ActionEvent e) {

        if (appTempData.isSessionChanged()) {
            boolean toSave = PopUps.getBooleanFromUser("Session has been changed. Save changes? " +
                            "If you choose No, all current changes will be lost.",
                    "Save session data", "Confirmation");
            if (toSave) {
                handleSaveSession(null);
            }
        }
        appResources.getLoincAnnotationMap().clear();
        appTempData.getUserCreatedLoincLists().values().forEach(p -> p.clear());
        createNewSession();
        appTempData.setSessionChanged(false);
        e.consume();
    }

    @FXML
    private void handleOpenSession(ActionEvent e){
        e.consume();

        if (appResources.getLoincEntryMap() == null) {
            PopUps.showWarningDialog("NO LOINC", "LOINC table Not Initialized",
                    "click init Loinc button to import Loinc table");
            return;
        }
        if (appResources.getTermidTermMap() == null) {
            PopUps.showWarningDialog("NO HPO", "HPO Not Initialized",
                    "click init HPO button to import HPO");
            return;
        }

        String pathToOpen;
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose session folder");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File f  = directoryChooser.showDialog(null);
        if (f != null) {
            pathToOpen = f.getAbsolutePath();
        } else {
            return;
        }

        openSession(pathToOpen);
        settings.setAnnotationFolder(pathToOpen);
        Settings.writeSettings(settings, Loinc2HpoPlatform.getPathToSettingsFile());
    }

    /**
     * Open the annotation
     * @param pathToOpen
     */
    protected void openSession(String pathToOpen) {

        String dataDir = pathToOpen + File.separator + "Data";

        //import annotations
        loinc2HpoAnnotationsTabController.importLoincAnnotation(dataDir);

        //import the LOINC categories
        File loinc_category_folder = new File(dataDir + File.separator + LOINC_CATEGORY_folder);
        if (!loinc_category_folder.exists() || !loinc_category_folder.isDirectory()) {
            return;
        }

        File[] files = loinc_category_folder.listFiles();
        if (files == null) {
            return;
        } else {
            for (File file : files) {
                if (!file.getName().endsWith(".txt")) {
                    continue;
                }
                if (file.getName().endsWith("DS_Store.txt")){
                    continue;
                }
                try {
                    LoincOfInterest loincCategory = new LoincOfInterest(file.getAbsolutePath());
                    Set<String> loincIdStrings = loincCategory.getLoincOfInterest();
                    String categoryName = file.getName().substring(0, file.getName().length() - 4);;

                    Set<LoincId> loincIds = loincIdStrings.stream().map(p -> {
                        try {
                            return new LoincId(p);
                        } catch (MalformedLoincCodeException e1) {
                            logger.error("malformed LOINC id: " + p);
                            logger.error("in:" + file.getAbsolutePath());
                        }
                        return null;
                    }).collect(Collectors.toSet());
                    if (!annotateTabController.userCreatedLoincLists.contains
                            (categoryName)) {
                        annotateTabController.userCreatedLoincLists.add
                                (categoryName);
                    }
                    appTempData.addUserCreatedLoincList(categoryName, loincIds);
                } catch (FileNotFoundException e1) {
                    logger.error("file not found:" + file.getAbsolutePath());
                }
            }
            annotateTabController.changeColorLoincTableView();
        }

    }


    @FXML
    private void handleSaveSession(ActionEvent e) {

        logger.trace("user wants to save a session");
        //Create a session if it is saved for the first time
        if (settings.getAnnotationFolder() == null) {
            createNewSession();
        }

        String dataDir = settings.getAnnotationFolder() + File.separator + "Data";

        // The following codes demonstrates how to save the annotations in TSVSeparatedFiles format
        //create folder is not present
        Path folderTSVSeparated = Paths.get(dataDir + File.separator + Constants.TSVSeparateFilesFolder);
        if (!Files.exists(folderTSVSeparated)) {
            try {
                Files.createDirectory(folderTSVSeparated);
            } catch (IOException e1) {

                PopUps.showWarningDialog("Error message",
                        "Failure to create folder",
                        String.format("An error occurred when trying to make a directory at %s. Try again!", folderTSVSeparated));
                return;
            }

        }

        Path folderTSVSingle = Paths.get(dataDir + File.separator + Constants.TSVSingleFileFolder);
        if (!Files.exists(folderTSVSingle)) {
            try {
                Files.createDirectory(folderTSVSingle);
            } catch (IOException e1) {
                PopUps.showWarningDialog("Error message",
                        "Failure to create folder" ,
                        String.format("An error occurred when trying to make a directory at %s. Try again!", folderTSVSingle));
                return;
            }
        }

        String annotationTSVSingleFile = folderTSVSingle.toString() + File.separator + Constants.TSVSingleFileName;
        try {
            LoincAnnotationSerializationFactory.setHpoTermMap(appResources.getTermidTermMap());
            LoincAnnotationSerializationFactory.serializeToFile(appResources.getLoincAnnotationMap(), LoincAnnotationSerializationFactory.SerializationFormat.TSVSingleFile, annotationTSVSingleFile);
        } catch (IOException e1) {
            PopUps.showWarningDialog("Error message",
                    "Failure to Save Session Data" ,
                    String.format("An error occurred when trying to save data to %s. Try again!", annotationTSVSingleFile));
            return;
        }

        String pathToLoincCategory = dataDir + File.separator + LOINC_CATEGORY_folder;
        if (!new File(pathToLoincCategory).exists()) {
            new File(pathToLoincCategory).mkdir();
        }
        appTempData.getUserCreatedLoincLists().entrySet()
                .forEach(p -> {
                    String path = pathToLoincCategory + File.separator + p.getKey() + ".txt";
                    Set<LoincId> loincIds = appTempData.getUserCreatedLoincLists().get(p.getKey());
                    StringBuilder builder = new StringBuilder();
                    loincIds.forEach(l -> {
                        builder.append (l);
                        builder.append("\n");
                    });
                    WriteToFile.writeToFile(builder.toString().trim(), path);
                });

        //reset the session change tracker
        appTempData.setSessionChanged(false);

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
        loinc2HpoAnnotationsTabController.saveAnnotations();

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
        loinc2HpoAnnotationsTabController.saveAnnotationsAs();
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

    @FXML
    private void updateHpo(ActionEvent e) {
        e.consume();
        logger.trace("user wants to update HPO");
        try {
            updateHpo();
        } catch (NullPointerException e1) {
            PopUps.showWarningDialog("Warning", "Failure to update Human Phenotype Ontology", "You may not have set the path to HPO repository");
        } catch (Exception e1) {
            PopUps.showWarningDialog("Warning", "Failure to update Human Phenotype Ontology", "Do it manually");
        }

    }

    @FXML
    private void start(ActionEvent e){
        e.consume();
        logger.trace("user starts a session");
        try {
            sendLockingEmail();
            PopUps.showWarningDialog("Success", "Locking Message Sent", "Next: pull latest annotation data");
        } catch (Exception e1){
            PopUps.showWarningDialog("Warning", "Failure to send locking message", "Do it manually");
        }
    }

    @FXML
    private void checkoutData(ActionEvent e) {
        e.consume();
        logger.trace("user starts a session");
        try {
            checkoutAnnotation();
            boolean restart = PopUps.getBooleanFromUser("Restart now?", "Need to restart to apply new settings", "Restart Required");
            if (restart) {
                logger.debug("user choose to restart: " + restart);
                RestartApp.restartApplication();
            } else {
                PopUps.showWarningDialog("Warning", "Latest data successfully pulled from Github but now shown", "Need to restart");
            }
        } catch (Exception e1){
            PopUps.showWarningDialog("Warning", "Failure to execute terminal command", "Need to start manually");
        }
    }

    @FXML
    private void checkinData(ActionEvent e) {
        e.consume();
        logger.trace("user ends a session");
        try {
            checkinAnnotation();
            PopUps.showWarningDialog("Success", "Data successfully checked in to Github", "Next: send out unlocking message");
        } catch (Exception e1){
            PopUps.showWarningDialog("Warning", "Failure to check in data to Github", "Possible reasons & solutions:\n1) No data change: no need to check in data \n2) Data has not been saved: save session data and retry\n 3) unknown reasons: check in data manually");
        }
    }

    @FXML
    private void end(ActionEvent e) {
        e.consume();
        logger.trace("user ends a session");
        try {
            sendUnlockingEmail();
            PopUps.showWarningDialog("Success", "Unlocking message successfully sent out", "All clear. You can safely leave now");
        } catch (Exception e1){
            PopUps.showWarningDialog("Warning", "Failure to send unlocking message", "Need to send manually");
        }
    }

    private void updateHpo() throws IOException, InterruptedException, CommandExecutionException {
        String command = "git pull && cd src/ontology && make && cd ../..";
        String[] commands = new String[] {"/bin/bash", "-c", command};
        TerminalCommand tm = new TerminalCommand(commands, appTempData.getPathToHpGitRepo());
        int exitvalue = tm.execute();
        if (exitvalue != 0) {
            throw new CommandExecutionException("failure"); //borrowed an exception from another library
        }
    }

    private void checkoutAnnotation() throws IOException, InterruptedException, CommandExecutionException {

        String command = "git pull origin develop && git checkout develop";
        String[] commands = new String[] {"/bin/bash", "-c", command};
        TerminalCommand tm = new TerminalCommand(commands, settings.getAnnotationFolder());
        int exitvalue = tm.execute();
        if (exitvalue != 0) {
            throw new CommandExecutionException("failure"); //borrowed an exception from another library
        }

    }

    public void checkinAnnotation() throws IOException, InterruptedException, CommandExecutionException {

        String command = String.format("git add . && git commit -m \"%s\" && git push origin develop", "update");
        String[] commands = new String[] {"/bin/bash", "-c", command};
        TerminalCommand tm = new TerminalCommand(commands, settings.getAnnotationFolder());
        int exitvalue = tm.execute();
        if (exitvalue != 0) {
            throw new CommandExecutionException("failure"); //borrowed an exception from another library
        }

    }

    public void sendLockingEmail() throws IOException, InterruptedException, CommandExecutionException {
        String command = String.format("echo \"Biocurator: %s\" | mail -s \"LOCKING loinc2hpoAnnotation\" \"loinc2hpoannotation@googlegroups.com\"", settings.getBiocuratorID());
        String[] commands = new String[] {"/bin/bash", "-c", command};
        TerminalCommand tm = new TerminalCommand(commands, settings.getAnnotationFolder());
        int exitvalue = tm.execute();
        if (exitvalue != 0) {
            throw new CommandExecutionException("failure"); //borrowed an exception from another library
        }
    }

    public void sendUnlockingEmail() throws IOException, InterruptedException, CommandExecutionException {
        String command = String.format("echo \"Biocurator: %s\" | mail -s \"UNLOCKING loinc2hpoAnnotation\" \"loinc2hpoannotation@googlegroups.com\"", settings.getBiocuratorID());
        String[] commands = new String[] {"/bin/bash", "-c", command};
        TerminalCommand tm = new TerminalCommand(commands, settings.getAnnotationFolder());
        int exitvalue = tm.execute();
        if (exitvalue != 0) {
            throw new CommandExecutionException("failure"); //borrowed an exception from another library
        }
    }


}

