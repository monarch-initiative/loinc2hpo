package org.monarchinitiative.loinc2hpocore.fhir2hpo;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.loinc2hpocore.exception.LoincCodeNotFoundException;
import org.monarchinitiative.loinc2hpocore.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpocore.exception.UnsupportedCodingSystemException;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;



public class FhirObservationUtil {
    /**
     * A method to get the loinc id from a FHIR observation
     * @return
     * @throws MalformedLoincCodeException
     * @throws LoincCodeNotFoundException
     * @throws UnsupportedCodingSystemException
     */
    public static LoincId getLoincIdOfObservation(Observation observation) throws MalformedLoincCodeException, LoincCodeNotFoundException{
        LoincId loincId = null;
        for (Coding coding : observation.getCode().getCoding()) {
            if (coding.getSystem().equals("http://loinc.org")) {
                loincId = new LoincId(coding.getCode());
            }
        }
        if (loincId == null) throw new LoincCodeNotFoundException();
        return loincId;
    }



}
