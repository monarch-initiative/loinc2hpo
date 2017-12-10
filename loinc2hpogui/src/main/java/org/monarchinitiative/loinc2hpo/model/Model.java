package org.monarchinitiative.loinc2hpo.model;

import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.github.phenomics.ontolib.formats.hpo.HpoTermRelation;
import com.github.phenomics.ontolib.ontology.data.Ontology;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.io.HpoOntologyParser;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Model {
    private static final Logger logger = LogManager.getLogger();

    private String pathToLoincCoreTableFile=null;

    private String biocuratorname=null;

    private String pathToSettingsFile=null;

    private String pathToHpoOboFile=null;

    private String pathToAnnotationFile=null;

    private  Ontology<HpoTerm, HpoTermRelation> ontology=null;

    public void setPathToLoincCoreTableFile(String pathToLoincCoreTableFile) {
        this.pathToLoincCoreTableFile = pathToLoincCoreTableFile;
    }

    public void setPathToSettingsFile(String p) { this.pathToSettingsFile=p;}

    public void setPathToAnnotationFile(String p) {pathToAnnotationFile=p;}

    public void setPathToHpOboFile(String p) { pathToHpoOboFile=p;}


    public String getPathToLoincCoreTableFile() {
        return pathToLoincCoreTableFile;
    }
    public String getPathToHpoOboFile() {
        return pathToHpoOboFile;
    }


    public Model() {
        init();
    }



    private void init() {
    }


    public void parseOntology() {
        if (this.pathToHpoOboFile==null) {
            logger.error("Attempt to parse hpobo file with null path to file");
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
                    .filter(e -> e.getValue() == 1)
                    .map(e -> e.getKey())
                    .collect(Collectors.toList());

            res.forEach( term -> termmap.put(term.getName(),term));
        }
        return termmap.build();
    }


    public void writeSettings() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(pathToSettingsFile));
            if (biocuratorname!=null) {
                bw.write(String.format("biocuratorname:%s\n",biocuratorname));
            }
            if (pathToLoincCoreTableFile!=null) {
                bw.write(String.format("loincTablePath:%s\n",pathToLoincCoreTableFile));
            }
            if (pathToAnnotationFile!=null) {
                bw.write(String.format("annotationFile:%s\n"+pathToAnnotationFile));
            }
            if (pathToHpoOboFile!=null) {
                bw.write(String.format("hp-obo:%s\n",pathToHpoOboFile));
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Could not write settings at " + pathToSettingsFile);
        }
    }

    public void setSettings(final String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line = null;
            while ((line = br.readLine()) != null) {
               logger.trace(line);
                String A[] = line.split(":");
                if (A[0].equals("biocuratorname")) this.biocuratorname = A[1].trim();
                else if (A[0].equals("loincTablePath")) this.pathToLoincCoreTableFile = A[1].trim();
                else if (A[0].equals("annotationFile")) this.pathToAnnotationFile = A[1].trim();
                else if (A[0].equals("hp-obo")) this.pathToHpoOboFile = A[1].trim();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Could not open settings at " + path);
        }
    }

}
