package org.monarchinitiative.loinc2hpo.testresult;


import org.monarchinitiative.phenol.ontology.data.TermId;

public interface LabTestResultInHPO {


    public TermId getTermId();

    public boolean isNegated();

}
