package org.monarchinitiative.loinc2hpo.testresult;

import com.github.phenomics.ontolib.ontology.data.TermId;

public interface LabTestResultInHPO {


    public TermId getTermId();

    public boolean isNegated();

}
