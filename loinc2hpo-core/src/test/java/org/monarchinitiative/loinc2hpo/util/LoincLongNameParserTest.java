package org.monarchinitiative.loinc2hpo.util;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.util.LoincCodeClass;
import org.monarchinitiative.loinc2hpo.util.LoincLongNameParser;

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
        System.out.println(loinc.keysInLoinParameter().peek());


    }

    @Test
    public void testString2() {
        String name = "Erythrocyte distribution width [Ratio] by Automated count";
        LoincCodeClass loinc = LoincLongNameParser.parse(name);
        assertEquals("Erythrocyte distribution width", loinc.getLoincParameter());
        assertEquals("", loinc.getLoincTissue());
        assertEquals("Ratio", loinc.getLoincType());
        assertEquals("Automated count", loinc.getLoincMethod());
    }

    @Test
    public void testString3() {
        String name = "Platelet mean volume [Entitic volume] in Blood by Automated count";
        LoincCodeClass loinc = LoincLongNameParser.parse(name);
        assertEquals("Platelet mean volume", loinc.getLoincParameter());
        assertEquals("Blood", loinc.getLoincTissue());
        assertEquals("Entitic volume", loinc.getLoincType());
        assertEquals("Automated count", loinc.getLoincMethod());
    }

    @Test
    public void testString4() {
        String name = "CD3 cells/100 cells in Blood";
        LoincCodeClass loinc = LoincLongNameParser.parse(name);
        assertEquals("CD3 cells/100 cells", loinc.getLoincParameter());
        assertEquals("Blood", loinc.getLoincTissue());
        assertEquals("", loinc.getLoincType());
        assertEquals("", loinc.getLoincMethod());
    }

    @Test
    public void testString5() {
        String name = "Oxygen [Partial pressure] adjusted to patient's actual temperature in Arterial blood";
        LoincCodeClass loinc = LoincLongNameParser.parse(name);
        assertEquals("Oxygen adjusted to patient's actual temperature", loinc.getLoincParameter());
        assertEquals("Arterial blood", loinc.getLoincTissue());
        assertEquals("Partial pressure", loinc.getLoincType());
        assertEquals("", loinc.getLoincMethod());
    }

    @Test
    public void testString6() {
        String name = "Fibrosis score";
        LoincCodeClass loinc = LoincLongNameParser.parse(name);
        assertEquals("Fibrosis score", loinc.getLoincParameter());
        assertEquals("", loinc.getLoincTissue());
        assertEquals("", loinc.getLoincType());
        assertEquals("", loinc.getLoincMethod());
    }

    @Test
    public void testString7() {
        String name = "Microalbumin [Mass/time] in 24 hour Urine by Detection limit <= 1.0 mg/L";
        LoincCodeClass loinc = LoincLongNameParser.parse(name);
        assertEquals("Microalbumin", loinc.getLoincParameter());
        assertEquals("24 hour Urine", loinc.getLoincTissue());
        assertEquals("Mass/time", loinc.getLoincType());
        assertEquals("Detection limit <= 1.0 mg/L", loinc.getLoincMethod());
    }

    @Test
    public void testString8() {
        String name = "Anion gap 3 in Serum or Plasma";
        LoincCodeClass loinc = LoincLongNameParser.parse(name);
        assertEquals("Anion gap 3", loinc.getLoincParameter());
        assertEquals("Serum or Plasma", loinc.getLoincTissue());
        assertEquals("", loinc.getLoincType());
        assertEquals("", loinc.getLoincMethod());
    }

    @Test
    public void testString9() {
        String name = "Thyrotropin [Units/volume] in Serum or Plasma by Detection limit <= 0.05 mIU/L";
        LoincCodeClass loinc = LoincLongNameParser.parse(name);
        assertEquals("Thyrotropin", loinc.getLoincParameter());
        assertEquals("Serum or Plasma", loinc.getLoincTissue());
        assertEquals("Units/volume", loinc.getLoincType());
        assertEquals("Detection limit <= 0.05 mIU/L", loinc.getLoincMethod());
    }

    @Test
    public void testString10() {
        String name = "Cholesterol in HDL [Presence] in Serum or Plasma";
        LoincCodeClass loinc = LoincLongNameParser.parse(name);
        assertEquals("Cholesterol in HDL", loinc.getLoincParameter());
        assertEquals("Serum or Plasma", loinc.getLoincTissue());
        assertEquals("Presence", loinc.getLoincType());
        assertEquals("", loinc.getLoincMethod());
    }

    @Test
    public void testString11() {
        String name = "Appearance of Body fluid";
        LoincCodeClass loinc = LoincLongNameParser.parse(name);
        assertEquals("Appearance", loinc.getLoincParameter());
        assertEquals("Body fluid", loinc.getLoincTissue());
        assertEquals("", loinc.getLoincType());
        assertEquals("", loinc.getLoincMethod());
    }

    @Test
    public void testString12() {
        String name = "Human papilloma virus 16+18+31+33+35+39+45+51+52+56+58+59+68 DNA [Presence] in Cervix by Probe and signal amplification method";
        LoincCodeClass loinc = LoincLongNameParser.parse(name);
        assertEquals("Human papilloma virus 16+18+31+33+35+39+45+51+52+56+58+59+68 DNA", loinc.getLoincParameter());
        assertEquals("Cervix", loinc.getLoincTissue());
        assertEquals("Presence", loinc.getLoincType());
        assertEquals("Probe and signal amplification method", loinc.getLoincMethod());
    }


    @Test
    public void testString13() {
        String name = "pH of Urine by Test strip";
        LoincCodeClass loinc = LoincLongNameParser.parse(name);
        assertEquals("pH", loinc.getLoincParameter());
        assertEquals("Urine", loinc.getLoincTissue());
        assertEquals("", loinc.getLoincType());
        assertEquals("Test strip", loinc.getLoincMethod());
    }

    @Test
    public void testString14() {
        String name = "Appearance of Cerebral spinal fluid";
        LoincCodeClass loinc = LoincLongNameParser.parse(name);
        assertEquals("Appearance", loinc.getLoincParameter());
        assertEquals("Cerebral spinal fluid", loinc.getLoincTissue());
        assertEquals("", loinc.getLoincType());
        assertEquals("", loinc.getLoincMethod());

    }

    @Test
    public void testString15(){
        String name = "little i Ag [Presence] on Red Blood Cells from donor";
        LoincCodeClass loinc = LoincLongNameParser.parse(name);
        assertEquals("little i Ag", loinc.getLoincParameter());
        assertEquals("Red Blood Cells", loinc.getLoincTissue());
        assertEquals("Presence", loinc.getLoincType());
        assertEquals("", loinc.getLoincMethod());

    }

    @Test
    public void testString16(){
        String name = "Fungus identified in Unspecified specimen by Sticky tape for environmental fungus";
        LoincCodeClass loinc = LoincLongNameParser.parse(name);
        assertEquals("Fungus identified", loinc.getLoincParameter());
        assertEquals("Unspecified specimen", loinc.getLoincTissue());
        assertEquals("Sticky tape for environmental fungus", loinc.getLoincMethod());
        assertEquals("", loinc.getLoincType());

    }

    @Test
    public void testString17(){
        String name = "Glucose [Mass/volume] in Serum or Plasma --45 minutes post 50 g lactose PO";
        LoincCodeClass loinc = LoincLongNameParser.parse(name);
        assertEquals("Glucose", loinc.getLoincParameter());
        assertEquals("Serum or Plasma", loinc.getLoincTissue());
        assertEquals("Mass/volume", loinc.getLoincType());
        assertEquals("", loinc.getLoincMethod());
    }

}