package org.monarchinitiative.loinc2hpofhir.fhir2hpo.FHIRLoincPanelConversionLogic;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.exceptions.FHIRException;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.loinc2hpocore.exception.*;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.FHIRLoincPanelImpl;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Hpo2Outcome;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.loinc2hpofhir.phenotypemodel.LabTestOutcome;

public class BloodPressurePanel extends FHIRLoincPanelImpl {

    public BloodPressurePanel(LoincId loincId) {
        super(loincId);
    }

    public BloodPressurePanel(LoincId loincId, Patient subject) {
        super(loincId, subject);
    }

    @Override
    public Hpo2Outcome getHPOforObservation() throws  FHIRException {
        return null;
        /*
        if (components.size() < 2) {
            return null;
        }
        //Logic: call hypertension only when at least one is high
        Observation systolic = components.get(new LoincId("8480-6"));
        Observation dystolic = components.get(new LoincId("8462-4"));
        if (systolic == null || dystolic == null) {
            throw Loinc2HpoRuntimeException.missingPanelComponent();
        }
        LabTestOutcome systolicOutcome = FhirObservationAnalyzer.getHPO4ObservationOutcome(systolic);
        LabTestOutcome dystolicOutcome = FhirObservationAnalyzer.getHPO4ObservationOutcome(dystolic);
        //"Elevated systolic blood pressure" or "Elevated diastolic blood pressure"
        if (systolicOutcome.getOutcome().getId().getValue().equals("HP:0004421") ||
                dystolicOutcome.getOutcome().getId().getValue().equals("HP:0005117")){
            return loincAnnotationMap.get(panelId).loincInterpretationToHPO(ShortCode.H);
        }
        //"Hypotension"
        if (systolicOutcome.getOutcome().getId().getValue().equals("HP:0002615") &&
                dystolicOutcome.getOutcome().getId().getValue().equals("HP:0002615")) {
            return loincAnnotationMap.get(panelId).loincInterpretationToHPO(ShortCode.L);
        }
        else {
            return loincAnnotationMap.get(panelId).loincInterpretationToHPO(ShortCode.N);
        }

         */
    }
}
