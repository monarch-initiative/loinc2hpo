package org.monarchinitiative.loinc2hpo.util;



import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


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
        Queue<String> results = testclass.keysInLoinParameter();
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
        assertEquals("", LoincLongNameParser.parse(aLoinc).getLoincTissue());
        System.out.println(LoincLongNameParser.parse(aLoinc).keysInLoincTissue().size());
        for (String tissue : LoincLongNameParser.parse(aLoinc).keysInLoincTissue()) {
            System.out.println(tissue);
        }
        assertTrue(LoincLongNameParser.parse(aLoinc).keysInLoincTissue().isEmpty());
    }


}