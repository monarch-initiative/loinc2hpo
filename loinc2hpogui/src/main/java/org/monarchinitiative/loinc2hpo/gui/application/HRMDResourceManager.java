package org.monarchinitiative.loinc2hpo.gui.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.gui.PopUps;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class is responsible for management of resources needed to run the gui. <p>Created by Daniel Danis on 7/16/17.
 */
public final class HRMDResourceManager {
    private static final Logger logger = LogManager.getLogger();
    /**
     * Use this name to save HP.obo file on the local filesystem.
     */
    public static final String DEFAULT_HPO_FILE_NAME = "HP.obo";

    /**
     * Use this name to save the Entrez gene file on the local filesystem.
     */
    public static final String DEFAULT_ENTREZ_FILE_NAME = "Homo_sapiens.gene_info.gz";

    private static final Logger log = LogManager.getLogger(HRMDResourceManager.class);

    /**
     * By default, the <code>hrmd-resources.json</code> file with content of {@link HRMDResourceManager} is expected to
     * be present at classpath.
     */
    private static final String DEFAULT_RESOURCES_NAME = "hrmd-resources.json";

    private static final File DEFAULT_RESOURCES_FILE = new File(getJarDir(), DEFAULT_RESOURCES_NAME);

    /**
     * Name of the YAML file containing data used to populate FXML (view) elements. Parsed into {link
     * org.monarchinitiative.hrmd_gui.model.ChoiceBasket} object by {link org.monarchinitiative.hrmd_gui.model.ChoiceBasket}
     * factory method.
     */
    private static final String templateFileName = "templateParameters.yml";

    private final File resourcesFile;

    private final ObjectMapper mapper;

    private final HRMDResources resources;

    /**
     * Maps which hold OMIM terms and allow access using MIM ID or disease canonical name. Loaded lazily.
     */
    private Map<String, String> mimid2canonicalName, canonicalName2mimid;

    /**
     * Maps which hold Entrez gene info and allow access using Entrez ID or gene symbol. Loaded lazily.
     */
    private Map<String, String> entrezId2symbol, symbol2entrezId;

    /**
     * Map which holds HPO term information and allows access to HPO ID using HPO term as a key. Loaded lazily.
     */
    private Map<String, String> hpoTerm2id;

    /**
     * Initialize class & ensure that the base resource directory exists. Attempt to read the provided file at first
     * place. If the file doesn't exist or is null, then initialize empty {@link HRMDResources}. classpath. Throw an
     * IOException if the loading was not successful here.
     */
    public HRMDResourceManager(File resourcesFile) throws IOException {
        mapper = new ObjectMapper();
        if (existsAndIsFile(resourcesFile)) { // load from provided file
            log.info(String.format("Loading resources from file %s", resourcesFile.getAbsolutePath()));
            this.resources = mapper.readValue(resourcesFile, HRMDResources.class);
            this.resourcesFile = resourcesFile;
        } else if (!existsAndIsFile(resourcesFile) && existsAndIsFile(DEFAULT_RESOURCES_FILE)) { // load from default
            log.info(String.format("Loading resources from default resources file %s", DEFAULT_RESOURCES_FILE));
            this.resources = mapper.readValue(DEFAULT_RESOURCES_FILE, HRMDResources.class);
            this.resourcesFile = DEFAULT_RESOURCES_FILE;
        } else if (!existsAndIsFile(DEFAULT_RESOURCES_FILE)) { // create new blank resources
            log.info(String.format("Creating new resources which will be stored to %s", DEFAULT_RESOURCES_FILE));
            this.resources = new HRMDResources("", "", "", "", "", "");
            this.resourcesFile = DEFAULT_RESOURCES_FILE;
        } else {
            throw new RuntimeException(String.format("Provided path %s doesn't point to resource file!",
                    resourcesFile.getAbsolutePath()));
        }
    }

    private static boolean existsAndIsFile(File file) {
        return file != null && file.exists() && file.isFile();
    }

    private static boolean existsAndIsDir(File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    /**
     * Get path of directory where the HRMD GUI JAR file is localized.
     *
     * @return {@link File} object with requested path
     */
    public static File getJarDir() {
        // inspired by this post: https://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
        String jarFilePath = HRMDResourceManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File jarFile = new File(jarFilePath);
        return jarFile.getParentFile();
    }

    /**
     * Get platform-independent path to user's home directory. If it's not possible to retrieve the path e.g. due to
     * the {@link SecurityException}, then {@link #getJarDir()} path is returned.
     *
     * @return {@link File} with path to user's home directory or to the JAR-file parental directory
     */
    public static File getUserHomeDir() {
        try {
            return (System.getProperty("user.home") == null) ? getJarDir() : new File(System.getProperty("user.home"));
        } catch (Exception e) {
            return getJarDir();
        }
    }

    /**
     * Get path where YAML parameters file should be localized. There are two possible paths: <ul> <li>in the directory
     * where the JAR file is</li> <li>inside of the JAR file</li> </ul> The first has a priority.
     *
     * @return {@link Optional<File>} object with requested path.
     */
    public static Optional<File> getParametersFile() {
        // load YAML file from the directory where the JAR file is located (running from the distribution/assembly files scenario).
        File paramsInJarDirectory = new File(getJarDir() + File.separator + templateFileName);
        if (paramsInJarDirectory.exists()) {
            log.info(String.format("Using template parameters file: %s", paramsInJarDirectory.getAbsolutePath()));
            return Optional.of(paramsInJarDirectory);
        }
        // trying the classpath - this usually works when launching the app from IDE such as IntelliJ.
        File paramsInClasspath = new File(HRMDResourceManager.class.getResource("/" + templateFileName).getPath());
        if (paramsInClasspath.exists()) {
            log.info(String.format("Using template parameters file: %s", paramsInClasspath.getAbsolutePath()));
            return Optional.of(paramsInClasspath);
        }
        // load from within the JAR file - make a temporary copy of the YAML file.
        File paramsInJarfile = makeTemporaryCopy();
        if (paramsInJarfile != null) {
            log.info(String.format("Using temporary copy of template parameters file: %s", paramsInJarfile.getAbsolutePath()));
            return Optional.of(paramsInJarfile);
        }

        return Optional.empty();
    }

    /**
     * As a last resort, the YAML params file from within the JAR archive is used. The file is copied into temporary
     * file outside of the JAR file. The temporary file is deleted upon JVM termination.
     *
     * @return The {@link File} object pointing to the temporary YAML file or null if there was a problem during any IO
     * operation.
     */
    private static File makeTemporaryCopy() {
        InputStream is = null;
        OutputStream out = null;
        File paramsInJarfile = null;

        try {
            paramsInJarfile = File.createTempFile("templateParameters", ".yml");
            paramsInJarfile.deleteOnExit();
            is = HRMDResourceManager.class.getResourceAsStream("/" + templateFileName);
            out = new FileOutputStream(paramsInJarfile);
            byte[] bucket = new byte[1024];
            int bytesRead = 0;
            while (bytesRead != -1) {
                bytesRead = is.read(bucket);
                if (bytesRead > 0)
                    out.write(bucket, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (paramsInJarfile.exists()) {
            return paramsInJarfile;
        }
        return null;
    }

    /**
     * Return true if all required resources/files for running the gui has been downloaded and paths have been
     * initialized.
     *
     * @return true if all is ok.
     */
    public boolean isInitialized() {
        List<File> dirs = Arrays.asList(new File(resources.getRefGenomeDir()), new File(resources.getDataDir()), new
                File(resources.getDiseaseCaseDir()));
        List<File> files = Arrays.asList(new File(resources.getHpOBOPath()), new File(resources.getEntrezGenePath()));

        return (dirs.stream().allMatch(HRMDResourceManager::existsAndIsDir)
                && files.stream().allMatch(HRMDResourceManager::existsAndIsFile)
                // digits, upper/lowercase letters, hyphen, underscore & colon allowed
                && resources.getBiocuratorId().matches("[a-zA-Z:_\\-0-9]+"));
    }

    /**
     * Save the current resources to file <code>settings.json</code> (classpath).
     */
    public void saveResources() {
        try (Writer writer = new BufferedWriter(new FileWriter(resourcesFile))) {
            mapper.writeValue(writer, resources);
        } catch (IOException e) {
            PopUps.showException("Save resources", "Ooops...", "Error occured during saving of the resources.", e);
            log.error("Error occured during saving of the resources.", e);
        }
        log.info(String.format("Resources saved to file %s", resourcesFile));
    }

    public HRMDResources getResources() {
        return resources;
    }

    public Optional<File> getDataDir() {
        if (resources.getDataDir() != null) {
            File dd = new File(resources.getDataDir());
            if (existsAndIsDir(dd)) {
                return Optional.of(dd);
            }
        }
        return Optional.empty();
    }

   /* public Map<String, String> getHpoTerm2id() {
        if (hpoTerm2id == null) {
            HPOParser parser = new HPOParser(resources.getHpOBOPath());
            this.hpoTerm2id = parser.getHpoName2id();
        }
        return hpoTerm2id;
    }

    public Map<String, String> getMimid2canonicalName() {
        if (mimid2canonicalName == null) {
            OMIMParser omimParser = new OMIMParser();
            this.mimid2canonicalName = omimParser.getMimid2canonicalName();
            this.canonicalName2mimid = omimParser.getCanonicalName2mimid();
        }
        return mimid2canonicalName;
    }*/

    /*public Map<String, String> getCanonicalName2mimid() {
        if (canonicalName2mimid == null) {
            OMIMParser omimParser = new OMIMParser();
            this.mimid2canonicalName = omimParser.getMimid2canonicalName();
            this.canonicalName2mimid = omimParser.getCanonicalName2mimid();
        }
        return canonicalName2mimid;
    }

    public Map<String, String> getEntrezId2symbol() {
        if (entrezId2symbol == null) {
            EntrezParser entrezParser = new EntrezParser(resources.getEntrezGenePath());
            this.entrezId2symbol = entrezParser.getEntrezId2symbol();
            this.symbol2entrezId = entrezParser.getSymbol2entrezId();
        }
        return entrezId2symbol;
    }

    public Map<String, String> getSymbol2entrezId() {
        if (symbol2entrezId == null) {
            EntrezParser entrezParser = new EntrezParser(resources.getEntrezGenePath());
            this.entrezId2symbol = entrezParser.getEntrezId2symbol();
            this.symbol2entrezId = entrezParser.getSymbol2entrezId();
        }
        return symbol2entrezId;
    }*/
}
