package org.monarchinitiative.loinc2hpo.testresult;

import com.github.phenomics.ontolib.ontology.data.TermId;
import org.monarchinitiative.loinc2hpo.loinc.HpoTermId4LoincTest;
import org.monarchinitiative.loinc2hpo.loinc.LoincObservationResult;

/**
 * This class represents the final outcome for a lab test.
 * It keeps record of the HPO term for the result, and
 */
public class BasicLabTestResultInHPO implements LabTestResultInHPO {

    HpoTermId4LoincTest hpoId;

    LoincObservationResult observation;

    String comment;

    public BasicLabTestResultInHPO(HpoTermId4LoincTest id, LoincObservationResult obs, String text) {

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
            return "error => observation is null in LabTestResultInHPO";
        return String.format("BasicLabTestResultInHPO: %s [%s; %s]", hpoId.getId().getIdWithPrefix(),observation.toString(),comment!=null?comment:"");

    }


    @Override
    public boolean isNegated() { return hpoId.isNegated(); }


}
