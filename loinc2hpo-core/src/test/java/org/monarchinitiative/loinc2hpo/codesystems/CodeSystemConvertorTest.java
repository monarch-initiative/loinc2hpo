package org.monarchinitiative.loinc2hpo.codesystems;

import org.hl7.fhir.dstu3.model.codesystems.V3ObservationInterpretation;
import org.junit.Test;

import static org.junit.Assert.*;

public class CodeSystemConvertorTest {
    @Test
    public void initV3toInternalCodeMap() throws Exception {
        assertNotNull(CodeSystemConvertor.getV3toInternalCodeMap());
        assertEquals(11, CodeSystemConvertor.getV3toInternalCodeMap().size());
    }

    @Test
    public void convertToInternalCode() throws Exception {

        V3ObservationInterpretation v3code;
        Loinc2HPOCodedValue internal;

        //v3: N    internal: N
        v3code = V3ObservationInterpretation.fromCode("N");
        internal = CodeSystemConvertor.convertToInternalCode(v3code);
        assertEquals("N", internal.toCode());

        //v3: HH    internal: H
        v3code = V3ObservationInterpretation.fromCode("HH");
        internal = CodeSystemConvertor.convertToInternalCode(v3code);
        assertEquals("H", internal.toCode());

    }

}