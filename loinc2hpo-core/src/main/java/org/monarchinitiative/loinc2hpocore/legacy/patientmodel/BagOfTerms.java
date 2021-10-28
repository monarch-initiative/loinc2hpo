package org.monarchinitiative.loinc2hpocore.legacy.patientmodel;

import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * This class models a patient with a bag of HP terms.
 * An infer function allows inference with the Human Phenotype Ontology to get the ancestors of terms.
 */
public class BagOfTerms implements InferWithHPOHierarchy {

    private final String patient;
    private final Ontology hpo;
    private final Set<TermId> terms;
    private Set<TermId> terms_inferred;

    public BagOfTerms(String patient, Ontology hp) {
        this.patient = patient;
        this.hpo = hp;
        this.terms = new LinkedHashSet<>();
        this.terms_inferred = new LinkedHashSet<>(this.terms);
    }

    public BagOfTerms(String patient, Set<TermId> hpterms, Ontology hp) {
        this.patient = patient;
        this.terms = Objects.requireNonNull(hpterms);
        this.hpo = hp;
        this.terms_inferred = new LinkedHashSet<>(this.terms);
    }

    public String getPatient() {
        return this.patient;
    }

    public Set<TermId> getOriginalTerms() {
        return new LinkedHashSet<>(this.terms);
    }

    public Set<TermId> getInferedTerms() {
        return new LinkedHashSet<>(this.terms_inferred);
    }

    public void addTerm (TermId term) {
        terms.add(term);
    }

    @Override
    public void infer() {
        terms_inferred = new LinkedHashSet<>(terms);
        terms.forEach(t -> {
            Set<TermId> ancestors = OntologyAlgorithm.getAncestorTerms(this.hpo, t, false);
            terms_inferred.addAll(ancestors);
        });
    }
}
