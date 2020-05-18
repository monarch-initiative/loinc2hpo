package org.monarchinitiative.loinc2hpo.loinc;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PanelComponentConditionalityTest {

    @Test
    public void testValue() {
        assertEquals("R", PanelComponentConditionality.R.toString());
    }

    @Test
    public void testValueOf() {
        assertEquals(PanelComponentConditionality.R, PanelComponentConditionality.valueOf("R"));
    }

}