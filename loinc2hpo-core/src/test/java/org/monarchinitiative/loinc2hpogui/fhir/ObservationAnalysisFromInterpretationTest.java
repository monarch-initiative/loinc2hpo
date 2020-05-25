package org.monarchinitiative.loinc2hpogui.fhir;

import org.hl7.fhir.dstu3.model.Observation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpocore.exception.AmbiguousResultsFoundException;
import org.monarchinitiative.loinc2hpocore.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpocore.fhir.FhirResourceRetriever;
import org.monarchinitiative.loinc2hpocore.fhir.ObservationAnalysisFromInterpretation;
import org.monarchinitiative.loinc2hpocore.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.loinc2hpocore.loinc.LoincScale;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;


public class ObservationAnalysisFromInterpretationTest {

    private static Observation[] observations = new Observation[2];
    private static Map<LoincId, LOINC2HpoAnnotationImpl> testmap = new HashMap<>();
    private static CodeSystemConvertor convertor;

    @BeforeAll
    public static void setup() throws MalformedLoincCodeException, IOException, DataFormatException {
        String path = FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/glucoseHigh.fhir").getPath();
        Observation observation1 = FhirResourceRetriever.parseJsonFile2Observation(path);
        path = FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/glucoseConflictingInterpretation.fhir").getPath();
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
        convertor = new CodeSystemConvertor();
    }


    @Test
    public void testGetInterpretationCodes() throws Exception{

        LoincId loincId = new LoincId("15074-8");

        ObservationAnalysisFromInterpretation analyzer =
                new ObservationAnalysisFromInterpretation(loincId,
                        observations[0].getInterpretation(), testmap, convertor);
        assertNotNull(analyzer.getInterpretationCodes());
        assertEquals(1, analyzer.getInterpretationCodes().size());
        assertNotEquals(0, analyzer.getInterpretationCodes().size());

        analyzer = new ObservationAnalysisFromInterpretation(loincId,
                observations[1].getInterpretation(), testmap, convertor);
        assertNotNull(analyzer.getInterpretationCodes());
        assertEquals(2, analyzer.getInterpretationCodes().size());
        assertNotEquals(0, analyzer.getInterpretationCodes().size());
    }
    @Test
    public void getHPOforObservation() throws Exception {

        LoincId loincId = new LoincId("15074-8");
        ObservationAnalysisFromInterpretation analyzer =
                new ObservationAnalysisFromInterpretation(loincId,
                        observations[0].getInterpretation(), testmap, convertor);

        assertNotNull(analyzer.getHPOforObservation());
        HpoTerm4TestOutcome hpoterm = analyzer.getHPOforObservation();
        assertEquals("HP:003", hpoterm.getId().getValue());
        assertFalse(hpoterm.isNegated());

    }


    @Test
    public void getHPOforObservationTestException() throws Exception {
        Assertions.assertThrows(AmbiguousResultsFoundException.class, () -> {
            LoincId loincId = new LoincId("15074-8");
            ObservationAnalysisFromInterpretation analyzer =
                    new ObservationAnalysisFromInterpretation(loincId,
                            observations[1].getInterpretation(), testmap, convertor);
            HpoTerm4TestOutcome hpoterm = analyzer.getHPOforObservation();
        });


    }

}