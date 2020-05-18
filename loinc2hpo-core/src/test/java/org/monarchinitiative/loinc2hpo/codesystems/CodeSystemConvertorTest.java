package org.monarchinitiative.loinc2hpo.codesystems;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class CodeSystemConvertorTest {
    @Test
    public void testAddCodeSystems() {
        assertNotNull(CodeSystemConvertor.getCodeContainer());
        assertNotNull(CodeSystemConvertor.getCodeContainer().getCodeSystemMap());
        assertEquals(2, CodeSystemConvertor.getCodeContainer().getCodeSystemMap().size());
        assertEquals(7, CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM).size());
        assertEquals(39, CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get("http://hl7.org/fhir/v2/0078").size());

    }

    @Test
    public void testAddMappingData() {
        assertEquals(39, CodeSystemConvertor.getCodeConversionMap().size());
    }

    @Test
    public void convertToInternalCode() throws Exception {

        Code v2 = Code.getNewCode().setSystem("http://hl7.org/fhir/v2/0078").setCode("POS");
        Code internal = CodeSystemConvertor.convertToInternalCode(v2);
        assertEquals(Loinc2HPOCodedValue.CODESYSTEM, internal.getSystem());
        assertEquals("POS", internal.getCode());
        assertEquals("present", internal.getDisplay());

        Code v2_1 = Code.getNewCode().setSystem("http://hl7.org/fhir/v2/0078").setCode("W");
        Code internal2 = CodeSystemConvertor.convertToInternalCode(v2_1);
        assertEquals(Loinc2HPOCodedValue.CODESYSTEM, internal2.getSystem());
        assertNotEquals("N", internal2.getCode());
        assertNotEquals("normal", internal2.getDisplay());

        Code v2_2 = Code.getNewCode().setSystem("http://hl7.org/fhir/v2/0078").setCode("WR");
        Code internal3 = CodeSystemConvertor.convertToInternalCode(v2_2);
        assertEquals(Loinc2HPOCodedValue.CODESYSTEM, internal2.getSystem());
        assertNotEquals("POS", internal2.getCode());
        assertNotEquals("present", internal2.getDisplay());

        Code v2_3 = Code.getNewCode().setSystem("http://hl7.org/fhir/v2/0078").setCode("AA");
        Code internal4 = CodeSystemConvertor.convertToInternalCode(v2_3);
        assertEquals(Loinc2HPOCodedValue.CODESYSTEM, internal2.getSystem());
        assertEquals("A", internal2.getCode());
        assertEquals("abnormal", internal2.getDisplay());

    }
}