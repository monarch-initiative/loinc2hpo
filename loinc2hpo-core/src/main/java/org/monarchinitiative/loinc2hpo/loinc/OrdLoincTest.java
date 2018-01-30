package org.monarchinitiative.loinc2hpo.loinc;

import com.github.phenomics.ontolib.ontology.data.TermId;

public class OrdLoincTest extends LoincTest {

    public OrdLoincTest(LoincId lid, LoincScale lsc) {
        super(lid, lsc);
    }

    @Override
    public HpoTermId4LoincTest loincInterpretationToHpo(LoincObservationResult obs) {
        return null;
    }

    @Override
    public TermId getBelowNormalHpoTermId() {
        return null;
    }

    @Override
    public TermId getNotAbnormalHpoTermName() {
        return null;
    }

    @Override
    public TermId getAboveNormalHpoTermName() {
        return null;
    }

    @Override
    public TermId getCorrespondingHpoTermName() {
        return null;
    }

    @Override
    public String getNote() {
        return null;
    }

    @Override
    public boolean getFlag() {
        return false;
    }
}