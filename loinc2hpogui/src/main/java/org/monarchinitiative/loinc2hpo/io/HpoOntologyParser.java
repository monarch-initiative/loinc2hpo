package org.monarchinitiative.loinc2hpo.io;

import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;

import org.monarchinitiative.phenol.io.obo.hpo.HpOboParser;
import org.monarchinitiative.phenol.io.owl.hpo.HpOwlParser;
import org.monarchinitiative.phenol.ontology.data.*;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;

/**
 * This class uses the <a href="https://github.com/phenomics/ontolib">ontolb</a> library to
 * parse both the {@code hp.obo} file and the phenotype annotation file
 * {@code phenotype_annotation.tab}
 * (see <a href="http://human-phenotype-ontology.github.io/">HPO Homepage</a>).
 * @author Peter Robinson
 * @author Vida Ravanmehr
 * @author Aaron Zhang
 */
public class HpoOntologyParser {
    private static final Logger logger = LogManager.getLogger();
    /** Path to the {@code hp.obo} file. */
    private String hpoOboPath =null;
    private String hpoOwlPath = null;
    private boolean isObo = false;
    private HpoOntology hpoOntology;


    /** Map of all of the Phenotypic abnormality terms (i.e., not the inheritance terms). */
    private ImmutableMap<String, Term> termmap;
    private ImmutableMap<TermId, Term> termmap2;


    public HpoOntologyParser(String path){
        if (path.contains(".obo")) {
            isObo = true;
            hpoOboPath = path;
        } else {
            hpoOwlPath = path;
        }
    }

    /**
     * Parse the HP ontology file and place the data in {@link #hpoOntology} and
     * @throws PhenolException, OWLOntologyCreationException
     */
    public void parseOntology() throws PhenolException, OWLOntologyCreationException {
        if (isObo) {
            HpOboParser hpoOboParser = new HpOboParser(new File(hpoOboPath));
            logger.debug("ontology path: " + hpoOboPath);
            this.hpoOntology = hpoOboParser.parse();
        } else {
            HpOwlParser hpoOwlParser = new HpOwlParser(new File(hpoOwlPath));
            this.hpoOntology = hpoOwlParser.parse();
        }
    }

    private void initTermMaps() {
        if (hpoOntology !=null) {
            termmap2 = ImmutableMap.copyOf(this.hpoOntology.getTermMap());
            ImmutableMap.Builder<String,Term> termmapBuilder = new ImmutableMap.Builder<>();
            this.hpoOntology.getTerms().forEach(term -> termmapBuilder.put(term.getName(), term));
            termmap = termmapBuilder.build();
        }
    }

    //public Ontology getPhenotypeSubontology() { return ontology.getPhenotypicAbnormalitySubOntology(); }
    //public Ontology getInheritanceSubontology() { return ontology.subOntology(INHERITANCE); }

    public HpoOntology getOntology() {

        return this.hpoOntology;

    }

    /** @return a map will all terms of the Hpo Phenotype subontology. */
    public ImmutableMap<String,Term> getTermMap() {
        if (termmap == null) {
            initTermMaps();
        }
        return termmap;
    }

    public ImmutableMap<TermId,Term> getTermMap2() {
        if (termmap2 == null) {
            initTermMaps();
        }
        return termmap2;
    }


}
