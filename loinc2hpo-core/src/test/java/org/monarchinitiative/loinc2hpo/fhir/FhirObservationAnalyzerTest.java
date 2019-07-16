package org.monarchinitiative.loinc2hpo.fhir;


import org.hl7.fhir.dstu3.model.Observation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.loinc.*;
import org.monarchinitiative.loinc2hpo.testresult.LabTestOutcome;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;

import static org.junit.Assert.*;

public class FhirObservationAnalyzerTest {

    private static Observation observation;
    private static Map<String, Term> hpoTermMap;

    @BeforeClass
    public static void setup() throws Exception{
        String path = FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/glucoseHigh.fhir").getPath();
        observation = FhirResourceRetriever.parseJsonFile2Observation(path);
    }

    @Test
    public void setObservation() throws Exception {

        FhirObservationAnalyzer.setObservation(observation);
        assertNotNull(FhirObservationAnalyzer.getObservation());
    }

    @Test
    public void getHPO4ObservationOutcome() throws Exception {
        FhirObservationAnalyzer.setObservation(observation);
    }

    @Test
    public void getLoincIdOfObservation() throws Exception {

        FhirObservationAnalyzer.setObservation(observation);
        LoincId loincId = FhirObservationAnalyzer.getLoincIdOfObservation();
        assertEquals("15074-8", loincId.toString());

    }


    @Test
    public void testUniversalAnnotation() throws Exception {

        FhirObservationAnalyzer.setObservation(observation);

        Map<LoincId, LOINC2HpoAnnotationImpl> testmap = new HashMap<>();
        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        TermId low = TermId.of("HP:001");
        TermId normal = TermId.of("HP:002");
        TermId hi = TermId.of("HP:003");

        LOINC2HpoAnnotationImpl glucoseAnnotation = new LOINC2HpoAnnotationImpl.Builder()
                .setLoincId(loincId)
                .setLoincScale(loincScale)
                .setLowValueHpoTerm(low)
                .setIntermediateValueHpoTerm(normal)
                .setHighValueHpoTerm(hi)
                .setIntermediateNegated(true)
                .build();

        testmap.put(loincId, glucoseAnnotation);

        Set<LoincId> loincIdSet = new HashSet<>();
        loincIdSet.add(loincId);
        LabTestOutcome result = FhirObservationAnalyzer.getHPO4ObservationOutcome(loincIdSet, testmap);
        assertEquals(result.getOutcome().getId(), hi);

    }


    @Test
    public void getHPOFromRawValue() throws Exception {
    }

    @Test
    public void getHPOFromCodedValue() throws Exception {
    }

}