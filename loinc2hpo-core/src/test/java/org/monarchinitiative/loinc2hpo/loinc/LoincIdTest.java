package org.monarchinitiative.loinc2hpo.loinc;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class LoincIdTest {


    @Test
    public void testConstructor() throws MalformedLoincCodeException {
        String code = "15074-8";
        LoincId id = new LoincId(code);
        assertEquals(code,id.toString());
    }

    @Test
    public void testConstructor2() throws MalformedLoincCodeException {
        String code = "3141-9";
        LoincId id = new LoincId(code);
        assertEquals(code,id.toString());
    }

    @Test
    public void testBadCode() {
        Assertions.assertThrows(MalformedLoincCodeException.class, () -> {
            String code = "15074-";
            LoincId id = new LoincId(code);
            assertEquals(code,id.toString());
        });

    }

    @Test
    public void testBadCode2() throws MalformedLoincCodeException {
        Assertions.assertThrows(MalformedLoincCodeException.class, () -> {
            String code = "1507423";
            LoincId id = new LoincId(code);
            assertEquals(code, id.toString());
        });
    }

    // test custom equals function
    @Test
    public void testEquals()throws MalformedLoincCodeException {
        String code1="19048-8";
        String code2="19048-8";
        LoincId id1=new LoincId(code1);
        LoincId id2=new LoincId(code2);
        assertEquals(id1,id2);
    }




}
