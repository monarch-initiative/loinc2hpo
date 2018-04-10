package org.monarchinitiative.loinc2hpo.io;



import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.formats.hpo.HpoRelationship;
import org.monarchinitiative.phenol.formats.hpo.HpoTerm;
import org.monarchinitiative.phenol.io.obo.hpo.HpoOboParser;
import org.monarchinitiative.phenol.ontology.data.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


/**
 * This class uses the <a href="https://github.com/phenomics/ontolib">ontolb</a> library to
 * parse both the {@code hp.obo} file and the phenotype annotation file
 * {@code phenotype_annotation.tab}
 * (see <a href="http://human-phenotype-ontology.github.io/">HPO Homepage</a>).
 * @author Peter Robinson
 * @author Vida Ravanmehr
 * @version 0.1.1 (2017-11-15)
 */
public class HpoOntologyParser {
    private static final Logger logger = LogManager.getLogger();
    /** Path to the {@code hp.obo} file. */
    private String hpoOntologyPath=null;

    private TermPrefix pref = new ImmutableTermPrefix("HP");
    private final TermId INHERITANCE = new ImmutableTermId(pref,"0000005");
    HpoOntology ontology;

    /** Map of all of the Phenotypic abnormality terms (i.e., not the inheritance terms). */
    private ImmutableMap<String, HpoTerm> termmap;
    private ImmutableMap<TermId, HpoTerm> termmap2;


    public HpoOntologyParser(String path){

        hpoOntologyPath=path;

    }

    /**
     * Parse the HP ontology file and place the data in {@link #ontology} and
     * @throws IOException
     */
    public void parseOntology() throws IOException {
        HpoOboParser hpoOboParser = new HpoOboParser(new File(hpoOntologyPath));
        this.ontology = hpoOboParser.parse();
    }

    private void initTermMaps() {
        ImmutableMap.Builder<String,HpoTerm> termmapBuilder = new ImmutableMap.Builder<>();
        ImmutableMap.Builder<TermId,HpoTerm> termmap2Builder = new ImmutableMap.Builder<>();
        if (ontology !=null) {

            // ontology.getTermMap().values().  forEach(term -> termmap.put(term.getName(), term));
            // for some reason there is a bug here...issue #34 on ontolib tracker
            // here is a workaround to remove duplicate entries
            List<HpoTerm> res = ontology.getTermMap().values().stream().distinct()
                    .collect(Collectors.toList());

            res.forEach( term -> termmapBuilder.put(term.getName(),term));
            termmap = termmapBuilder.build();
            res.forEach( term -> termmap2Builder.put(term.getId(), term));
            termmap2 = termmap2Builder.build();
            //res.forEach( term -> logger.info(term.getName()));
        }
    }

    public Ontology<HpoTerm, HpoRelationship> getPhenotypeSubontology() { return ontology.getPhenotypicAbnormalitySubOntology(); }
    public Ontology<HpoTerm, HpoRelationship> getInheritanceSubontology() { return ontology.subOntology(INHERITANCE); }
    public HpoOntology getOntology() { return ontology; }

    /** @return a map will all terms of the Hpo Phenotype subontology. */
    public ImmutableMap<String,HpoTerm> getTermMap() {
        if (termmap == null) {
            initTermMaps();
        }
        return termmap;
    }

    public ImmutableMap<TermId,HpoTerm> getTermMap2() {
        if (termmap2 == null) {
            initTermMaps();
        }
        return termmap2;
    }


}
