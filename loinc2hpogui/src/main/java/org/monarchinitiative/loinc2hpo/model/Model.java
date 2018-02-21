package org.monarchinitiative.loinc2hpo.model;

import com.github.phenomics.ontolib.formats.hpo.HpoOntology;
import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.github.phenomics.ontolib.ontology.data.*;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.io.HpoOntologyParser;
import org.monarchinitiative.loinc2hpo.loinc.*;
import org.monarchinitiative.loinc2hpo.loinc.QnLoinc2HPOAnnotation;

import java.io.*;
import java.util.*;

/**
 * Prototype model for LOINC to HPO Biocuration process.
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.1.2 (2017-12-12)
 */
public class Model {
    private static final Logger logger = LogManager.getLogger();

    private String pathToLoincCoreTableFile=null;
    /** We save a few settings in a file that we store in ~/.loinc2hpo/loinc2hpo.settings. This variable should
     * be initialized to the absolute path of the file. */
    private String pathToSettingsFile=null;
    /** Path to {@code hp.obo}. */
    private String pathToHpoOboFile=null;
    /** PATH to {@code hp.owl}. */
    private String pathToHpoOwlFile = null;
    /** Path to the file we are creating with LOINC code to HPO annotations. */
    private String pathToAnnotationFile=null;
    /** A String such as MGM:rrabbit .*/
    private String biocuratorID=null;

    private String pathToJsonFhirFile=null;


    /** The complete HPO ontology. */
    private HpoOntology ontology=null;
    private static final TermPrefix HPPREFIX = new ImmutableTermPrefix("HP");
    /** Key: a loinc code such as 10076-3; value: the corresponding {@link QnLoinc2HPOAnnotation} object .*/
    public Map<LoincId,UniversalLoinc2HPOAnnotation> loincAnnotationMap =new LinkedHashMap<>();

    private Map<LoincId, LoincEntry> loincEntryMap;
    private HashSet<LoincId> loincIds = new HashSet<>();

    private Map<String, String> tempStrings = new HashMap<>();//hpo terms before being used to create an annotation
    private Map<String, String> tempAdvancedAnnotation = new HashMap<>();//a advanced annotation before it is being added to record
    private UniversalLoinc2HPOAnnotation currentAnnotation = null;
    //private boolean tempInversed= false;
    private boolean inversedBasicMode = false; //whether inverse is checked for basic mode
    private boolean inversedAdvancedMode = false; //whether inverse is checked for advanced mode

    public void setLoincEntryMap(Map<LoincId, LoincEntry> map) {
        this.loincEntryMap = map;
        loincIds.addAll(this.loincEntryMap.keySet());
    }
    public Map<LoincId, LoincEntry> getLoincEntryMap() {
        return this.loincEntryMap;
    }
    public HashSet<LoincId> getLoincIds() { return this.loincIds; }

    //hpo term maps from name or id to hpoterm
    private ImmutableMap<String,HpoTerm> termmap=null;
    private ImmutableMap<TermId, HpoTerm> termmap2 = null;


    /**
     * The following section handles github labels for HPO
     * (used to suggest new hpo terms for some loinc codes)
     */
    private List<String> labels = new ArrayList<>();
    public boolean hasLabels() {
        return !labels.isEmpty();
    }

    public void setGithublabels(List<String> labels) {
        this.labels.addAll(labels);
    }

    public List<String> getGithublabels() {
        return this.labels;
    }


    public void setPathToLoincCoreTableFile(String pathToLoincCoreTableFile) {
        this.pathToLoincCoreTableFile = pathToLoincCoreTableFile;
    }
    public void setPathToSettingsFile(String p) { this.pathToSettingsFile=p;}
    public void setPathToAnnotationFile(String p) {pathToAnnotationFile=p;}
    public void setPathToHpOboFile(String p) { pathToHpoOboFile=p;}
    public void setPathToHpOwlFile(String p) { pathToHpoOwlFile = p;
    }
    public void setBiocuratorID(String id){biocuratorID=id;}

    public String getPathToLoincCoreTableFile() {
        return pathToLoincCoreTableFile;
    }
    public String getPathToHpoOboFile() {
        return pathToHpoOboFile;
    }
    public String getBiocuratorID() {return biocuratorID;}
    public String getPathToAnnotationFile(){return pathToAnnotationFile;}
    public String getPathToHpoOwlFile(){ return pathToHpoOwlFile;}


    public void setFhirFilePath(String p) { pathToJsonFhirFile=p;}
    public String getPathToJsonFhirFile() { return pathToJsonFhirFile; }

    public int getOntologyTermCount() { return ontology!=null?ontology.countNonObsoleteTerms():0; }
    public int getLoincAnnotationCount() { return loincAnnotationMap !=null?this.loincAnnotationMap.size():0;}


    public void setTempTerms(Map<String, String> temp) { this.tempStrings = temp; }
    public Map<String, String> getTempTerms() { return new HashMap<>(this.tempStrings); }
    public void setTempAdvancedAnnotation(Map<String, String> tempAdvancedAnnotation) { this.tempAdvancedAnnotation = tempAdvancedAnnotation;}
    public Map<String, String> getTempAdvancedAnnotation() {return new HashMap<>(this.tempAdvancedAnnotation);}

    public boolean isInversedBasicMode() {
        return inversedBasicMode;
    }

    public void setInversedBasicMode(boolean inversedBasicMode) {
        this.inversedBasicMode = inversedBasicMode;
    }

    public boolean isInversedAdvancedMode() {
        return inversedAdvancedMode;
    }

    public void setInversedAdvancedMode(boolean inversedAdvancedMode) {
        this.inversedAdvancedMode = inversedAdvancedMode;
    }

    public void setCurrentAnnotation(UniversalLoinc2HPOAnnotation current) {this.currentAnnotation = current;}
    public UniversalLoinc2HPOAnnotation getCurrentAnnotation() {
        return currentAnnotation;
    }

    public Model() {
        init();
    }


    public String termId2HpoName(TermId id ) {
        if (id ==null) {
            logger.error("Could not find id "+id);
            return "?";
        }
        if (ontology.getTermMap().get(id)==null) {
            logger.error("id not in mapp");
            return "?";
        }
        return ontology.getTermMap().get(id).getName();
    }


    public TermId string2TermId(String hpoId) {
        if (! hpoId.startsWith("HP:")) {
            logger.error("Malformed HPO ID: "+ hpoId);
            return null;
        }
        hpoId= hpoId.substring(3);
        return new ImmutableTermId(HPPREFIX,hpoId);
    }



    public void addLoincTest(UniversalLoinc2HPOAnnotation test) {
        // todo warn if term already in map
        loincAnnotationMap.put(test.getLoincId(),test);
        logger.debug("Annotation is add for: " + test.getLoincId());
    }

    public void removeLoincTest(String loincNum) {
        if (this.loincAnnotationMap.containsKey(loincNum)) {
            this.loincAnnotationMap.remove(loincNum);
        } else {
            logger.error("removing a Loinc annotation record that does not " +
                    "exist");
        }
    }

    public Map<LoincId,UniversalLoinc2HPOAnnotation> getLoincAnnotationMap(){ return loincAnnotationMap; }


    private void init() {
    }

    /** Parse the {@code hp.obo} file. This will initialize {@link #ontology}. */
    public void parseOntology() {
        if (this.pathToHpoOboFile==null) {
            logger.error("Attempt to parse hp.obo file with null path to file");
            return;
        }
        HpoOntologyParser parser = new HpoOntologyParser(pathToHpoOboFile);
        try {
            parser.parseOntology();
            this.ontology = parser.getOntology();
        } catch (IOException e) {
            logger.error("Could not parse HPO obo file at "+pathToHpoOboFile);
        }
        termmap=parser.getTermMap();
        termmap2=parser.getTermMap2();
    }

    public ImmutableMap<String,HpoTerm> getTermMap() { return termmap;}

    public Map<TermId, HpoTerm> getTermMap2() { return termmap2; }


    public HpoOntology getOntology() {
        return ontology;
    }

    /** Write a few settings to a file in the user's .loinc2hpo directory. */
    public void writeSettings() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(pathToSettingsFile));
            if (biocuratorID!=null) {
                bw.write(String.format("biocuratorid:%s\n",biocuratorID));
            }
            if (pathToLoincCoreTableFile!=null) {
                bw.write(String.format("loincTablePath:%s\n",pathToLoincCoreTableFile));
            }

            //Do not save pathToAnnotationFile so that it can be changed easily
            //if (pathToAnnotationFile!=null) {
            //    bw.write(String.format("annotationFile:%s\n",
            //        pathToAnnotationFile));
            //}
            if (pathToHpoOboFile!=null) {
                bw.write(String.format("hp-obo:%s\n",pathToHpoOboFile));
            }
            if (pathToHpoOwlFile!= null) {
                bw.write(String.format("hp-owl:%s\n", pathToHpoOwlFile));
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Could not write settings at " + pathToSettingsFile);
        }
    }

    /** Read the loinc2hpo settings file from the user's .loinc2hpo directory. */
    public void inputSettings(final String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line = null;
            while ((line = br.readLine()) != null) {
               int idx=line.indexOf(":");
               if (idx<0) {
                   logger.error("Malformed settings line (no semicolon): "+line);
               }
               if (line.length()<idx+2) {
                   logger.error("Malformed settings line (value too short): "+line);
               }
               String key,value;
               key=line.substring(0,idx).trim();
               value=line.substring(idx+1).trim();

                if (key.equals("biocuratorid")) this.biocuratorID = value;
                else if (key.equals("loincTablePath")) this.pathToLoincCoreTableFile = value;
                else if (key.equals("annotationFile")) this.pathToAnnotationFile = value;
                else if (key.equals("hp-obo")) this.pathToHpoOboFile = value;
                else if (key.equals("hp-owl")) this.pathToHpoOwlFile = value;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Could not open settings at " + path);
        }
    }


}
