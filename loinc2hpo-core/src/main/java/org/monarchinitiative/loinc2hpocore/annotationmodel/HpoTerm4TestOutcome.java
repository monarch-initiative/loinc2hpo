package org.monarchinitiative.loinc2hpocore.annotationmodel;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.Serializable;

public class HpoTerm4TestOutcome implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean isNegated;
    private final TermId tid;


    public HpoTerm4TestOutcome(TermId id) {
        this.tid=id;
        this.isNegated = false;
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
