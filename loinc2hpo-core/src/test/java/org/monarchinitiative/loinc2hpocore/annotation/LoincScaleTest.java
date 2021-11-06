package org.monarchinitiative.loinc2hpocore.annotation;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoincScaleTest {

    @Test
    public void testQn() {
        LoincScale scale = LoincScale.QUANTITATIVE;
        assertEquals("Qn", scale.shortName());
    }

    @Test
    public void testOrdQn() {
        LoincScale scale = LoincScale.fromString("OrdQn");
        assertEquals(LoincScale.OrdQn, scale);
    }
}
