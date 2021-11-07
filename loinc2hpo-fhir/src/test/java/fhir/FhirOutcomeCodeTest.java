package fhir;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.FhirOutcomeCode;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FhirOutcomeCodeTest {

    @Test
    public void low() {
        ShortCode low = ShortCode.L;
        String fhirOffScaleLow = "L";
        assertEquals(low, FhirOutcomeCode.fhir2shortcode(fhirOffScaleLow));
    }

    @Test
    public void offScaleLow() {
        ShortCode low = ShortCode.L;
        String fhirOffScaleLow = "<";
        assertEquals(low, FhirOutcomeCode.fhir2shortcode(fhirOffScaleLow));
    }
    @Test
    public void criticalLow() {
        ShortCode low = ShortCode.L;
        String fhirOffScaleLow = "LL";
        assertEquals(low, FhirOutcomeCode.fhir2shortcode(fhirOffScaleLow));
    }

    @Test
    public void significantLow() {
        ShortCode low = ShortCode.L;
        String fhirOffScaleLow = "LU";
        assertEquals(low, FhirOutcomeCode.fhir2shortcode(fhirOffScaleLow));
    }

    @Test
    public void high() {
        ShortCode high = ShortCode.H;
        String fhirOffScaleHigh = "H";
        assertEquals(high, FhirOutcomeCode.fhir2shortcode(fhirOffScaleHigh));
    }

    @Test
    public void offScaleHigh() {
        ShortCode high = ShortCode.H;
        String fhirOffScaleHigh = ">";
        assertEquals(high, FhirOutcomeCode.fhir2shortcode(fhirOffScaleHigh));
    }


    @Test
    public void criticallyHigh() {
        ShortCode high = ShortCode.H;
        String fhirOffScaleHigh = "HH";
        assertEquals(high, FhirOutcomeCode.fhir2shortcode(fhirOffScaleHigh));
    }

    @Test
    public void significantlyHigh() {
        ShortCode high = ShortCode.H;
        String fhirOffScaleHigh = "HU";
        assertEquals(high, FhirOutcomeCode.fhir2shortcode(fhirOffScaleHigh));
    }


    @Test
    public void abnormal() {
        ShortCode pos = ShortCode.POS;
        String fhirAbnormal = "A";
        assertEquals(pos, FhirOutcomeCode.fhir2shortcode(fhirAbnormal));
    }

    @Test
    public void criticallyAbnormal() {
        ShortCode pos = ShortCode.POS;
        String fhirCriticallyAbnormal = "AA";
        assertEquals(pos, FhirOutcomeCode.fhir2shortcode(fhirCriticallyAbnormal));
    }

    @Test
    public void positive() {
        ShortCode pos = ShortCode.POS;
        String fhirPositive = "POS";
        assertEquals(pos, FhirOutcomeCode.fhir2shortcode(fhirPositive));
    }

    @Test
    public void detected() {
        ShortCode pos = ShortCode.POS;
        String fhirDetected = "DET";
        assertEquals(pos, FhirOutcomeCode.fhir2shortcode(fhirDetected));
    }

    @Test
    public void negative() {
        ShortCode neg = ShortCode.NEG;
        String fhirNotDetected = "NEG";
        assertEquals(neg, FhirOutcomeCode.fhir2shortcode(fhirNotDetected));
    }

    @Test
    public void notDetected() {
        ShortCode neg = ShortCode.NEG;
        String fhirNotDetected = "ND";
        assertEquals(neg, FhirOutcomeCode.fhir2shortcode(fhirNotDetected));
    }


}
