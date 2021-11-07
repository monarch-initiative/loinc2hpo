package org.monarchinitiative.loinc2hpocore.annotation;

import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class Hpo2Outcome {

    private final TermId hpoTermId;
    private final Outcome outcome;


    public Hpo2Outcome(TermId id, Outcome outcome) {
        this.hpoTermId =id;
        this.outcome = outcome;
    }


    public TermId getHpoId() {return hpoTermId; }

    public Outcome outcome() { return outcome; }

    public ShortCode shortCode() { return outcome.getCode(); }
}
