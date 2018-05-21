package org.monarchinitiative.loinc2hpo.loinc;



import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.Serializable;

public class HpoTerm4TestOutcome implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean isNegated=false;
    private final TermId tid; //this is a flawed design. change to HpoTerm
    private Term hpoTerm;

    @Deprecated
    public HpoTerm4TestOutcome(TermId id) {
        this.tid=id;
    }

    @Deprecated
    public HpoTerm4TestOutcome(TermId id, boolean negated) {
        this(id);
        isNegated=negated;
    }

    //this is the prefered constructor
    public HpoTerm4TestOutcome(Term hpoTerm, boolean negated) {

        this.tid = hpoTerm.getId();
        this.hpoTerm = hpoTerm;
        this.isNegated = negated;
    }

    @Deprecated
    public TermId getId() {return tid; }

    public boolean isNegated() {
        return isNegated;
    }

    public Term getHpoTerm() {
        return this.hpoTerm;
    }





}
