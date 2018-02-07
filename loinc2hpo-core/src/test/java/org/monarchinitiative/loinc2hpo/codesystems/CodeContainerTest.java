package org.monarchinitiative.loinc2hpo.codesystems;

import org.junit.Test;

import static org.junit.Assert.*;

public class CodeContainerTest {
    @Test
    public void getInstance() throws Exception {
        CodeContainer codeContainer1 = CodeContainer.getInstance();
        CodeContainer codeContainer2 = CodeContainer.getInstance();
        assertEquals(codeContainer1, codeContainer2);
    }

    @Test
    public void add() throws Exception {
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