package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;



public class FhirObservationUtil {
    /**
     * A method to get the loinc id from a FHIR observation
     */
    public static LoincId getLoincIdOfObservation(Observation observation) {
        LoincId loincId = null;
        for (Coding coding : observation.getCode().getCoding()) {
            if (coding.getSystem().equals("http://loinc.org")) {
                loincId = new LoincId(coding.getCode());
            }
        }
        if (loincId == null) throw Loinc2HpoRuntimeException.loincCodeNotFound();
        return loincId;
    }



}
