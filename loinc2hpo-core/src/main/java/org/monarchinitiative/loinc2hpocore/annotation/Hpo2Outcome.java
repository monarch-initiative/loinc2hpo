package org.monarchinitiative.loinc2hpocore.annotation;

import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class Hpo2Outcome {

    private final boolean isNegated;
    private final TermId hpoTermId;
    private final ShortCode outcome;


    public Hpo2Outcome(TermId id, ShortCode outcome) {
        this.hpoTermId =id;
        this.isNegated = false;
        this.outcome = outcome;
    }


    public TermId getHpoId() {return hpoTermId; }

    public boolean isNegated() {
        return isNegated;
    }

    public ShortCode outcome() { return outcome; }
}
