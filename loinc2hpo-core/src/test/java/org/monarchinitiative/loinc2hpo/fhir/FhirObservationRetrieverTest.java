package org.monarchinitiative.loinc2hpo.fhir;

import ca.uhn.fhir.parser.DataFormatException;
import org.hl7.fhir.dstu3.model.Observation;
import org.junit.Test;



import static org.junit.Assert.*;

public class FhirObservationRetrieverTest {
    @Test
    public void testParseJsonFile2Observation() throws Exception{

        String path = getClass().getClassLoader().getResource("json/glucoseHigh.fhir").getPath();
        Observation observation = FhirResourceRetriever.parseJsonFile2Observation(path);
        assertNotNull(observation);
        assertEquals("Observation", observation.getResourceType().toString());
    }

    @Test (expected = DataFormatException.class)
    public void testParseJsonFile2ObservationException() throws Exception{
        String path = getClass().getClassLoader().getResource("json/malformedObservation.fhir").getPath();
        Observation observation = FhirResourceRetriever.parseJsonFile2Observation(path);
    }

    @Test
    public void retrieveObservationFromServer() throws Exception {
    }

}