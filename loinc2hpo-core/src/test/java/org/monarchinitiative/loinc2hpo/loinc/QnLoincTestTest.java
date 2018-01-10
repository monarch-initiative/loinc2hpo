package org.monarchinitiative.loinc2hpo.loinc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.monarchinitiative.loinc2hpo.fhir.FhirObservationParserTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class QnLoincTestTest {

    private static JsonNode node;

    @BeforeClass
    public static void setup() throws IOException {
        ClassLoader classLoader = FhirObservationParserTest.class.getClassLoader();
        String fhirPath = classLoader.getResource("json/erythrocyte.fhir").getFile();
        ObjectMapper mapper = new ObjectMapper();
        File f = new File(fhirPath);
        FileInputStream fis = new FileInputStream(f);
        byte[] data = new byte[(int) f.length()];
        fis.read(data);
        fis.close();
        node = mapper.readTree(data);
    }





}
