package org.monarchinitiative.loinc2hpocore.annotationmodel;

import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class Hpo2Outcome {

    private final boolean isNegated;
    private final TermId tid;
    private final ShortCode outcome;


    public Hpo2Outcome(TermId id, ShortCode outcome) {
        this.tid=id;
        this.isNegated = false;
        this.outcome = outcome;
    }


    public TermId getId() {return tid; }

    public boolean isNegated() {
        return isNegated;
    }

    public ShortCode outcome() { return outcome; }
}
