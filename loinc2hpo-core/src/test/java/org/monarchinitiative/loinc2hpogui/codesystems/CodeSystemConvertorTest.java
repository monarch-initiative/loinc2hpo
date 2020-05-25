package org.monarchinitiative.loinc2hpogui.codesystems;


import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpocore.codesystems.InternalCodeSystem;

import static org.junit.jupiter.api.Assertions.*;


public class CodeSystemConvertorTest {

    @Test
    public void convertToInternalCode() throws Exception {

        CodeSystemConvertor codeSystemConvertor = new CodeSystemConvertor();

        Code v2 = Code.getNewCode().setSystem("http://hl7.org/fhir/v2/0078").setCode("POS");
        Code internal = codeSystemConvertor.convertToInternalCode(v2);
        assertEquals(InternalCodeSystem.SYSTEMNAME, internal.getSystem());
        assertEquals("POS", internal.getCode());
        assertEquals("present", internal.getDisplay());

        Code v2_1 = Code.getNewCode().setSystem("http://hl7.org/fhir/v2/0078").setCode("W");
        Code internal2 = codeSystemConvertor.convertToInternalCode(v2_1);
        assertEquals(InternalCodeSystem.SYSTEMNAME, internal2.getSystem());
        assertNotEquals("N", internal2.getCode());
        assertNotEquals("normal", internal2.getDisplay());

        Code v2_2 = Code.getNewCode().setSystem("http://hl7.org/fhir/v2/0078").setCode("WR");
        Code internal3 = codeSystemConvertor.convertToInternalCode(v2_2);
        assertEquals(InternalCodeSystem.SYSTEMNAME, internal2.getSystem());
        assertNotEquals("POS", internal2.getCode());
        assertNotEquals("present", internal2.getDisplay());

        Code v2_3 = Code.getNewCode().setSystem("http://hl7.org/fhir/v2/0078").setCode("AA");
        Code internal4 = codeSystemConvertor.convertToInternalCode(v2_3);
        assertEquals(InternalCodeSystem.SYSTEMNAME, internal2.getSystem());
        assertEquals("A", internal2.getCode());
        assertEquals("abnormal", internal2.getDisplay());

    }
}