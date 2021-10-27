package org.monarchinitiative.loinc2hpocore.codesystems;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeTest {


    @Test
    public void getCode() {
        Code code =  Code.fromSystemAndCode("http://jax.org/loinc2hpo", "H");
        assertEquals("H", code.getCode());
        assertEquals("http://jax.org/loinc2hpo", code.getSystem());
    }

    @Test
    public void getDisplay() {
        Code code =  Code.fromSystemCodeAndDisplay("http://jax.org/loinc2hpo",
                "H", "above normal");
        assertEquals("above normal", code.getDisplay());
    }


    @Test
    public void testEqualityOfCodeObjects() {
        String system = "http://jax.org/loinc2hpo";
        String systemJaxOnly = "http://jax.org/loinc2hpo";
        String codeHigh = "H";
        String display = "Above normal";
        String codeNormal = "N";
        Code code1 = Code.fromSystemCodeAndDisplay(system, codeHigh, display);
        Code code2 = Code.fromSystemCodeAndDisplay(system, codeHigh, display);
        assertEquals(code1, code2);
        Code code3 = Code.fromSystemCodeAndDisplay(system, codeHigh, display);
        assertEquals(code1, code3);
        Code code4 = Code.fromSystemCodeAndDisplay(system, codeNormal, display);
        assertNotEquals(code1, code4);
        Code code5 = Code.fromSystemCodeAndDisplay(systemJaxOnly, codeHigh, display);
       // assertNotEquals(code1, code5);
    }



    @Test
    public void testtoString() {
        String system = "http://jax.org/loinc2hpo";
        String codeHigh = "H";
        String display = "Above normal";
        Code code1 = Code.fromSystemCodeAndDisplay(system, codeHigh, display);
        assertEquals("System: http://jax.org/loinc2hpo; Code: H, Display: Above normal", code1.toString());

    }
}