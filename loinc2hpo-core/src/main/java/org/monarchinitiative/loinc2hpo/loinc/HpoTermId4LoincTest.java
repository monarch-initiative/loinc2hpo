package org.monarchinitiative.loinc2hpo.loinc;


import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.github.phenomics.ontolib.ontology.data.TermId;

import java.io.Serializable;

public class HpoTermId4LoincTest implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean isNegated=false;
    private final TermId tid; //this is a flawed design. change to HpoTerm
    private HpoTerm hpoTerm;

    @Deprecated
    public HpoTermId4LoincTest(TermId id) {
        this.tid=id;
    }

    @Deprecated
    public HpoTermId4LoincTest(TermId id, boolean negated) {
        this(id);
        isNegated=negated;
    }

    //this is the prefered constructor
    public HpoTermId4LoincTest(HpoTerm hpoTerm, boolean negated) {

        this.tid = hpoTerm.getId();
        this.hpoTerm = hpoTerm;
        this.isNegated = negated;
    }


    public TermId getId() {return tid; }

    public boolean isNegated() {
        return isNegated;
    }

    public HpoTerm getHpoTerm() {
        return this.hpoTerm;
    }





}
