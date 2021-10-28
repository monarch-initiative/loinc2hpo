package org.monarchinitiative.loinc2hpofhir.fhir2hpo.FHIRLoincPanelConversionLogic;

import org.apache.commons.lang.NotImplementedException;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.FHIRLoincPanelImpl;
import org.monarchinitiative.loinc2hpocore.annotationmodel.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

public class GlasgowComaPanel extends FHIRLoincPanelImpl {

    public GlasgowComaPanel (LoincId loincId) {
        super(loincId);
    }

    public GlasgowComaPanel (LoincId loincId, Patient subject){
        super(loincId, subject);
    }

    @Override
    public HpoTerm4TestOutcome getHPOforObservation() throws Exception {
        if (components.size() < 4){
            return null;
        }
        //@TODO: use total score stored at 9269-2
        Observation target = components.get(new LoincId("9269-2"));
//        for (Observation obs : components) {
//            long targetFound = obs.getCode().getCoding().stream()
//                    .filter(coding -> coding.getSystem().equals(Constants.LOINCSYSTEM))
//                    .map(Coding::getCode)
//                    .filter(s -> s.equals("9269-2"))
//                    .count();
//            if (targetFound == 1) {
//                target = obs;
//                break;
//            }
//        }
        if (target == null) {
            return null;
        }

      //  Code score = new Code(target.getValueCodeableConcept().);
       // return loincAnnotationMap.get(new LoincId("9269-2")).loincInterpretationToHPO(score);
        throw new NotImplementedException("TODO NOT IMPLEMENTED");
    }
}
