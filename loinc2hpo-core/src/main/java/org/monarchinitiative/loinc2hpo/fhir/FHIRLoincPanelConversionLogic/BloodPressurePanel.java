package org.monarchinitiative.loinc2hpo.fhir.FHIRLoincPanelConversionLogic;

import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpo.ResourceCollection;
import org.monarchinitiative.loinc2hpo.fhir.FHIRLoincPanelImpl;
import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

public class BloodPressurePanel extends FHIRLoincPanelImpl {

    public BloodPressurePanel(LoincId loincId, ResourceCollection resourceCollection) {
        super(loincId, resourceCollection);
    }

    public BloodPressurePanel(LoincId loincId, Patient subject, ResourceCollection resourceCollection) {
        super(loincId, subject, resourceCollection);
    }

    @Override
    public HpoTerm4TestOutcome getHPOforObservation() throws Exception {
        if (components.size() < 2) {
            return null;
        }

        //@TODO: convert two required LOINC tests to HPO
        //Logic: call hypertension only when both are high
        return null;
    }
}
