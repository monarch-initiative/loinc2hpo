package org.monarchinitiative.loinc2hpo.fhir.FHIRLoincPanelConversionLogic;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.exceptions.FHIRException;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;
import org.monarchinitiative.loinc2hpo.exception.*;
import org.monarchinitiative.loinc2hpo.fhir.FHIRLoincPanelImpl;
import org.monarchinitiative.loinc2hpo.fhir.FhirObservationAnalyzer;
import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.testresult.LabTestOutcome;

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
            Code high = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM).get("H");
            return loincAnnotationMap.get(panelId).loincInterpretationToHPO(high);
        }
        //"Hypotension"
        if (systolicOutcome.getOutcome().getId().getValue().equals("HP:0002615") &&
                dystolicOutcome.getOutcome().getId().getValue().equals("HP:0002615")) {
            Code low = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM).get("L");
            return loincAnnotationMap.get(panelId).loincInterpretationToHPO(low);
        }
        else {
            Code normal = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM).get("N");
            return loincAnnotationMap.get(panelId).loincInterpretationToHPO(normal);
        }
    }
}
