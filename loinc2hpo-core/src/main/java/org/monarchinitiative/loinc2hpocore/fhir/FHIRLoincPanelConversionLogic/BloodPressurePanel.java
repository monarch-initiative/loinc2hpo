package org.monarchinitiative.loinc2hpocore.fhir.FHIRLoincPanelConversionLogic;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.exceptions.FHIRException;
import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.codesystems.InternalCode;
import org.monarchinitiative.loinc2hpocore.codesystems.InternalCodeSystem;
import org.monarchinitiative.loinc2hpocore.exception.*;
import org.monarchinitiative.loinc2hpocore.fhir.FHIRLoincPanelImpl;
import org.monarchinitiative.loinc2hpocore.fhir.FhirObservationAnalyzer;
import org.monarchinitiative.loinc2hpocore.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.loinc2hpocore.testresult.LabTestOutcome;

public class BloodPressurePanel extends FHIRLoincPanelImpl {

    public BloodPressurePanel(LoincId loincId) {
        super(loincId);
    }

    public BloodPressurePanel(LoincId loincId, Patient subject) {
        super(loincId, subject);
    }

    @Override
    public HpoTerm4TestOutcome getHPOforObservation() throws MalformedLoincCodeException, MissingPanelComponentException, FHIRException, ReferenceNotFoundException, LoincCodeNotAnnotatedException, AmbiguousResultsFoundException, UnrecognizedCodeException, LoincCodeNotFoundException, AnnotationNotFoundException, UnsupportedCodingSystemException, AmbiguousReferenceException {
        if (components.size() < 2) {
            return null;
        }
        //Logic: call hypertension only when at least one is high
        Observation systolic = components.get(new LoincId("8480-6"));
        Observation dystolic = components.get(new LoincId("8462-4"));
        if (systolic == null || dystolic == null) {
            throw new MissingPanelComponentException();
        }
        LabTestOutcome systolicOutcome = FhirObservationAnalyzer.getHPO4ObservationOutcome(systolic);
        LabTestOutcome dystolicOutcome = FhirObservationAnalyzer.getHPO4ObservationOutcome(dystolic);
        //"Elevated systolic blood pressure" or "Elevated diastolic blood pressure"
        if (systolicOutcome.getOutcome().getId().getValue().equals("HP:0004421") ||
                dystolicOutcome.getOutcome().getId().getValue().equals("HP:0005117")){
            Code high = InternalCodeSystem.getCode(InternalCode.H);
            return loincAnnotationMap.get(panelId).loincInterpretationToHPO(high);
        }
        //"Hypotension"
        if (systolicOutcome.getOutcome().getId().getValue().equals("HP:0002615") &&
                dystolicOutcome.getOutcome().getId().getValue().equals("HP:0002615")) {
            Code low = InternalCodeSystem.getCode(InternalCode.L);
            return loincAnnotationMap.get(panelId).loincInterpretationToHPO(low);
        }
        else {
            Code normal = InternalCodeSystem.getCode(InternalCode.N);
            return loincAnnotationMap.get(panelId).loincInterpretationToHPO(normal);
        }
    }
}
