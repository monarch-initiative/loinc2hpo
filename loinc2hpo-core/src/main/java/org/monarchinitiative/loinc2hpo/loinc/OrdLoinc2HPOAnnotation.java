package org.monarchinitiative.loinc2hpo.loinc;

import com.github.phenomics.ontolib.ontology.data.TermId;
import org.monarchinitiative.loinc2hpo.codesystems.Code;

public class OrdLoinc2HPOAnnotation extends Loinc2HPOAnnotation {

    public OrdLoinc2HPOAnnotation(LoincId lid, LoincScale lsc) {
        super(lid, lsc);
    }

    @Override
    public HpoTermId4LoincTest loincInterpretationToHpo(ObservationResultInInternalCode obs) {
        return null;
    }

    @Override
    public HpoTermId4LoincTest loincInterpretationToHPO(Code code) {
        return null;
    }

    @Override
    public TermId getBelowNormalHpoTermId() {
        return null;
    }

    @Override
    public TermId getAbnormalHpoTermName() {
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
    public String getNote() {
        return null;
    }

    @Override
    public boolean getFlag() {
        return false;
    }
}
