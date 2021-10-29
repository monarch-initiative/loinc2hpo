package org.monarchinitiative.loinc2hpocore.legacy;

import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Convert FHIR outcode codes to HPO2LOINC output codes. Code are taken from
 * http://hl7.org/fhir/v2/0078	display	FHIR. Note that we do not attempt to
 * find a correspondence for all codes, but instead 'translate' codes that have an
 * exact or close match in the LOINC2HPO framework.
 * @author Peter N Robinson
 */
public class FhirCodeSystemConvertor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FhirCodeSystemConvertor.class);
    private final Map<String, String> fhirCodeToLabelMap;
    private final Map<String, ShortCode> fhirCodeToOutcomeMap;

    public FhirCodeSystemConvertor(){
        fhirCodeToLabelMap = new HashMap<>();
        fhirCodeToOutcomeMap = new HashMap<>();
        fhirCodeToLabelMap.put("<", "Off scale low");
        fhirCodeToOutcomeMap.put("<", ShortCode.L);
        fhirCodeToLabelMap.put(">", "Off scale high");
        fhirCodeToOutcomeMap.put(">", ShortCode.H);
        fhirCodeToLabelMap.put("A", "Abnormal");
        fhirCodeToOutcomeMap.put("A", ShortCode.A);
        fhirCodeToLabelMap.put("AA", "Critically abnormal");
        fhirCodeToOutcomeMap.put("AA", ShortCode.A);
        fhirCodeToLabelMap.put("DET", "Detected");
        fhirCodeToOutcomeMap.put("DET", ShortCode.PRESENT);
        fhirCodeToLabelMap.put("H", "High");
        fhirCodeToOutcomeMap.put("H", ShortCode.H);
        fhirCodeToLabelMap.put("HH", "Critically high");
        fhirCodeToOutcomeMap.put("HH", ShortCode.H);
        fhirCodeToLabelMap.put("HU", "Very high");
        fhirCodeToOutcomeMap.put("HU", ShortCode.H);
        fhirCodeToLabelMap.put("L", "Low");
        fhirCodeToOutcomeMap.put("L", ShortCode.L);
        fhirCodeToLabelMap.put("LL", "Critically low");
        fhirCodeToOutcomeMap.put("LL", ShortCode.L);
        fhirCodeToLabelMap.put("LU", "Very low");
        fhirCodeToOutcomeMap.put("LU", ShortCode.L);
        fhirCodeToLabelMap.put("N", "Normal");
        fhirCodeToOutcomeMap.put("N", ShortCode.N);
        fhirCodeToLabelMap.put("ND", "Not Detected");
        fhirCodeToOutcomeMap.put("N", ShortCode.ABSENT);
        fhirCodeToLabelMap.put("NEG", "Negative");
        fhirCodeToOutcomeMap.put("NEG", ShortCode.ABSENT);
        fhirCodeToLabelMap.put("POS", "Positive");
        fhirCodeToOutcomeMap.put("POS", ShortCode.PRESENT);
    }

    public ShortCode convertToInternalCode(String fhirCode) {
        return fhirCodeToOutcomeMap.getOrDefault(fhirCode, ShortCode.U);
    }


}
