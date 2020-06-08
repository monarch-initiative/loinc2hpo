package org.monarchinitiative.loinc2hpocore.fhir2hpo;

import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

public interface FHIRLoincPanelFactory {
    FHIRLoincPanel createFhirLoincPanel(LoincId loincId);

    FHIRLoincPanel createFhirLoincPanel(LoincId loincId, Patient patient);
}
