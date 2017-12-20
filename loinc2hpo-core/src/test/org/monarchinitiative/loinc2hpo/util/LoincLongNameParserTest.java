package org.monarchinitiative.loinc2hpo.util;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LoincLongNameParserTest {

    @Test
    public void testString1() {
        String name = "Potassium [Moles/volume] in Serum or Plasma";
        LoincCodeClass loinc = LoincLongNameParser.parse(name);
        assertEquals("Potassium", loinc.getLoincParameter());
        assertEquals("Serum or Plasma", loinc.getLoincTissue());
        assertEquals("Moles/volume", loinc.getLoincType());
        assertEquals("", loinc.getLoincMethod());


    }

    /**
    @Test
    public void testString2() {
        String name = "Erythrocyte distribution width [Ratio] by Automated count";
        LoincLongNameParser parser = new LoincLongNameParser(name);
        assertEquals("Erythrocyte distribution width", parser.getLoincParameter());
        assertEquals("", parser.getLoincTissue());
        assertEquals("Ratio", parser.getLoincType());
        assertEquals("Automated count", parser.getLoincMethod());
    }

    @Test
    public void testString3() {
        String name = "Platelet mean volume [Entitic volume] in Blood by Automated count";
        LoincLongNameParser parser = new LoincLongNameParser(name);
        assertEquals("Platelet mean volume", parser.getLoincParameter());
        assertEquals("Blood", parser.getLoincTissue());
        assertEquals("Entitic volume", parser.getLoincType());
        assertEquals("Automated count", parser.getLoincMethod());
    }

    @Test
    public void testString4() {
        String name = "CD3 cells/100 cells in Blood";
        LoincLongNameParser parser = new LoincLongNameParser(name);
        assertEquals("CD3 cells/100 cells", parser.getLoincParameter());
        assertEquals("Blood", parser.getLoincTissue());
        assertEquals("", parser.getLoincType());
        assertEquals("", parser.getLoincMethod());
    }

    @Test
    public void testString5() {
        String name = "Oxygen [Partial pressure] adjusted to patient's actual temperature in Arterial blood";
        LoincLongNameParser parser = new LoincLongNameParser(name);
        assertEquals("Oxygen adjusted to patient's actual temperature", parser.getLoincParameter());
        assertEquals("Arterial blood", parser.getLoincTissue());
        assertEquals("Partial pressure", parser.getLoincType());
        assertEquals("", parser.getLoincMethod());
    }

    @Test
    public void testString6() {
        String name = "Fibrosis score";
        LoincLongNameParser parser = new LoincLongNameParser(name);
        assertEquals("Fibrosis score", parser.getLoincParameter());
        assertEquals("", parser.getLoincTissue());
        assertEquals("", parser.getLoincType());
        assertEquals("", parser.getLoincMethod());
    }

    @Test
    public void testString7() {
        String name = "Microalbumin [Mass/time] in 24 hour Urine by Detection limit <= 1.0 mg/L";
        LoincLongNameParser parser = new LoincLongNameParser(name);
        assertEquals("Microalbumin", parser.getLoincParameter());
        assertEquals("24 hour Urine", parser.getLoincTissue());
        assertEquals("Mass/time", parser.getLoincType());
        assertEquals("Detection limit <= 1.0 mg/L", parser.getLoincMethod());
    }

    @Test
    public void testString8() {
        String name = "Anion gap 3 in Serum or Plasma";
        LoincLongNameParser parser = new LoincLongNameParser(name);
        assertEquals("Anion gap 3", parser.getLoincParameter());
        assertEquals("Serum or Plasma", parser.getLoincTissue());
        assertEquals("", parser.getLoincType());
        assertEquals("", parser.getLoincMethod());
    }

    @Test
    public void testString9() {
        String name = "Thyrotropin [Units/volume] in Serum or Plasma by Detection limit <= 0.05 mIU/L";
        LoincLongNameParser parser = new LoincLongNameParser(name);
        assertEquals("Thyrotropin", parser.getLoincParameter());
        assertEquals("Serum or Plasma", parser.getLoincTissue());
        assertEquals("Units/volume", parser.getLoincType());
        assertEquals("Detection limit <= 0.05 mIU/L", parser.getLoincMethod());
    }

    @Test
    public void testString10() {
        String name = "Cholesterol in HDL [Presence] in Serum or Plasma";
        LoincLongNameParser parser = new LoincLongNameParser(name);
        assertEquals("Cholesterol in HDL", parser.getLoincParameter());
        assertEquals("Serum or Plasma", parser.getLoincTissue());
        assertEquals("Presence", parser.getLoincType());
        assertEquals("", parser.getLoincMethod());
    }

    @Test
    public void testString11() {
        String name = "Appearance of Body fluid";
        LoincLongNameParser parser = new LoincLongNameParser(name);
        assertEquals("Appearance", parser.getLoincParameter());
        assertEquals("Body fluid", parser.getLoincTissue());
        assertEquals("", parser.getLoincType());
        assertEquals("", parser.getLoincMethod());
    }

    @Test
    public void testString12() {
        String name = "Human papilloma virus 16+18+31+33+35+39+45+51+52+56+58+59+68 DNA [Presence] in Cervix by Probe and signal amplification method";
        LoincLongNameParser parser = new LoincLongNameParser(name);
        assertEquals("Human papilloma virus 16+18+31+33+35+39+45+51+52+56+58+59+68 DNA", parser.getLoincParameter());
        assertEquals("Cervix", parser.getLoincTissue());
        assertEquals("Presence", parser.getLoincType());
        assertEquals("Probe and signal amplification method", parser.getLoincMethod());
    }


    @Test
    public void testString13() {
        String name = "pH of Urine by Test strip";
        LoincLongNameParser parser = new LoincLongNameParser(name);
        assertEquals("pH", parser.getLoincParameter());
        assertEquals("Urine", parser.getLoincTissue());
        assertEquals("", parser.getLoincType());
        assertEquals("Test strip", parser.getLoincMethod());
    }

    @Test
    public void testString14() {
        String name = "Appearance of Cerebral spinal fluid";
        LoincLongNameParser parser = new LoincLongNameParser(name);
        assertEquals("Appearance", parser.getLoincParameter());
        assertEquals("Cerebral spinal fluid", parser.getLoincTissue());
        assertEquals("", parser.getLoincType());
        assertEquals("", parser.getLoincMethod());

    }
    **/

}