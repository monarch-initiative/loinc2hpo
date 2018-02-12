package org.monarchinitiative.loinc2hpo.loinc;


import com.github.phenomics.ontolib.ontology.data.TermId;

import java.io.Serializable;

public class HpoTermId4LoincTest implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean isNegated=false;
    private final TermId tid;

    public HpoTermId4LoincTest(TermId id) {
        this.tid=id;
    }

    public HpoTermId4LoincTest(TermId id, boolean negated) {
        this(id);
        isNegated=negated;
    }


    public TermId getId() {return tid; }

    public boolean isNegated() {
        return isNegated;
    }





}
