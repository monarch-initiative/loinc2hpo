package org.monarchinitiative.loinc2hpo.codesystems;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class CodeContainerTest {

    @BeforeEach
    public void resetSingleton() throws NoSuchFieldException, IllegalAccessException {
        Field instance = CodeContainer.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void getInstance() {
        CodeContainer codeContainer1 = CodeContainer.getInstance();
        CodeContainer codeContainer2 = CodeContainer.getInstance();
        assertEquals(codeContainer1, codeContainer2);
    }

    @Test
    public void add() {
        CodeContainer codeContainer = CodeContainer.getInstance();
        Code code1 = Code.getNewCode().setSystem("http://test").setDisplay("testdisplay").setCode("testcode");
        assertNotNull(codeContainer.getCodeSystemMap());
        codeContainer.add(code1);
        assertEquals(1, codeContainer.getCodeSystemMap().size());
        assertEquals(1, codeContainer.getCodeSystemMap().get("http://test").size());
        Code code2 = Code.getNewCode().setSystem("http://test").setCode("testcode2");
        codeContainer.add(code2);
        assertEquals(2, codeContainer.getCodeSystemMap().get("http://test").size());
        Code code3 = Code.getNewCode().setSystem("http://testsystem2").setCode("testcode3");
        codeContainer.add(code3);
        assertEquals(2, codeContainer.getCodeSystemMap().size());
        assertEquals(1, codeContainer.getCodeSystemMap().get("http://testsystem2").size());
        Code code4 = Code.getNewCode().setSystem("http://testsystem3").setCode("testcode4");
        codeContainer.add(code4);
        assertEquals(3, codeContainer.getCodeSystemMap().size());
        Code code5 = Code.getNewCode().setSystem("http://test").setCode("testcode5");
        codeContainer.add(code5);
        assertEquals(3, codeContainer.getCodeSystemMap().get("http://test").size());
    }


}