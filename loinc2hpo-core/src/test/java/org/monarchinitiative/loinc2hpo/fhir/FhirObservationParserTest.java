package org.monarchinitiative.loinc2hpo.fhir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phenomics.ontolib.formats.hpo.HpoOntology;
import org.apache.jena.base.Sys;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.exception.Loinc2HpoException;
import org.monarchinitiative.loinc2hpo.io.HPOParser;
import org.monarchinitiative.loinc2hpo.io.LoincMappingParser;
import org.monarchinitiative.loinc2hpo.io.LoincMappingParserTest;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.LoincTest;
import org.monarchinitiative.loinc2hpo.testresult.TestResult;

import java.io.*;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FhirObservationParserTest {

    private static JsonNode node;
    static private LoincMappingParser loincparser;
    static  private Map<LoincId, LoincTest> testmap;




    @BeforeClass
    public static void setup() throws IOException {
        ClassLoader classLoader = FhirObservationParserTest.class.getClassLoader();
        String obopath = classLoader.getResource("obo/hp.obo").getFile();
        String loincpath=classLoader.getResource("loinc2hpoAnnotationTest.tsv").getFile();
        HPOParser parser = new HPOParser(obopath);
        HpoOntology ontology = parser.getHPO();
        loincparser = new LoincMappingParser(loincpath,ontology);
        testmap=loincparser.getTestmap();

        String fhirPath = classLoader.getResource("json/glucose.fhir")
                .getFile();
        ObjectMapper mapper = new ObjectMapper();
        File f = new File(fhirPath);
        FileInputStream fis = new FileInputStream(f);
        byte[] data = new byte[(int) f.length()];
        fis.read(data);
        fis.close();
        node = mapper.readTree(data);
    }



    @Test
    public void testParse() throws Loinc2HpoException{
        FhirObservationParser.fhir2testrest(node,testmap);
    }


    @Test(expected = Loinc2HpoException.class)
    public void testCheckForObservation() throws Exception {
        ClassLoader classLoader = FhirObservationParserTest.class.getClassLoader();
        String fhirPath = classLoader.getResource("json/malformedObservation.fhir").getFile();
        ObjectMapper mapper = new ObjectMapper();
        File f = new File(fhirPath);
        FileInputStream fis = new FileInputStream(f);
        byte[] data = new byte[(int) f.length()];
        fis.read(data);
        fis.close();
        JsonNode node2 = mapper.readTree(data);
        FhirObservationParser.fhir2testrest(node2,testmap);
    }

    @Test
    public void testGetHyperglycemia() throws Loinc2HpoException{
        TestResult res = FhirObservationParser.fhir2testrest(node,testmap);
        assertNotNull(res);
        System.err.println(res);
        String expected="HP:0003074";
        String actual=res.getTermId().getIdWithPrefix();
        assertEquals(expected,actual);
    }








}
