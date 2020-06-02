package org.monarchinitiative.loinc2hpocore.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SynsetTest {

    @Test
    public void testGetRow(){
        assertEquals(-1, new Synset().getRow("brain"));
        assertEquals(0, new Synset().getRow("blood"));
        assertEquals(0, new Synset().getRow("serum"));
        assertEquals(4, new Synset().getRow("heart"));
    }

    @Test
    public void testAddSynset(){
        assertEquals("brain", new Synset().getSynset("brain").getSynset().remove());
    }
    @Test
    public void test1(){
        String key = "brain";
        assertEquals("brain", new Synset().getSynset(key).convertToRe());
    }

    @Test
    public void test2(){
        assertEquals("plasma|serum|blood", new Synset().getSynset("serum").convertToRe());
    }

    @Test
    public void test3(){
        String[] keys = new String[]{"serum", "plasma"};
        assertEquals("plasma|serum|blood", new Synset().getSynset(Arrays.asList(keys)).convertToRe());
    }

    @Test
    public void test4(){
        String[] keys = new String[]{"RBC", "blood"};
        assertEquals("RBC|plasma|serum|blood|red blood cell|erythrocyte", new Synset().getSynset(Arrays.asList(keys)).convertToRe());
    }

    @Test
    public void test5(){
        assertEquals("plasma|serum|brain|blood", new Synset().getSynset("brain", "blood").convertToRe());
    }

    @Test
    public void test6(){
        assertEquals("spleen|skin", new Synset().getSynset("spleen", "skin").convertToRe());
    }

    @Test
    public void test7(){
        List<String> testlist = new LinkedList<>(Arrays.asList(new String[]{"blood", "heart", "brain"}));
        System.out.println(new Synset().getSynset(testlist).convertToRe());
    }
}