package org.monarchinitiative.loinc2hpo.fhir;

import org.hl7.fhir.dstu3.model.Observation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.exception.ReferenceNotFoundException;
import org.monarchinitiative.loinc2hpo.loinc.*;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;

import static org.junit.Assert.*;

public class ObservationAnalysisFromQnValueTest {
    private static Observation[] observations = new Observation[2];
    private static Map<LoincId, LOINC2HpoAnnotationImpl> testmap = new HashMap<>();


    @BeforeClass
    public static void setup() throws MalformedLoincCodeException, IOException, DataFormatException {
        String path = FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/glucoseHighNoInterpretation.fhir").getPath();
        Observation observation1 = FhirResourceRetriever.parseJsonFile2Observation(path);
        path = FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/glucoseNoInterpretationNoReference.fhir").getPath();
        Observation observation2 = FhirResourceRetriever.parseJsonFile2Observation(path);
        observations[0] = observation1;
        observations[1] = observation2;

        LOINC2HpoAnnotationImpl.Builder loinc2HpoAnnotationBuilder = new LOINC2HpoAnnotationImpl.Builder();

        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        TermId low = TermId.of("HP:001");
        TermId normal = TermId.of("HP:002");
        TermId hi = TermId.of("HP:003");

        loinc2HpoAnnotationBuilder.setLoincId(loincId)
                .setLoincScale(loincScale)
                .setLowValueHpoTerm(low)
                .setIntermediateValueHpoTerm(normal)
                .setIntermediateNegated(true)
                .setHighValueHpoTerm(hi);

        LOINC2HpoAnnotationImpl annotation15074 = loinc2HpoAnnotationBuilder.build();


        testmap.put(loincId, annotation15074);

        loinc2HpoAnnotationBuilder = new LOINC2HpoAnnotationImpl.Builder();

        loincId = new LoincId("600-7");
        loincScale = LoincScale.string2enum("Nom");
        TermId ecoli = TermId.of("HP:004");
        TermId staphaureus = TermId.of("HP:005");
        TermId bacterial = TermId.of("HP:006");

        Code ecoli_snomed = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("112283007");
        Code staph_snomed = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("3092008");

        loinc2HpoAnnotationBuilder.setLoincId(loincId)
                .setLoincScale(loincScale)
                .setHighValueHpoTerm(bacterial)
                .addAdvancedAnnotation(ecoli_snomed, new HpoTerm4TestOutcome(ecoli, false))
                .addAdvancedAnnotation(staph_snomed, new HpoTerm4TestOutcome(staphaureus, false));

        LOINC2HpoAnnotationImpl annotation600 = loinc2HpoAnnotationBuilder.build();

        testmap.put(loincId, annotation600);
    }

    @Test
    public void testGetInterpretationCodes1() throws Exception {

        LoincId loincId = new LoincId("15074-8");
        ObservationAnalysisFromQnValue analyzer = new ObservationAnalysisFromQnValue(loincId, observations[0], testmap);
        assertNotNull(analyzer.getHPOforObservation());
        assertEquals("HP:003", analyzer.getHPOforObservation().getId().getValue());
    }


    @Test (expected = ReferenceNotFoundException.class)
    public void testGetInterpretationCodes2() throws Exception {

        LoincId loincId = new LoincId("15074-8");
        ObservationAnalysisFromQnValue analyzer = new ObservationAnalysisFromQnValue(loincId, observations[1], testmap);
        analyzer.getHPOforObservation();
    }

}