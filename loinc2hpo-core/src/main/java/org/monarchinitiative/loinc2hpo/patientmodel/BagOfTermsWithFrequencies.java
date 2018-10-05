package org.monarchinitiative.loinc2hpo.patientmodel;

import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class models patient HP phenotypes with a list of HPO terms and their counts.
 * A infer function allows inferrence with the hierarchy of HPO.
 */

public class BagOfTermsWithFrequencies implements InferWithHPOHierarchy{

    private String patientId;
    private Map<TermId, Integer> termCounts;
    private Map<TermId, Integer> inferred;
    private Ontology hpo;

    public BagOfTermsWithFrequencies(String patientId) {
        this.patientId = patientId;
        this.termCounts = new LinkedHashMap<>();
    }

    public BagOfTermsWithFrequencies(String patientId, Map<TermId, Integer> termCounts) {
        this.patientId = patientId;
        this.termCounts = termCounts;
    }

    public void addTerm(TermId term, int count) {
        if (termCounts.containsKey(term)) {
            termCounts.put(term, termCounts.get(term) + count);
        } else {
            termCounts.put(term, count);
        }
    }

    @Override
    public void infer() {
        this.inferred = new LinkedHashMap<>(termCounts);
        /**
         * For every term, find its ancestors (not including original term) and update their counts with the counts of current term.
         */
        termCounts.entrySet().forEach(entry -> {
            Set<TermId> ancesstors = OntologyAlgorithm.getAncestorTerms(hpo, entry.getKey(), false);
            ancesstors.forEach(t -> {
                inferred.putIfAbsent(t, 0);
                inferred.put(t, inferred.get(t) + entry.getValue());
            });
        });
    }
}
