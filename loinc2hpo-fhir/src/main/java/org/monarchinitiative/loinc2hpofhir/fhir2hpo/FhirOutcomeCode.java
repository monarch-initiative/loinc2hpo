package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;

import java.util.Map;

// TODO add other FHIR CODES
public class FhirOutcomeCode {

    private static Map<String, ShortCode> fhir2shortcodeMap;

    static {
        fhir2shortcodeMap.put("A", ShortCode.A);
        fhir2shortcodeMap.put("L", ShortCode.L);
        fhir2shortcodeMap.put("N", ShortCode.N);
        fhir2shortcodeMap.put("H", ShortCode.H);
        fhir2shortcodeMap.put("NP", ShortCode.ABSENT);
        fhir2shortcodeMap.put("P", ShortCode.PRESENT);
        fhir2shortcodeMap.put("U", ShortCode.U);
    }

    public static ShortCode fhir2shortcode(String fhirOutcome) {
        return fhir2shortcodeMap.getOrDefault(fhirOutcome, ShortCode.U);
    }
}
