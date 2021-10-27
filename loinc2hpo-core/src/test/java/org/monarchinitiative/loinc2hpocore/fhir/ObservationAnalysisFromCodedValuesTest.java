package org.monarchinitiative.loinc2hpocore.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;


import org.hl7.fhir.dstu3.model.Observation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpocore.exception.UnrecognizedCodeException;
import org.monarchinitiative.loinc2hpocore.fhir2hpo.ObservationAnalysis;
import org.monarchinitiative.loinc2hpocore.fhir2hpo.ObservationAnalysisFromCodedValues;
import org.monarchinitiative.loinc2hpocore.annotationmodel.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Loinc2HpoAnnotationModel;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.loinc2hpocore.loinc.LoincScale;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



public class ObservationAnalysisFromCodedValuesTest {
    private static Observation[] observations = new Observation[4];
    private static Map<LoincId, Loinc2HpoAnnotationModel> testmap = new HashMap<>();
    private static Loinc2Hpo loinc2Hpo = mock(Loinc2Hpo.class);


    @BeforeAll
    public static void setup() throws MalformedLoincCodeException, IOException {
        FhirContext ctx = FhirContext.forDstu3();
        IParser jsonparser = ctx.newJsonParser();
        Observation observation1 = (Observation)
                jsonparser.parseResource(ObservationAnalysisFromCodedValuesTest.class.getClassLoader().getResourceAsStream("json/staphylococcus.fhir"));

//        Observation observation2 =
//                (Observation) jsonparser.parseResource(FhirObservationAnalyzerTest.class.getClassLoader().getResourceAsStream("json/staphylococcusNoInterpretation.fhir"));
//
//        Observation observation3 =
//                (Observation) jsonparser.parseResource(FhirObservationAnalyzerTest.class.getClassLoader().getResourceAsStream("json/ecoliNoInterpretation.fhir"));

        Observation observation4 =
                (Observation) jsonparser.parseResource(ObservationAnalysisFromCodedValuesTest.class.getClassLoader().getResourceAsStream("json/neisseriaNoInterpretation.fhir"));

        observations[0] = observation1;
        observations[3] = observation4;

        Loinc2HpoAnnotationModel.Builder loinc2HpoAnnotationBuilder = new Loinc2HpoAnnotationModel.Builder();

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

        Loinc2HpoAnnotationModel annotation15074 = loinc2HpoAnnotationBuilder.build();


        testmap.put(loincId, annotation15074);

        loinc2HpoAnnotationBuilder = new Loinc2HpoAnnotationModel.Builder();

        loincId = new LoincId("600-7");
        loincScale = LoincScale.string2enum("Nom");
        TermId ecoli = TermId.of("HP:004");
        TermId staphaureus = TermId.of("HP:005");
        TermId bacterial = TermId.of("HP:006");

        Code ecoli_snomed = Code.fromSystemAndCode("http://snomed.info/sct", "112283007");
        Code staph_snomed = Code.fromSystemAndCode("http://snomed.info/sct", "3092008");

        loinc2HpoAnnotationBuilder.setLoincId(loincId)
                .setLoincScale(loincScale)
                .setHighValueHpoTerm(bacterial)
                .addAnnotation(ecoli_snomed, new HpoTerm4TestOutcome(ecoli, false))
                .addAnnotation(staph_snomed, new HpoTerm4TestOutcome(staphaureus, false));

        Loinc2HpoAnnotationModel annotation600 = loinc2HpoAnnotationBuilder.build();

        testmap.put(loincId, annotation600);

        when(loinc2Hpo.getAnnotationMap()).thenReturn(testmap);
    }

    @Test
    public void testNom1() throws Exception {
        ObservationAnalysis analyzer =
        new ObservationAnalysisFromCodedValues(loinc2Hpo, observations[0]);
        assertNotNull(analyzer.getHPOforObservation());
        assertEquals("005", analyzer.getHPOforObservation().getId().getId());
    }


    @Test
    public void testGetInterpretationCodes2()  {
        Assertions.assertThrows(UnrecognizedCodeException.class, () -> {
            ObservationAnalysis analyzer =
                    new ObservationAnalysisFromCodedValues(loinc2Hpo,
                            observations[3]);
            HpoTerm4TestOutcome term = analyzer.getHPOforObservation();
        });
    }

}
