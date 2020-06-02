package org.monarchinitiative.loinc2hpocore.fhir;


import org.hl7.fhir.dstu3.model.Observation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpocore.fhir2hpo.FhirObservationAnalyzer;
import org.monarchinitiative.loinc2hpocore.fhir2hpo.FhirResourceRetriever;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;


public class FhirObservationAnalyzerTest {

    private static Observation observation;
    private static CodeSystemConvertor convertor;

    @BeforeAll
    public static void setup() throws Exception {
        String path = Objects.requireNonNull(FhirObservationAnalyzerTest.class.getClassLoader().
                getResource("json/glucoseHigh.fhir")).getPath();
        observation = FhirResourceRetriever.parseJsonFile2Observation(path);
        convertor = mock(CodeSystemConvertor.class);
    }

    @Test
    public void setObservation() {
        FhirObservationAnalyzer.setObservation(observation);
        assertNotNull(FhirObservationAnalyzer.getObservation());
    }

    @Test
    public void getHPO4ObservationOutcome() {
        FhirObservationAnalyzer.setObservation(observation);
    }

    @Test
    public void getLoincIdOfObservation() throws Exception {
        FhirObservationAnalyzer.setObservation(observation);
        LoincId loincId = FhirObservationAnalyzer.getLoincIdOfObservation();
        assertEquals("15074-8", loincId.toString());
    }
}