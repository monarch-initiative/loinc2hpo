package org.monarchinitiative.loinc2hpo.io;

import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.*;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * This class uses the <a href="https://github.com/monarch-initiative/phenol">phenol</a> library to the Human Phenotype Ontology in .obo or .owl format
 * (see <a href="http://human-phenotype-ontology.github.io/">HPO Homepage</a>).
 * @author Peter Robinson
 * @author Aaron Zhang
 */
public class HpoOntologyParser {
    private static final Logger logger = LogManager.getLogger();
    /** Path to the {@code hp.obo} file. */
    private String hpoOboPath =null;
    private Ontology hpoOntology;


    /** Map of all of the Phenotypic abnormality terms (i.e., not the inheritance terms). */
    private ImmutableMap<String, Term> termmap;
    private ImmutableMap<TermId, Term> termmap2;


    public HpoOntologyParser(String path){
        if (path.contains(".obo")) {
            hpoOboPath = path;
        }
    }

    /**
     * Parse the HP ontology file and place the data in {@link #hpoOntology} and
     * @throws PhenolException, OWLOntologyCreationException
     */
    public void parseOntology() throws PhenolException, FileNotFoundException {
        logger.debug("ontology path: " + hpoOboPath);
        this.hpoOntology = OntologyLoader.loadOntology(new File(hpoOboPath));
    }

    private void initTermMaps() {
        if (hpoOntology !=null) {
            termmap2 = ImmutableMap.copyOf(this.hpoOntology.getTermMap());
            ImmutableMap.Builder<String,Term> termmapBuilder = new ImmutableMap.Builder<>();
            this.hpoOntology.getTerms().stream().distinct().forEach(term -> termmapBuilder.put(term.getName(), term));
            termmap = termmapBuilder.build();
        }
    }

    public Ontology getOntology() {
        return this.hpoOntology;
    }

    /** @return a map will all terms of the Hpo Phenotype subontology. */
    public ImmutableMap<String,Term> getTermMap() {
        if (termmap == null) {
            initTermMaps();
        }
        return termmap;
    }

    /**
     * @return a map from the term id of the HPO phenotype subontology
     */
    public ImmutableMap<TermId,Term> getTermMap2() {
        if (termmap2 == null) {
            initTermMaps();
        }
        return termmap2;
    }


}
