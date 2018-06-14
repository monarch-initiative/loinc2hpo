package org.monarchinitiative.loinc2hpo.fhir.FHIRLoincPanelConversionLogic;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpo.Constants;
import org.monarchinitiative.loinc2hpo.ResourceCollection;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.fhir.FHIRLoincPanelImpl;
import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

public class GlasgowComaPanel extends FHIRLoincPanelImpl{

    public GlasgowComaPanel (LoincId loincId, ResourceCollection resourceCollection) {
        super(loincId, resourceCollection);
    }

    public GlasgowComaPanel (LoincId loincId, Patient subject, ResourceCollection resourceCollection){
        super(loincId, subject, resourceCollection);
    }

    @Override
    public HpoTerm4TestOutcome getHPOforObservation() throws Exception {
        if (components.size() < 4){
            return null;
        }
        //@TODO: use total score stored at 9269-2
        Observation target = null;
        for (Observation obs : components) {
            long targetFound = obs.getCode().getCoding().stream()
                    .filter(coding -> coding.getSystem().equals(Constants.LOINCSYSTEM))
                    .map(Coding::getCode)
                    .filter(s -> s.equals("9269-2"))
                    .count();
            if (targetFound == 1) {
                target = obs;
                break;
            }
        }
        if (target == null) {
            return null;
        }

        Code score = new Code(target.getValueCodeableConcept().getCoding().get(0));
        return resourceCollection.annotationMap().get(new LoincId("9269-2")).loincInterpretationToHPO(score);

    }
}
