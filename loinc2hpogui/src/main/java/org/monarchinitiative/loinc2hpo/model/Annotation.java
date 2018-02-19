package org.monarchinitiative.loinc2hpo.model;

import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.loinc.HpoTermId4LoincTest;

/**
 * This class is for data visualization in GUI only. It simply keeps a record of a code and its corresponding hpoTermId4LoincTest term
 * wrapped in HpoTermId4LoincTest
 */
public class Annotation {
    private Code code;
    private String hpo_term; //this is the hpoTermId4LoincTest term name. No need for this one because it can be accessed from the following
    private HpoTermId4LoincTest hpoTermId4LoincTest;

    public String getHpo_term() {
        return hpo_term;
    }

    public void setHpo_term(String hpo_term) {
        this.hpo_term = hpo_term;
    }

    @Deprecated
    public Annotation(Code code, String hpo_term, HpoTermId4LoincTest hpoTermId4LoincTest) {
        this.code = code;
        this.hpo_term = hpo_term;
        this.hpoTermId4LoincTest = hpoTermId4LoincTest;
    }

    //This is the preferred constructor.
    public Annotation(Code code, HpoTermId4LoincTest hpoTermId4LoincTest) {
        this.code = code;
        this.hpo_term = hpoTermId4LoincTest.getHpoTerm().getName();
        this.hpoTermId4LoincTest = hpoTermId4LoincTest;
    }


    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public HpoTermId4LoincTest getHpoTermId4LoincTest() {
        return hpoTermId4LoincTest;
    }

    public void setHpoTermId4LoincTest(HpoTermId4LoincTest hpoTermId4LoincTest) {
        this.hpoTermId4LoincTest = hpoTermId4LoincTest;
    }
}
