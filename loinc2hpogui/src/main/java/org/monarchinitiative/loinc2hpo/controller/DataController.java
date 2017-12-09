package org.monarchinitiative.loinc2hpo.controller;

import com.github.monarchinitiative.hpotextmining.HPOTextMining;
import com.github.monarchinitiative.hpotextmining.TextMiningResult;
import com.github.monarchinitiative.hpotextmining.model.PhenotypeTerm;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ontologizer.ontology.Ontology;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.monarchinitiative.loinc2hpo.gui.WidthAwareTextFields;
import org.monarchinitiative.loinc2hpo.gui.application.HRMDResourceManager;
import org.monarchinitiative.loinc2hpo.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

/**
 * This class is a controller for all elements presented in GUI window except Menu. It manages model data.
 */
public final class DataController implements Initializable {

    private static final Logger log = LogManager.getLogger();

    /**
     * The model class that is displayed in the elements of this controller.
     */
    private Model model;

    @Autowired
    private HRMDResourceManager resourceManager;

    @Autowired
    private Environment env;

    @Autowired
    private Ontology ontology;

    @Autowired
    private ExecutorService executorService;

//    @Autowired
//    private PubMedValidator pubMedValidator;

    @FXML
    private Label currentModelLabel;

    @FXML
    private ComboBox<String> genomeBuildComboBox;

    @FXML
    private TextField inputPubMedDataTextField;

    @FXML
    private Button inputPubMedButton;

    @FXML
    private TextField pmidTextField;

    @FXML
    private Button pmidLookupButton;

    @FXML
    private TextField entrezIDTextField;

    @FXML
    private TextField geneSymbolTextField;

    @FXML
    private Accordion variantsAccordion;

    @FXML
    private ComboBox<String> diseaseDatabaseComboBox;

    @FXML
    private TextField diseaseIDTextField;

    @FXML
    private TextField diseaseNameTextField;

    @FXML
    private TextField probandFamilyTextField;

    @FXML
    private ComboBox<String> sexComboBox;

    @FXML
    private TextField ageTextField;

    @FXML
    private TextField biocuratorIdTextField;

    @FXML
    private TextArea metadataTextArea;

    /**
     * Keep track to path of file containing data of current model so we don't need to ask user where to save a model
     * everytime a change has been made.
     */
    private File currentModelPath;

//    /**
//     * Determine {@link VariantMode} of given {@link Variant} .
//     *
//     * @param variant {@link Variant} instance to be analyzed.
//     * @return {@link VariantMode} corresponding to subclass of given variant instance.
//     */
//    private static VariantMode getVariantMode(Variant variant) {
//        if (variant instanceof MendelianVariant) {
//            return VariantMode.MENDELIAN;
//        }
//        if (variant instanceof SplicingVariant) {
//            return VariantMode.SPLICING;
//        }
//        if (variant instanceof SomaticVariant) {
//            return VariantMode.SOMATIC;
//        }
//        throw new IllegalArgumentException("ERROR: Variant of unknown type");
//    }

//    /**
//     * Determine subclass of given {@link Variant}, perform casting and create appropriate @link BaseVariantController
//     * subclass.
//     *
//     * @param mode    {@link VariantMode} to cast {@link Variant} object into.
//     * @param variant {@link Variant} to be casted.
//     * @return @link BaseVariantController subclass, which is also subclass of {@link TitledPane} and can be displayed
//     * as a content within this {@link DataController}.
//     */
//    private static TitledPane getVariantController(VariantMode mode, Variant variant) {
//        switch (mode) {
//            case MENDELIAN:
//                return new MendelianVariantController((MendelianVariant) variant);
//            case SPLICING:
//                return new SplicingVariantController((SplicingVariant) variant);
//            case SOMATIC:
//                return new SomaticVariantController((SomaticVariant) variant);
//            default:
//                String msg = String.format("ERROR: Unknown variant mode %s\n%s", mode.name(), variant);
//                log.error(msg);
//                throw new RuntimeException(msg);
//        }
//    }

    /**
     * Get path to XML file corresponding to current model.
     *
     * @return {@link File} containing the path.
     */
    public File getCurrentModelPath() {
        return currentModelPath;
    }

    /**
     * Set path to XML file corresponding to current model.
     *
     * @param currentModelPath {@link File} containing the path.
     */
    public void setCurrentModelPath(File currentModelPath) {
        this.currentModelPath = currentModelPath;
    }

    public Model getModel() {
        return model;
    }

    /**
     * Set model instance that will be displayed by this DataController. Setting the model will bind this controller's
     * View elements to the corresponding model properties.<p> <em>Note:</em> it's also good to update current model
     * path using {@link DataController#setCurrentModelPath(File)} after setting a new model here.
     *
     * @param model {@link Model} instance.
     */
    public void setModel(Model model) {
        this.model = model;

        initializeBindings(model);
    }

    /**
     * Create bindings between values of model attributes and GUI elements to ensure that updating of any value is
     * synchronized in model & GUI elements. Configure autocompletions to text fields.
     *
     * @param model {@link Model} instance to be bound with GUI elements.
     */
    private void initializeBindings(Model model) {

        // Current model title, this is "readOnly" binding
//        currentModelLabel.textProperty().bind(model.getPublication().titleProperty());
//
//        // Genome build
//        genomeBuildComboBox.valueProperty().bindBidirectional(model.genomeBuildProperty());
//
//        // Gene
//        entrezIDTextField.textProperty().bindBidirectional(model.getTargetGene().entrezIDProperty());
//        geneSymbolTextField.textProperty().bindBidirectional(model.getTargetGene().geneNameProperty());
//
//        // Disease
//        diseaseDatabaseComboBox.valueProperty().bindBidirectional(model.getDisease().databaseProperty());
//        diseaseIDTextField.textProperty().bindBidirectional(model.getDisease().diseaseIdProperty());
//        diseaseNameTextField.textProperty().bindBidirectional(model.getDisease().diseaseNameProperty());
//
//        // Proband & family
//        probandFamilyTextField.textProperty().bindBidirectional(model.getFamilyInfo().familyOrPatientIDProperty());
//        sexComboBox.valueProperty().bindBidirectional(model.getFamilyInfo().sexProperty());
//        ageTextField.textProperty().bindBidirectional(model.getFamilyInfo().ageProperty());
//
//        // Biocurator
//        biocuratorIdTextField.textProperty().bind(model.getBiocurator().bioCuratorIdProperty());
//
//        // Metadata
//        metadataTextArea.textProperty().bindBidirectional(model.getMetadata().metadataTextProperty());
    }

//    /**
//     * Load variant data into GUI accordion.
//     *
//     * @param variants {@link Collection} of {@link Variant} objects to be loaded.
//     */
//    private void loadVariants(Collection<Variant> variants) {
//        variantsAccordion.getPanes().clear();
//        variants.forEach(variant -> variantsAccordion.getPanes()
//                // variant controller is also TitledPane.
//                .add(getVariantController(getVariantMode(variant), variant)));
//    }


    @FXML
    void hpoTextMiningButtonAction() {
        URL textMiningUrl = null;
        try {
            textMiningUrl = new URL(env.getProperty("text.mining.url"));
        } catch (MalformedURLException e) {
            log.warn(e.getMessage());
        }
//        Set<PhenotypeTerm> terms = model.getHpoList().stream()
//                .map(hpo -> new PhenotypeTerm(ontology.getTerm(hpo.getHpoId()), (hpo.getObserved().equals("YES"))))
//                .collect(Collectors.toSet());
//
//        HPOTextMining hpoTextMining = new HPOTextMining(ontology, textMiningUrl, new Stage());
//        hpoTextMining.addTerms(terms); // terms already present in the model
//        hpoTextMining.setPmid((model.getPublication().getPmid() == null) ? "" : model.getPublication().getPmid());
//        TextMiningResult result = hpoTextMining.runAnalysis();
//
//        // possible change in pmid is ignored here
//        model.getHpoList().clear();// all terms were sent out so we're getting them back if they weren't removed by user
//        model.getHpoList().addAll(result.getTerms().stream()
//                .map(term -> new HPO(term.getHpoId(), term.getName(), (term.isPresent()) ? "YES" : "NOT"))
//                .collect(Collectors.toSet()));
    }




    /**
     * {@inheritDoc}<p>The new empty {@link Model} is implicitly created here. </p>
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HRMDResourceManager.getParametersFile().ifPresent(file -> {
//            ChoiceBasket basket = ChoiceBasket.loadChoices(file);
//            genomeBuildComboBox.getItems().addAll(basket.getGenomeBuild());
//            diseaseDatabaseComboBox.getItems().addAll(basket.getDiseaseDatabases());
//            sexComboBox.getItems().addAll(basket.getSex());
        });

        setModel(new Model());
        enableAutocompletions();
    }

    /**
     * Create autocompletions on GUI elements e.g. allowing completion of gene symbol after entering gene id. Add
     * suggestion boxes offering e.g. disease name based on a few typed characters.
     */
    private void enableAutocompletions() {
//
//        // create bindings for autocompletion of Entrez ID & Symbol
//        WidthAwareTextFields.bindWidthAwareAutoCompletion(entrezIDTextField, resourceManager.getEntrezId2symbol().keySet());
//        WidthAwareTextFields.bindWidthAwareAutoCompletion(geneSymbolTextField, resourceManager.getSymbol2entrezId().keySet());
//        entrezIDTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
//            if (newValue == null || newValue.equals("")) {
//                geneSymbolTextField.clear();
//            } else {
//                if (resourceManager.getEntrezId2symbol().containsKey(newValue)) {
//                    geneSymbolTextField.setText(resourceManager.getEntrezId2symbol().get(newValue));
//                }
//            }
//        }));
//        geneSymbolTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
//            if (newValue == null || newValue.equals("")) {
//                entrezIDTextField.clear();
//            } else {
//                if (resourceManager.getSymbol2entrezId().containsKey(newValue))
//                    entrezIDTextField.setText(resourceManager.getSymbol2entrezId().get(newValue));
//            }
//        }));
//
//        // create bindings for autocompletion of Disease ID & name (label)
//        WidthAwareTextFields.bindWidthAwareAutoCompletion(diseaseIDTextField, resourceManager.getMimid2canonicalName().keySet());
//        WidthAwareTextFields.bindWidthAwareAutoCompletion(diseaseNameTextField, resourceManager.getCanonicalName2mimid().keySet());
//        diseaseIDTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
//            if (newValue == null || newValue.equals("")) {
//                diseaseNameTextField.clear();
//            } else {
//                if (resourceManager.getMimid2canonicalName().containsKey(newValue))
//                    diseaseNameTextField.setText(resourceManager.getMimid2canonicalName().get(newValue));
//            }
//        }));
//        diseaseNameTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
//            if (newValue == null || newValue.equals("")) {
//                diseaseIDTextField.clear();
//            } else {
//                if (resourceManager.getCanonicalName2mimid().containsKey(newValue))
//                    diseaseIDTextField.setText(resourceManager.getCanonicalName2mimid().get(newValue));
//            }
//        }));
//
//        // Trim whitespaces in input & enable PMID lookup after a valid integer has been entered into the pmidTextField
//        pmidTextField.textProperty().addListener((observable, oldValue, newValue) -> {
//            String in = (newValue == null) ? "" : newValue;
//            if (in.matches("\\s+.*") || in.matches(".*\\s+")) { // whitespace at start or in the end
//                in = in.trim();
//            }
//            if (in.matches("\\d+")) {
//                pmidLookupButton.setDisable(false);
//            } else {
//                pmidLookupButton.setDisable(true);
//            }
//            pmidTextField.setText(in);
//        });
    }

}
