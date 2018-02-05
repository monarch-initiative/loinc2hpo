package org.monarchinitiative.loinc2hpo.codesystems;

import org.hl7.fhir.dstu3.model.codesystems.V3ObservationInterpretation;
import org.junit.Test;

import static org.junit.Assert.*;

public class CodeSystemConvertorTest {
    @Test
    public void testAddCodeSystems() throws Exception {
        assertNotNull(CodeSystemConvertor.getCodeContainer());
        assertNotNull(CodeSystemConvertor.getCodeContainer().getCodeSystemMap());
        assertEquals(2, CodeSystemConvertor.getCodeContainer().getCodeSystemMap().size());
        assertEquals(7, CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get("http://jax.org/loinc2hpo").size());
        assertEquals(39, CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get("http://hl7.org/fhir/v2/0078").size());

    }

    @Test
    public void testAddMappingData() throws Exception{
        assertEquals(39, CodeSystemConvertor.getCodeConversionMap().size());
    }

    @Test
    public void convertToInternalCode() throws Exception {

        Code v2 = Code.getNewCode().setSystem("http://hl7.org/fhir/v2/0078").setCode("POS");
        Code internal = CodeSystemConvertor.convertToInternalCode(v2);
        assertEquals("http://jax.org/loinc2hpo", internal.getSystem());
        assertEquals("P", internal.getCode());
        assertEquals("present", internal.getDisplay());

        Code v2_1 = Code.getNewCode().setSystem("http://hl7.org/fhir/v2/0078").setCode("W");
        Code internal2 = CodeSystemConvertor.convertToInternalCode(v2_1);
        assertEquals("http://jax.org/loinc2hpo", internal2.getSystem());
        assertNotEquals("N", internal2.getCode());
        assertNotEquals("normal", internal2.getDisplay());

        Code v2_2 = Code.getNewCode().setSystem("http://hl7.org/fhir/v2/0078").setCode("WR");
        Code internal3 = CodeSystemConvertor.convertToInternalCode(v2_2);
        assertEquals("http://jax.org/loinc2hpo", internal2.getSystem());
        assertNotEquals("P", internal2.getCode());
        assertNotEquals("present", internal2.getDisplay());

        Code v2_3 = Code.getNewCode().setSystem("http://hl7.org/fhir/v2/0078").setCode("AA");
        Code internal4 = CodeSystemConvertor.convertToInternalCode(v2_3);
        assertEquals("http://jax.org/loinc2hpo", internal2.getSystem());
        assertEquals("A", internal2.getCode());
        assertEquals("abnormal", internal2.getDisplay());

    }

    /**
    @Test
    public void convertToInternalCodeTest2() throws Exception{
        assertNotNull(CodeSystemConvertor.getCodeConversionMap());
        assertEquals(11, CodeSystemConvertor.getCodeConversionMap().size());
        CodeSystemConvertor.getCodeConversionMap().entrySet()
                .forEach(x -> System.out.println(x.getKey().getSystem() + " " + x.getKey().getCode() + " " + x.getKey().getDisplay()
                        + " : " + x.getValue().getSystem() + " " + x.getValue().getCode() + " " + x.getValue().getDisplay()));
        Coding v3Code;
        Coding internal;

        V3ObservationInterpretation v3value;
        Loinc2HPOCodedValue internalvalue;

        //v3: N    internal: N
        v3value = V3ObservationInterpretation.fromCode("N");
        v3Code = new Coding(v3value.getSystem(), v3value.toCode(), v3value.getDisplay());
        System.out.println(v3Code.getSystem() + " " + v3Code.getCode() + " " + v3Code.getDisplay());
        internal = CodeSystemConvertor.convertToInternalCode(v3Code);
        //assertNotNull(internal);

    }
**/
}