package org.monarchinitiative.loinc2hpo.loinc;

import org.junit.Test;
import org.monarchinitiative.loinc2hpo.exception.MaformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

import static org.junit.Assert.assertEquals;

public class LoincIdTest {


    @Test
    public void testConstructor() throws MaformedLoincCodeException  {
        String code = "15074-8";
        LoincId id = new LoincId(code);
        assertEquals(code,id.toString());
    }

    @Test(expected = MaformedLoincCodeException.class)
    public void testBadCode() throws MaformedLoincCodeException  {
        String code = "15074-";
        LoincId id = new LoincId(code);
        assertEquals(code,id.toString());
    }


}
