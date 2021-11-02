package fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;


import org.hl7.fhir.dstu3.model.Observation;
import org.junit.jupiter.api.BeforeAll;

import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Loinc2HpoAnnotation;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.loinc2hpocore.loinc.LoincScale;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;


public class ObservationAnalysisFromCodedValuesTest {
    private static Observation[] observations = new Observation[4];
    private static Map<LoincId, Loinc2HpoAnnotation> testmap = new HashMap<>();
    private static Loinc2Hpo loinc2Hpo = mock(Loinc2Hpo.class);


    @BeforeAll
    public static void setup() {
        FhirContext ctx = FhirContext.forDstu3();
        IParser jsonparser = ctx.newJsonParser();
        Observation observation1 = (Observation)
                jsonparser.parseResource(ObservationAnalysisFromCodedValuesTest.class.getClassLoader().getResourceAsStream("json/staphylococcus.fhir"));
        Observation observation4 =
                (Observation) jsonparser.parseResource(ObservationAnalysisFromCodedValuesTest.class.getClassLoader().getResourceAsStream("json/neisseriaNoInterpretation.fhir"));

        observations[0] = observation1;
        observations[3] = observation4;
    /*
        Loinc2HpoAnnotation.Builder loinc2HpoAnnotationBuilder = new Loinc2HpoAnnotationModelLEGACY.Builder();

        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.fromString("Qn");
        TermId low = TermId.of("HP:001");
        TermId normal = TermId.of("HP:002");
        TermId hi = TermId.of("HP:003");

        loinc2HpoAnnotationBuilder.setLoincId(loincId)
                .setLoincScale(loincScale)
                .setLowValueHpoTerm(low)
                .setIntermediateValueHpoTerm(normal, true)
                .setHighValueHpoTerm(hi);

        Loinc2HpoAnnotationModelLEGACY annotation15074 = loinc2HpoAnnotationBuilder.build();


        testmap.put(loincId, annotation15074);

        loinc2HpoAnnotationBuilder = new Loinc2HpoAnnotationModelLEGACY.Builder();

        loincId = new LoincId("600-7");
        loincScale = LoincScale.fromString("Nom");
        TermId ecoli = TermId.of("HP:004");
        TermId staphaureus = TermId.of("HP:005");
        TermId bacterial = TermId.of("HP:006");
        OutcomeCodeOLD ecoli_snomed = OutcomeCodeOLD.fromSystemAndCode("http://snomed.info/sct", "112283007");
        OutcomeCodeOLD staph_snomed = OutcomeCodeOLD.fromSystemAndCode("http://snomed.info/sct", "3092008");

        loinc2HpoAnnotationBuilder.setLoincId(loincId)
                .setLoincScale(loincScale)
                .setHighValueHpoTerm(bacterial)
                .addAnnotation(ecoli_snomed, new Hpo2Outcome(ecoli, false))
                .addAnnotation(staph_snomed, new Hpo2Outcome(staphaureus, false));

        Loinc2HpoAnnotationModelLEGACY annotation600 = loinc2HpoAnnotationBuilder.build();

        testmap.put(loincId, annotation600);

        when(loinc2Hpo.getAnnotationMap()).thenReturn(testmap);

 */
    }

    /*
    @Test
    public void testNom1() throws Exception {
        ObservationAnalysis analyzer = new ObservationAnalysisFromCodedValues(loinc2Hpo, observations[0]);
//        assertNotNull(analyzer.getHPOforObservation());
        // TODO -- WHAT
      //  assertEquals("005", analyzer.getHPOforObservation().getId().getId());
    }

     */



}
