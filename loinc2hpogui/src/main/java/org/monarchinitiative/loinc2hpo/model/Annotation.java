package org.monarchinitiative.loinc2hpo.model;

import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.loinc.HpoTermId4LoincTest;

/**
 * This class is for data visualization in GUI only. It simply keeps a record of a code and its corresponding hpo term
 * wrapped in HpoTermId4LoincTest
 */
public class Annotation {
    private Code code;
    private String hpo_term;
    private HpoTermId4LoincTest hpo;

    public String getHpo_term() {
        return hpo_term;
    }

    public void setHpo_term(String hpo_term) {
        this.hpo_term = hpo_term;
    }

    public Annotation(Code code, String hpo_term, HpoTermId4LoincTest hpo) {
        this.code = code;
        this.hpo_term = hpo_term;

        this.hpo = hpo;
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public HpoTermId4LoincTest getHpo() {
        return hpo;
    }

    public void setHpo(HpoTermId4LoincTest hpo) {
        this.hpo = hpo;
    }
}
