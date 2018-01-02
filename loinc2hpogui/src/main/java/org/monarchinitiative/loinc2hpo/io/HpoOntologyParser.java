package org.monarchinitiative.loinc2hpo.io;


import com.github.phenomics.ontolib.formats.hpo.HpoOntology;
import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.github.phenomics.ontolib.formats.hpo.HpoTermRelation;
import com.github.phenomics.ontolib.io.obo.hpo.HpoOboParser;
import com.github.phenomics.ontolib.ontology.data.*;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
    private final  TermId INHERITANCE = new ImmutableTermId(pref,"0000005");
    HpoOntology ontology;

    /** Map of all of the Phenotypic abnormality terms (i.e., not the inheritance terms). */


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

    public Ontology<HpoTerm, HpoTermRelation> getPhenotypeSubontology() { return ontology.getPhenotypicAbnormalitySubOntology(); }
    public Ontology<HpoTerm, HpoTermRelation> getInheritanceSubontology() { return ontology.subOntology(INHERITANCE); }
    public HpoOntology getOntology() { return ontology; }

    /** @return a map will all terms of the Hpo Phenotype subontology. */
    public ImmutableMap<String,HpoTerm> getTermMap() {
        ImmutableMap.Builder<String,HpoTerm> termmap = new ImmutableMap.Builder<>();
        if (ontology !=null) {

            // ontology.getTermMap().values().  forEach(term -> termmap.put(term.getName(), term));
            // for some reason there is a bug here...issue #34 on ontolib tracker
            // here is a workaround to remove duplicate entries
            List<HpoTerm> res = ontology.getTermMap().values().stream().distinct()
                    .collect(Collectors.toList());

            res.forEach( term -> termmap.put(term.getName(),term));
        }
        return termmap.build();
    }


}
