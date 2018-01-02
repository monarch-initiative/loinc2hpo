package org.monarchinitiative.loinc2hpo.model;

import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.github.phenomics.ontolib.formats.hpo.HpoTermRelation;
import com.github.phenomics.ontolib.ontology.data.Ontology;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.io.HpoOntologyParser;
import org.monarchinitiative.loinc2hpo.loinc.AnnotatedLoincRangeTest;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    /** The complete HPO ontology. */
    private  Ontology<HpoTerm, HpoTermRelation> ontology=null;
    /** Key: a loinc code such as 10076-3; value: the corresponding {@link AnnotatedLoincRangeTest} object .*/
    public Map<String,AnnotatedLoincRangeTest> testmap=new HashMap<>();

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

    public int getOntologyTermCount() { return ontology!=null?ontology.countNonObsoleteTerms():0; }
    public int getLoincAnnotationCount() { return testmap!=null?this.testmap.size():0;}


    public Model() {
        init();
    }

    public void addLoincTest(AnnotatedLoincRangeTest test) {
        // todo warn if term already in map
        testmap.put(test.getLoincNumber(),test);
    }

    public Map<String,AnnotatedLoincRangeTest> getTestmap(){ return testmap; }


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
            this.ontology = parser.getPhenotypeSubontology();
        } catch (IOException e) {
            logger.error("Could not parse HPO obo file at "+pathToHpoOboFile);
        }
    }
    /** @return a map will all terms of the Hpo Phenotype subontology. */
    public ImmutableMap<String,HpoTerm> getTermMap() {
        ImmutableMap.Builder<String,HpoTerm> termmap = new ImmutableMap.Builder<>();
        if (ontology !=null) {

           // ontology.getTermMap().values().  forEach(term -> termmap.put(term.getName(), term));
            // for some reason there is a bug here...issue #34 on ontolib tracker
            // here is a workaround to remove duplicate entries
            List<HpoTerm> res = ontology.getTermMap().values().stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet()
                    .stream()
                    .filter(e -> e.getValue() == 1)      //this might cause
                    // some classes to be ignored(? e.g. hypoglycemia)
                    .map(e -> e.getKey())
                    .collect(Collectors.toList());

            res.forEach( term -> termmap.put(term.getName(),term));
            res.forEach( term -> System.out.println(term.getName()));
        }
        return termmap.build();
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
            if (pathToAnnotationFile!=null) {
                bw.write(String.format("annotationFile:%s\n",pathToAnnotationFile));
            }
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
