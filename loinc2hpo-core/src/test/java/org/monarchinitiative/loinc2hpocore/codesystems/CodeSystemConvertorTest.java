package org.monarchinitiative.loinc2hpocore.codesystems;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.exception.InternalCodeNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class CodeSystemConvertorTest {

    private final static CodeSystemConvertor codeSystemConvertor = new CodeSystemConvertor();

    @Test
    public void convertPosToInternalCode() throws InternalCodeNotFoundException {
        Code v2 = Code.getNewCode().setSystem("http://hl7.org/fhir/v2/0078").setCode("POS");
        Code internal = codeSystemConvertor.convertToInternalCode(v2);
        assertEquals(InternalCodeSystem.SYSTEMNAME, internal.getSystem());
        assertEquals("POS", internal.getCode());
        assertEquals("present", internal.getDisplay());
    }

    @Test
    public void convertWToInternalCode() throws InternalCodeNotFoundException {
        Code v2_1 = Code.getNewCode().setSystem("http://hl7.org/fhir/v2/0078").setCode("W");
        Code internal2 = codeSystemConvertor.convertToInternalCode(v2_1);
        assertEquals(InternalCodeSystem.SYSTEMNAME, internal2.getSystem());
        assertNotEquals("N", internal2.getCode());
        assertNotEquals("normal", internal2.getDisplay());
        Code v2_2 = Code.getNewCode().setSystem("http://hl7.org/fhir/v2/0078").setCode("WR");
        assertEquals(InternalCodeSystem.SYSTEMNAME, internal2.getSystem());
        assertNotEquals("POS", internal2.getCode());
        assertNotEquals("present", internal2.getDisplay());


    }

    @Test
    public void convertAAToInternalCode() throws InternalCodeNotFoundException {
        Code v2_1 = Code.getNewCode().setSystem("http://hl7.org/fhir/v2/0078").setCode("W");
        Code internal2 = codeSystemConvertor.convertToInternalCode(v2_1);
        Code v2_3 = Code.getNewCode().setSystem("http://hl7.org/fhir/v2/0078").setCode("AA");
        assertEquals(InternalCodeSystem.SYSTEMNAME, internal2.getSystem());
        assertEquals("A", internal2.getCode());
        assertEquals("abnormal", internal2.getDisplay());
    }



}