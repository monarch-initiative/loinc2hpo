package org.monarchinitiative.loinc2hpo.io;


import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.loinc.UniversalLoinc2HPOAnnotation;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;

import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FromFileTest {

    static private FromFile loincparser;

    @BeforeClass
    public static void setup() throws IOException {
        ClassLoader classLoader = FromFileTest.class.getClassLoader();
        String obopath = classLoader.getResource("obo/hp.obo").getFile();
        String loincpath=classLoader.getResource("loinc2hpoAnnotationTest.tsv").getFile();
        HPOParser parser = new HPOParser(obopath);
        HpoOntology ontology = parser.getHPO();
        loincparser = new FromFile(loincpath,ontology);
    }



    @Test
    @Ignore
    public void testNotNull() {
        assertNotNull(loincparser);
    }


    @Test
    @Ignore
    public void testGetTest() {
        Set<UniversalLoinc2HPOAnnotation> tests = loincparser.getTests();
        assertTrue(tests.size()>0);
    }




}
