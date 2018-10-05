package org.monarchinitiative.loinc2hpo.io;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.owl.OwlImmutableOntologyLoader;
import org.monarchinitiative.phenol.ontology.data.*;

import java.io.File;

/**
 * This class parses the Human Phenotype Ontology in the owl format.
 */

public class HpOwlParser {

    private final File owlFile;

    private final boolean debug;

    public HpOwlParser(File owlFile, boolean debug) {
        this.owlFile = owlFile;
        this.debug = debug;
    }

    public HpOwlParser(File owlFile) {
        this(owlFile,false);
    }

    public HpoOntology parse() throws PhenolException {
        Ontology ontology;

        final OwlImmutableOntologyLoader loader = new OwlImmutableOntologyLoader(owlFile);
        ontology = loader.load();
        if (debug) {
            System.err.println(String.format("Parsed a total of %d HP terms",ontology.countAllTerms()));
        }

        // hpo root termid
        TermId hpoRoot = new TermId(new TermPrefix("HP"), "0000001");

        return new HpoOntology(
                (ImmutableSortedMap<String, String>) ontology.getMetaInfo(),
                ontology.getGraph(),
                hpoRoot,
                ontology.getNonObsoleteTermIds(),
                ontology.getObsoleteTermIds(),
                (ImmutableMap<TermId, Term>) ontology.getTermMap(),
                (ImmutableMap<Integer, Relationship>) ontology.getRelationMap());
    }

}
