package org.monarchinitiative.loinc2hpogui.fhir;

import org.hl7.fhir.dstu3.model.Observation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpocore.exception.UnrecognizedCodeException;
import org.monarchinitiative.loinc2hpocore.fhir.FhirResourceRetriever;
import org.monarchinitiative.loinc2hpocore.fhir.ObservationAnalysis;
import org.monarchinitiative.loinc2hpocore.fhir.ObservationAnalysisFromCodedValues;
import org.monarchinitiative.loinc2hpocore.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.loinc2hpocore.loinc.LoincScale;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ObservationAnalysisFromCodedValuesTest {
    private static Observation[] observations = new Observation[4];
    private static Map<LoincId, LOINC2HpoAnnotationImpl> testmap = new HashMap<>();


    @BeforeAll
    public static void setup() throws MalformedLoincCodeException, IOException {
        String path =
                FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/staphylococcus.fhir").getPath();
        Observation observation1 = FhirResourceRetriever.parseJsonFile2Observation(path);
        path =
                FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/staphylococcusNoInterpretation.fhir").getPath();
        Observation observation2 = FhirResourceRetriever.parseJsonFile2Observation(path);
        path =
                FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/ecoliNoInterpretation.fhir").getPath();
        Observation observation3 = FhirResourceRetriever.parseJsonFile2Observation(path);
        path =
                FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/neisseriaNoInterpretation.fhir").getPath();
        Observation observation4 = FhirResourceRetriever.parseJsonFile2Observation(path);

        observations[0] = observation1;
        observations[1] = observation2;
        observations[2] = observation3;
        observations[3] = observation4;

        LOINC2HpoAnnotationImpl.Builder loinc2HpoAnnotationBuilder = new LOINC2HpoAnnotationImpl.Builder();

        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        TermId low = TermId.of("HP:001");
        TermId normal = TermId.of("HP:002");
        TermId hi = TermId.of("HP:003");

        loinc2HpoAnnotationBuilder.setLoincId(loincId)
                .setLoincScale(loincScale)
                .setLowValueHpoTerm(low)
                .setIntermediateValueHpoTerm(normal, true)
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
    public void testNom1() throws Exception {
        LoincId loincId = new LoincId("600-7");
        ObservationAnalysis analyzer =
        new ObservationAnalysisFromCodedValues(loincId,
                observations[0].getValueCodeableConcept(), testmap);
        assertNotNull(analyzer.getHPOforObservation());
        assertEquals("005", analyzer.getHPOforObservation().getId().getId());
    }


    @Test
    public void testGetInterpretationCodes2()  {
        Assertions.assertThrows(UnrecognizedCodeException.class, () -> {
            LoincId loincId = new LoincId("600-7");
            ObservationAnalysis analyzer = new ObservationAnalysisFromCodedValues(loincId, observations[3].getValueCodeableConcept(), testmap);
            analyzer.getHPOforObservation();
        });


    }

}
