package org.monarchinitiative.loinc2hpo.fhir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phenomics.ontolib.formats.hpo.HpoOntology;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.exception.Loinc2HpoException;
import org.monarchinitiative.loinc2hpo.io.HPOParser;
import org.monarchinitiative.loinc2hpo.io.LoincMappingParser;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.Loinc2HPOAnnotation;
import org.monarchinitiative.loinc2hpo.testresult.LabTestResultInHPO;

import java.io.*;
import java.util.Map;

import static org.junit.Assert.*;

public class FhirObservationParserTest {

    private static JsonNode node;
    static private LoincMappingParser loincparser;
    static  private Map<LoincId, Loinc2HPOAnnotation> testmap;




    @BeforeClass
    public static void setup() throws IOException {
        ClassLoader classLoader = FhirObservationParserTest.class.getClassLoader();
        String obopath = classLoader.getResource("obo/hp.obo").getFile();
        String loincpath=classLoader.getResource("loinc2hpoAnnotationTest.tsv").getFile();
        HPOParser parser = new HPOParser(obopath);
        HpoOntology ontology = parser.getHPO();
        loincparser = new LoincMappingParser(loincpath,ontology);
        testmap=loincparser.getTestmap();

        String fhirPath = classLoader.getResource("json/glucoseHigh.fhir")
                .getFile();
        ObjectMapper mapper = new ObjectMapper();
        File f = new File(fhirPath);
        FileInputStream fis = new FileInputStream(f);
        byte[] data = new byte[(int) f.length()];
        fis.read(data);
        fis.close();
        node = mapper.readTree(data);
    }


    private static JsonNode getObservationNode(String path) {
        JsonNode node=null;
        try {
            ClassLoader classLoader = FhirObservationParserTest.class.getClassLoader();
            String fhirPath = classLoader.getResource(path).getFile();
            ObjectMapper mapper = new ObjectMapper();
            File f = new File(fhirPath);
            FileInputStream fis = new FileInputStream(f);
            byte[] data = new byte[(int) f.length()];
            fis.read(data);
            fis.close();

            node = mapper.readTree(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return node;
    }



    @Test
    public void testParse() throws Loinc2HpoException{
        FhirResourceRetriever.fhir2testrest(node,testmap);
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
        FhirResourceRetriever.fhir2testrest(node2,testmap);
    }

    @Test
    public void testGetHyperglycemia() throws Loinc2HpoException{
        LabTestResultInHPO res = FhirResourceRetriever.fhir2testrest(node,testmap);
        assertNotNull(res);
//        System.err.println(res);
        String expected="HP:0003074";
        String actual=res.getTermId().getIdWithPrefix();
        assertEquals(expected,actual);
    }

    @Test
    public void testGetNormoglycemia() throws Loinc2HpoException{
        JsonNode normGlycNode = getObservationNode("json/glucoseNormal.fhir");
        assertNotNull(normGlycNode);
        LabTestResultInHPO res =  FhirResourceRetriever.fhir2testrest(normGlycNode,testmap);
        assertNotNull(res);
//        System.err.println(res);
        String expected="HP:0011015"; // Abn of glucose metabolism
        assertTrue(res.isNegated());
        assertEquals(expected,res.getTermId().getIdWithPrefix());
    }








}
