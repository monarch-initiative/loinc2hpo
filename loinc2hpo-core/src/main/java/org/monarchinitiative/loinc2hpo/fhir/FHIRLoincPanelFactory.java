package org.monarchinitiative.loinc2hpo.fhir;

import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

public interface FHIRLoincPanelFactory {
    FHIRLoincPanel createFhirLoincPanel(LoincId loincId);

    FHIRLoincPanel createFhirLoincPanel(LoincId loincId, Patient patient);
}
