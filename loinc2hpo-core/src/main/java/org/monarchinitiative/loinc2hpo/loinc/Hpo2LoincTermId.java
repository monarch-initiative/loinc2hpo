package org.monarchinitiative.loinc2hpo.loinc;


import com.github.phenomics.ontolib.ontology.data.ImmutableTermId;
import com.github.phenomics.ontolib.ontology.data.TermId;

public class Hpo2LoincTermId  {


    private boolean isNegated=false;
    private final TermId tid;

    public Hpo2LoincTermId(TermId id) {
        this.tid=id;
    }

    public Hpo2LoincTermId(TermId id, boolean negated) {
        this(id);
        isNegated=negated;
    }


    public TermId getId() {return tid; }

    public boolean isNegated() {
        return isNegated;
    }





}
