package org.monarchinitiative.loinc2hpo.fhir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.exception.Loinc2HpoException;
import org.monarchinitiative.loinc2hpo.exception.WrongElementException;

import java.io.*;

public class FhirObservationParserTest {

    private static JsonNode node;


    @BeforeClass
    public static void setup() throws IOException {
        ClassLoader classLoader = FhirObservationParserTest.class.getClassLoader();
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
        FhirObservationParser.Fhri2Pojo(node);
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
        FhirObservationParser.Fhri2Pojo(node2);
    }








}
