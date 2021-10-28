package org.monarchinitiative.loinc2hpocore.legacy.patientmodel;

import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class models patient HP phenotypes with a list of HPO terms and their counts.
 * An infer function allows inference with the hierarchy of HPO.
 */

public class BagOfTermsWithFrequencies implements InferWithHPOHierarchy{

    private final String patientId;
    private final Map<TermId, Integer> termCounts;
    private Map<TermId, Integer> inferred;
    private final Ontology hpo;

    public BagOfTermsWithFrequencies(String patientId, Ontology hpo) {
        this.patientId = patientId;
        this.termCounts = new LinkedHashMap<>();
        this.hpo = hpo;
    }

    public BagOfTermsWithFrequencies(String patientId, Map<TermId, Integer> termCounts, Ontology hpo) {
        this.patientId = patientId;
        this.termCounts = termCounts;
        this.hpo = hpo;
    }

    public void addTerm(TermId termId, int count) {
        if (termCounts.containsKey(termId)) {
            termCounts.put(termId, termCounts.get(termId) + count);
        } else {
            termCounts.put(termId, count);
        }
    }

    public String getPatientId() {
        return this.patientId;
    }

    public Map<TermId, Integer> getOriginalTermCounts() {
        return new LinkedHashMap<>(termCounts);
    }

    public Map<TermId, Integer> getInferredTermCounts() {
        return new LinkedHashMap<>(inferred);
    }

    @Override
    public void infer() {
        //clone the list of terms so that inferred terms can be added
        this.inferred = new LinkedHashMap<>(termCounts);

        //For every term, find its ancestors (not including original term) and update their counts with the counts of current term.
        termCounts.forEach((key, value) -> {
            Set<TermId> ancesstors = OntologyAlgorithm.getAncestorTerms(hpo, key, false);
            ancesstors.forEach(t -> {
                inferred.putIfAbsent(t, 0);
                inferred.put(t, inferred.get(t) + value);
            });
        });
    }

    @Override
    public String toString() {
        int count_ori = this.termCounts.size();
        int count_infer = this.inferred.size();
        return String.format("Patient id: %s\n" +
                "original terms: %d\n" +
                "after infer: %d",
                this.patientId, count_ori, count_infer);
    }

}
