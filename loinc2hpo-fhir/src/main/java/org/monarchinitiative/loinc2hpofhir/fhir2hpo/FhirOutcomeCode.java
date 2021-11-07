package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;

import java.util.HashMap;
import java.util.Map;

// TODO add other FHIR CODES
public class FhirOutcomeCode {

    private static final Map<String, ShortCode> fhir2shortcodeMap;

    static {
        fhir2shortcodeMap = new HashMap<>();
        fhir2shortcodeMap.put("A", ShortCode.POS); // Abnormal, usually for non-numeric results
        fhir2shortcodeMap.put("AA", ShortCode.POS);
        fhir2shortcodeMap.put("L", ShortCode.L);
        fhir2shortcodeMap.put("LL", ShortCode.L);
        fhir2shortcodeMap.put("LU", ShortCode.L);
        fhir2shortcodeMap.put("H", ShortCode.H);
        fhir2shortcodeMap.put("HH", ShortCode.H);
        fhir2shortcodeMap.put("HU", ShortCode.H);
        fhir2shortcodeMap.put(">", ShortCode.H);
        fhir2shortcodeMap.put("<", ShortCode.L); // < is off-scale low
        fhir2shortcodeMap.put("N", ShortCode.N);
        fhir2shortcodeMap.put("NEG", ShortCode.NEG);
        fhir2shortcodeMap.put("ND", ShortCode.NEG);
        fhir2shortcodeMap.put("POS", ShortCode.POS);
        fhir2shortcodeMap.put("DET", ShortCode.POS);
        fhir2shortcodeMap.put("U", ShortCode.U);
    }

    public static ShortCode fhir2shortcode(String fhirOutcome) {
        return fhir2shortcodeMap.getOrDefault(fhirOutcome, ShortCode.U);
    }
}
