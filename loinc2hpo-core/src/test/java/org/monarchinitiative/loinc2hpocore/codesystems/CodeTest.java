package org.monarchinitiative.loinc2hpocore.codesystems;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeTest {
    @Test
    public void systemGetterAndSetter() throws Exception {
        Code code = new Code();
        code.setSystem("http://jax.org/loinc2hpo");
        assertNotNull(code.getSystem());
        assertEquals("http://jax.org/loinc2hpo", code.getSystem());
    }

    @Test
    public void getCode() {
        Code code = new Code();
        code.setCode("H");
        assertNotNull(code.getCode());
        assertEquals("H", code.getCode());
    }

    @Test
    public void getDisplay() {
        Code code = new Code();
        code.setDisplay("above normal");
        assertNotNull(code.getDisplay());
        assertEquals("above normal", code.getDisplay());
    }


    @Test
    public void equals() {
        Code code1 = Code.getNewCode().setSystem("http://jax.org/loinc2hpo").setCode("H").setDisplay("Above normal");
        Code code2 = Code.getNewCode().setSystem("http://jax.org/loinc2hpo").setCode("H").setDisplay("Above normal");
        assertEquals(code1, code2);
        Code code3 = Code.getNewCode().setSystem("http://jax.org/loinc2hpo").setCode("H");
        assertEquals(code1, code3);
        Code code4 = Code.getNewCode().setSystem("http://jax.org/loinc2hpo").setCode("N").setDisplay("Above normal");
        assertNotEquals(code1, code4);
        Code code5 = Code.getNewCode().setSystem("http://jax.org").setCode("H").setDisplay("Above normal");
        assertNotEquals(code1, code5);
        Code code6 = Code.getNewCode().setSystem("http://jax.org/loinc2hpo").setCode("H").setDisplay("high");
        assertEquals(code1, code6);
    }

    @Test
    public void testhashCode() {
        Code code1 = Code.getNewCode().setSystem("http://jax.org/loinc2hpo").setCode("H").setDisplay("Above normal");
        Code code2 = Code.getNewCode().setSystem("http://jax.org/loinc2hpo").setCode("H").setDisplay("Above normal");
        assertEquals(code1.hashCode(), code2.hashCode());
        Code code3 = Code.getNewCode().setSystem("http://jax.org/loinc2hpo").setCode("H");
        assertEquals(code1.hashCode(), code3.hashCode());
        Code code4 = Code.getNewCode().setSystem("http://jax.org/loinc2hpo").setCode("N").setDisplay("Above normal");
        assertNotEquals(code1.hashCode(), code4.hashCode());
        Code code5 = Code.getNewCode().setSystem("http://jax.org").setCode("H").setDisplay("Above normal");
        assertNotEquals(code1.hashCode(), code5.hashCode());
        Code code6 = Code.getNewCode().setSystem("http://jax.org/loinc2hpo").setCode("H").setDisplay("high");
        assertEquals(code1.hashCode(), code6.hashCode());

    }

    @Test
    public void testtoString() {
        Code code1 = Code.getNewCode().setSystem("http://jax.org/loinc2hpo").setCode("H").setDisplay("Above normal");
        assertEquals("System: http://jax.org/loinc2hpo; Code: H, Display: Above normal", code1.toString());

    }
}