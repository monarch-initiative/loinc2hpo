package org.monarchinitiative.loinc2hpocore.util;



import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.sparql.LoincLongNameComponents;
import org.monarchinitiative.loinc2hpocore.sparql.LoincLongNameParser;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class LoincLongNameComponentsTest {
    static LoincLongNameComponents testclass;

    @BeforeAll
    public static void init() {
        testclass = new LoincLongNameComponents("Erythrocytes distribution width", "blood or serum", "Automated Count", "Ratio");
    }

    @Test
    public void getLoincParameter() throws Exception {
        assertEquals("Erythrocytes distribution width", testclass.getLoincParameter());
    }

    @Test
    public void getLoincTissue() throws Exception {
        assertEquals("blood or serum", testclass.getLoincTissue());
    }

    @Test
    public void getLoincMethod() throws Exception {
        assertEquals("Automated Count", testclass.getLoincMethod());
    }

    @Test
    public void getLoincType() throws Exception {
        assertEquals("Ratio", testclass.getLoincType());
    }

    @Test
    public void keysInLoinParameter() throws Exception {
        Queue<String> results = testclass.keysInLoincParameter();
        assertEquals("Erythrocyte*", results.remove());
        assertEquals("distribution", results.remove());
        assertEquals("width", results.remove());
    }

    @Test
    public void keysInLoincTissue() throws Exception {
        Queue<String> results = testclass.keysInLoincTissue();
        assertEquals("blood", results.remove());
        assertEquals("serum", results.remove());
    }

    @Test
    public void testALoinc() {
        String aLoinc = "Erythrocyte distribution width [Ratio] by Automated count";
        assertEquals("Erythrocyte distribution width", LoincLongNameParser.parse(aLoinc).getLoincParameter());
        LoincLongNameComponents loincLongName = LoincLongNameParser.parse(aLoinc);
        Queue<String> queue = loincLongName.keysInLoincParameter();
        assertEquals(3,queue.size());
        Set<String> items = new HashSet<>(queue);
        assertTrue(items.contains("Erythrocyte"));
        assertTrue(items.contains("distribution"));
        assertTrue(items.contains("width"));
        // no tissue, so no keys available for tissue
        assertEquals("", loincLongName.getLoincTissue());
        assertTrue(loincLongName.keysInLoincTissue().isEmpty());
    }

    /**
     * The processing of the LOINC long name should not return 42 as a possible key
     */
    @Test
    public void testLongNameWithNumberLoinc() {
        String aLoinc = "Erythrocyte distribution 42 width [Ratio] by Automated count";
        assertEquals("Erythrocyte distribution 42 width", LoincLongNameParser.parse(aLoinc).getLoincParameter());
        LoincLongNameComponents loincLongName = LoincLongNameParser.parse(aLoinc);
        // expect to see "Erythrocyte", "distribution", "width", with 42 filtered out
        Queue<String> queue = loincLongName.keysInLoincParameter();
        assertEquals(3,queue.size());
        Set<String> items = new HashSet<>(queue);
        assertTrue(items.contains("Erythrocyte"));
        assertTrue(items.contains("distribution"));
        assertTrue(items.contains("width"));
        assertFalse(items.contains("42"));
        // no tissue, so no keys available for tissue
        assertEquals("", loincLongName.getLoincTissue());
        assertTrue(loincLongName.keysInLoincTissue().isEmpty());
    }


}