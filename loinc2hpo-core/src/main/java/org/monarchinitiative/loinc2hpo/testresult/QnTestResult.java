package org.monarchinitiative.loinc2hpo.testresult;

import com.github.phenomics.ontolib.ontology.data.TermId;
import org.monarchinitiative.loinc2hpo.loinc.HpoTermId4LoincTest;
import org.monarchinitiative.loinc2hpo.loinc.LoincObservationResult;

public class QnTestResult implements TestResult {

    HpoTermId4LoincTest hpoId;

    LoincObservationResult observation;

    String comment;

    public QnTestResult(HpoTermId4LoincTest id, LoincObservationResult obs, String text) {

        hpoId=id;
        observation=obs;
        comment=text;
    }

    @Override
    public TermId getTermId() { return hpoId.getId(); }

    @Override
    public String toString() {
        if (hpoId==null) {
            return "error => hpoId is null in testResult";
        }
        if (hpoId.getId()==null) {
            return "error => hpoId.getId() is null in testResult";
        }
        if (observation==null)
            return "error => observation is null in TestResult";
        return String.format("QnTestResult: %s [%s; %s]", hpoId.getId().getIdWithPrefix(),observation.toString(),comment!=null?comment:"");

    }


    @Override
    public boolean isNegated() { return hpoId.isNegated(); }


}
