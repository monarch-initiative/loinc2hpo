package org.monarchinitiative.loinc2hpo.io;

import com.github.phenomics.ontolib.formats.hpo.HpoOntology;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.loinc.Loinc2HPOAnnotation;

import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LoincMappingParserTest {

    static private LoincMappingParser loincparser;

    @BeforeClass
    public static void setup() throws IOException {
        ClassLoader classLoader = LoincMappingParserTest.class.getClassLoader();
        String obopath = classLoader.getResource("obo/hp.obo").getFile();
        String loincpath=classLoader.getResource("loinc2hpoAnnotationTest.tsv").getFile();
        HPOParser parser = new HPOParser(obopath);
        HpoOntology ontology = parser.getHPO();
        loincparser = new LoincMappingParser(loincpath,ontology);
    }



    @Test
    public void testNotNull() {
        assertNotNull(loincparser);
    }


    @Test
    public void testGetTest() {
        Set<Loinc2HPOAnnotation> tests = loincparser.getTests();
        assertTrue(tests.size()>0);
    }




}
