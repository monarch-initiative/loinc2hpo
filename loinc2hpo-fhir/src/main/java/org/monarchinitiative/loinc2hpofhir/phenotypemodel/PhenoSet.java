package org.monarchinitiative.loinc2hpofhir.phenotypemodel;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Set;

public interface PhenoSet {

    /**
     * A set of hpo terms that are outcomes of same lab tests
     * e.g. a LOINC code has three outcomes, the HPO terms form a phenonet (like a word "synonet" in natural language processing). The purpose of do this is that we can switch HPO terms when a new test outcome comes in. Imaging: a patient was assigned "hyperglycemia" based on a LOINC test a few years ago; now the patient is tested with the same test but the outcome is now normal-- in this case, we shouldn't just add a term to describe the patient, instead we should terminate the previous term and switch to a new term (not "abnormality of blood glucose concentration".
     * @return
     */
    Set<TermId> getSet();

    boolean sameSet(TermId term);

    boolean hasOccurred(TermId term);

    void add(TermId hpoTerm);

}