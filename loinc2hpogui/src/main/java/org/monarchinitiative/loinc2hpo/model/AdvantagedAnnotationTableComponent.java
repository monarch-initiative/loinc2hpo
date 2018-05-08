package org.monarchinitiative.loinc2hpo.model;

import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;

/**
 * This class is for data visualization in GUI only. It simply keeps a record of a code and its corresponding hpoTerm4TestOutcome term
 * wrapped in HpoTerm4TestOutcome
 */
public class AdvantagedAnnotationTableComponent {
    private Code code;
    private String hpo_term; //this is the hpoTerm4TestOutcome term name. No need for this one because it can be accessed from the following
    private HpoTerm4TestOutcome hpoTerm4TestOutcome;

    public String getHpo_term() {
        return hpo_term;
    }

    public void setHpo_term(String hpo_term) {
        this.hpo_term = hpo_term;
    }

    @Deprecated
    public AdvantagedAnnotationTableComponent(Code code, String hpo_term, HpoTerm4TestOutcome hpoTerm4TestOutcome) {
        this.code = code;
        this.hpo_term = hpo_term;
        this.hpoTerm4TestOutcome = hpoTerm4TestOutcome;
    }

    //This is the preferred constructor.
    public AdvantagedAnnotationTableComponent(Code code, HpoTerm4TestOutcome hpoTerm4TestOutcome) {
        this.code = code;
        if (hpoTerm4TestOutcome != null )
            this.hpo_term = hpoTerm4TestOutcome.getHpoTerm().getName();
        this.hpoTerm4TestOutcome = hpoTerm4TestOutcome;
    }


    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public HpoTerm4TestOutcome getHpoTerm4TestOutcome() {
        return hpoTerm4TestOutcome;
    }

    public void setHpoTerm4TestOutcome(HpoTerm4TestOutcome hpoTerm4TestOutcome) {
        this.hpoTerm4TestOutcome = hpoTerm4TestOutcome;
    }

    @Override
    public String toString() {
        if (this.hpoTerm4TestOutcome != null) {
            String negate = this.hpoTerm4TestOutcome.isNegated() ? "not " : "";
            return this.code.getSystem() + ": " + this.code.getCode() + " -> " + negate + this.hpoTerm4TestOutcome.getHpoTerm().getName();

        } else {
            return this.code.getSystem() + ": " + this.code.getCode() + " -> No annotation";
        }
    }
}
