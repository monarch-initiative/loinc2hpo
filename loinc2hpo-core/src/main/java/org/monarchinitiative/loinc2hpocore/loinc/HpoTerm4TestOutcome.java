package org.monarchinitiative.loinc2hpocore.loinc;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.Serializable;

public class HpoTerm4TestOutcome implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean isNegated=false;
    private final TermId tid;


    public HpoTerm4TestOutcome(TermId id) {
        this.tid=id;
    }

    public HpoTerm4TestOutcome(TermId id, boolean negated) {
        this.tid = id;
        isNegated=negated;
    }

    public TermId getId() {return tid; }

    public boolean isNegated() {
        return isNegated;
    }
}
