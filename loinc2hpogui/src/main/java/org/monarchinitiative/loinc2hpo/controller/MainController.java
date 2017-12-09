package org.monarchinitiative.loinc2hpo.controller;

import com.genestalker.springscreen.core.DialogController;
import com.genestalker.springscreen.core.FXMLDialog;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.monarchinitiative.loinc2hpo.gui.application.HRMDResourceManager;
import org.monarchinitiative.loinc2hpo.gui.application.ScreensConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is the controller of the main dialog of the HRMD app. <p>
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public final class MainController implements DialogController {

    private static final Logger log = LogManager.getLogger();

    /**
     * Reference to the {@link FXMLDialog} object representing the window of the MVC view element of this controller.
     */
    private FXMLDialog dialog;

    @Autowired
    private ScreensConfig screensConfig;

    /**
     * {@inheritDoc}
     */
    @Autowired
    private HRMDResourceManager resourceManager;



    @FXML
    private ScrollPane contentScrollPane;

    /**
     * {@inheritDoc}
     *
     * @param dialog The {@link FXMLDialog} instance which represents an independent window.
     */
    @Override
    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }

//    @FXML
//    void newMenuItemAction() {
//        screensConfig.dataController().setModel(new DiseaseCaseModel());
//        screensConfig.dataController().setCurrentModelPath(null);
//    }
//
//    @FXML
//    void openMenuItemAction() {
//        Optional<File> which = ScreensConfig.selectFileToOpen(dialog,
//                new File(resourceManager.getResources().getDiseaseCaseDir()), "Select model to open");
//        which.ifPresent(selected -> {
//            Optional<DiseaseCaseModel> dcm = XMLModelParser.loadDiseaseCaseModel(selected);
//            dcm.ifPresent(model -> {
//                screensConfig.dataController().setModel(model);
//                screensConfig.dataController().setCurrentModelPath(selected);
//            });
//        });
//    }
//
//    @FXML
//    void saveMenuItemAction() {
//        saveModel(screensConfig.dataController().getCurrentModelPath(),
//                screensConfig.dataController().getModel());
//    }

//    @FXML
//    void saveAsMenuItemAction() {
//        saveModel(null, screensConfig.dataController().getModel());
//    }

    @FXML
    void exitMenuItemAction() {
        dialog.close();
    }

//    @FXML
//    void showEditCurrentPublicationMenuItemAction() {
//        FXMLDialog showDialog = screensConfig.showEditPublicationDialog();
//        Publication publication = screensConfig.dataController().getModel().getPublication();
//        screensConfig.showEditPublicationController().setPublication(publication);
//        showDialog.show();
//    }
//
//    @FXML
//    void setResourcesMenuItemAction() {
//        screensConfig.setResourcesDialog().show();
//    }
//
//    @FXML
//    void showResourcesMenuItemAction() {
//        screensConfig.showResourcesDialog().show();
//    }



//    @FXML
//    void validateCurrentEntryMenuItemAction() {
//        List<ValidationLine> lines = validationRunner.validateModels(
//                Collections.singleton(screensConfig.dataController().getModel()));
//        FXMLDialog dialog = screensConfig.showValidationResultsDialog();
//        screensConfig.showValidationResultsController().setValidationLines(lines);
//        dialog.show();
//    }
//
//    @FXML
//    void validateAllFilesMenuItemAction() {
//        Set<DiseaseCaseModel> models = modelParser.getModelNames().stream()
//                .map(modelParser::readModel)
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .collect(Collectors.toSet());
//
//        List<ValidationLine> lines = validationRunner.validateModels(models);
//        FXMLDialog dialog = screensConfig.showValidationResultsDialog();
//        screensConfig.showValidationResultsController().setValidationLines(lines);
//        dialog.show();
//    }

//    @FXML
//    void exportToCSVMenuItemAction() {
//        ModelExporter exporter = new TSVModelExporter(resourceManager.getResources().getDiseaseCaseDir(), "\t");
//        Optional<File> where = ScreensConfig.selectFileToSave(dialog, new File(resourceManager.getResources()
//                        .getDiseaseCaseDir()),
//                "Export variants to CSV file", "variants.tsv");
//        where.ifPresent(dest -> {
//            try (Writer writer = new FileWriter(dest)) {
//                exporter.exportModels(writer);
//            } catch (IOException e) {
//                log.warn(String.format("Error occured during variant export: %s", e.getMessage()));
//            }
//        });
//    }

    @FXML
    void exportToSummaryFileMenuItemAction() {
        // TODO -- export to summary menuitem - implement me
    }

    @FXML
    void exportListOfComHetCasesWithOneCodingVariantMenuItemAction() {
        // TODO -- export list of comphets menuitem - implement me
    }



    @FXML
    void helpMenuItemAction() {
        FXMLDialog dialog = screensConfig.showHtmlContentDialog();
        screensConfig.showHtmlContentController().setContent(getClass().getResource("/html/hrmd.html"));
        dialog.show();
    }

//    @FXML
//    void TFListMenuItemAction() {
//        FXMLDialog dialog = screensConfig.showHtmlContentDialog();
//        screensConfig.showHtmlContentController().setContent(getClass().getResource("/html/tflist.html"));
//        dialog.show();
//    }
//
//    @FXML
//    void NCIThesaurusMenuItemAction() {
//        FXMLDialog dialog = screensConfig.showHtmlContentDialog();
//        screensConfig.showHtmlContentController().setContent(getClass().getResource("/html/ncithesaurus.html"));
//        dialog.show();
//    }

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        contentScrollPane.setContent(screensConfig.dataPane());
    }

    /**
     * Save model to file in project's model directory.<p> <ul><li>Ensure that DiseaseCaseDir exists</li><li>Validate
     * completness of model</li><li>Check if we are saving model which has been opened from XML file or a new Model and
     * ask user where to save, if necessary .</li><li>Save it</li></ul>
     */
//    private void saveModel(File currentModelPath, DiseaseCaseModel model) {
//        String conversationTitle = "Save data into file";
//
//        if (completenessValidator.validateDiseaseCase(model) != ValidationResult.PASSED) {
//            PopUps.showInfoMessage(completenessValidator.getErrorMessage(), "The data is not complete, thus not " +
//                    "saved.");
//            return;
//        }
//
//        if (currentModelPath == null) {
//            String suggestedXmlName = model.getFileName() + ".xml";
//            Optional<File> where = ScreensConfig.selectFileToSave(dialog, new File(resourceManager.getResources()
//                    .getDiseaseCaseDir()), conversationTitle, suggestedXmlName);
//            if (!where.isPresent())
//                return;
//
//            currentModelPath = where.get();
//        }
//        model.getBiocurator().setBioCuratorId(resourceManager.getResources().getBiocuratorId());
//        XMLModelParser.saveDiseaseCaseModel(model, currentModelPath);
//
//        screensConfig.dataController().setCurrentModelPath(currentModelPath);
//        PopUps.showInfoMessage(String.format("Data saved into file %s", currentModelPath.getName()), conversationTitle);
//    }
}
