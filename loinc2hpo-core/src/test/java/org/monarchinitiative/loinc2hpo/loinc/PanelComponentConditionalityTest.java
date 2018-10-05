package org.monarchinitiative.loinc2hpo.loinc;

import org.junit.Test;

import static org.junit.Assert.*;

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