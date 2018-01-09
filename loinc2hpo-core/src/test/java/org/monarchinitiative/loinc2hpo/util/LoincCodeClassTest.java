package org.monarchinitiative.loinc2hpo.util;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.util.LoincCodeClass;
import org.monarchinitiative.loinc2hpo.util.LoincLongNameParser;

import java.util.List;
import java.util.Queue;

import static org.junit.Assert.*;

public class LoincCodeClassTest {
    static LoincCodeClass testclass;

    @BeforeClass
    public static void init() {
        testclass = new LoincCodeClass("Erythrocytes distribution width", "blood or serum", "Automated Count", "Ratio");
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
        Assert.assertEquals("Erythrocyte distribution width", LoincLongNameParser.parse(aLoinc).getLoincParameter());
        assertEquals("", LoincLongNameParser.parse(aLoinc).getLoincTissue());
        System.out.println(LoincLongNameParser.parse(aLoinc).keysInLoincTissue().size());
        for (String tissue : LoincLongNameParser.parse(aLoinc).keysInLoincTissue()) {
            System.out.println(tissue);
        }
        assertEquals(true, LoincLongNameParser.parse(aLoinc).keysInLoincTissue().isEmpty());
    }


}