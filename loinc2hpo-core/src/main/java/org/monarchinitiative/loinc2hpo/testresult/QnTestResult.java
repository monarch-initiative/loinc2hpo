package org.monarchinitiative.loinc2hpo.testresult;

import com.github.phenomics.ontolib.ontology.data.TermId;
import org.monarchinitiative.loinc2hpo.loinc.Hpo2LoincTermId;
import org.monarchinitiative.loinc2hpo.loinc.LoincObservation;

public class QnTestResult implements TestResult {

    Hpo2LoincTermId hpoId;

    LoincObservation observation;

    String comment;

    public QnTestResult(Hpo2LoincTermId id, LoincObservation obs, String text) {

        hpoId=id;
        observation=obs;
        comment=text;
    }

    @Override
    public TermId getTermId() { return hpoId.getId(); }

    @Override
    public String toString() {
        return String.format("%s [%s; %s]", hpoId.getId().getIdWithPrefix(),observation.toString(),comment);

    }


    @Override
    public boolean isNegated() { return hpoId.isNegated(); }


}
