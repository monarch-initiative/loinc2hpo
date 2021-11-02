package fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.dstu3.model.Observation;

import org.junit.jupiter.api.BeforeAll;
import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Loinc2HpoAnnotation;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.util.HashMap;
import java.util.Map;


public class ObservationAnalysisFromQnValueTest {
    private static final Observation[] observations = new Observation[2];
    private static final Map<LoincId, Loinc2HpoAnnotation> testmap = new HashMap<>();
    private static Loinc2Hpo loinc2Hpo;


    @BeforeAll
    public static void setup() {
        FhirContext ctx = FhirContext.forDstu3();
        IParser parser = ctx.newJsonParser();

        Observation observation1 = (Observation)
                parser.parseResource(ObservationAnalysisFromQnValueTest.class.getClassLoader().getResourceAsStream("json/glucoseHighNoInterpretation.fhir"));

        Observation observation2 =
                (Observation) parser.parseResource(ObservationAnalysisFromQnValueTest.class.getClassLoader().getResourceAsStream("json/glucoseNoInterpretationNoReference.fhir"));
        observations[0] = observation1;
        observations[1] = observation2;
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
       // loinc2Hpo = new Loinc2Hpo(testmap, null);

     */
    }
/*
    @Test
    public void testGetHpo() throws Exception {

        ObservationAnalysisFromQnValue analyzer =
                new ObservationAnalysisFromQnValue(loinc2Hpo, observations[0]);
        assertNotNull(analyzer.getHPOforObservation());
        assertEquals("HP:003", analyzer.getHPOforObservation().getId().getValue());
    }


    @Test
    public void testNoRef() {
        Assertions.assertThrows(Loinc2HpoRuntimeException.class, () -> {
            ObservationAnalysisFromQnValue analyzer =
                    new ObservationAnalysisFromQnValue(loinc2Hpo,
                            observations[1]);
            analyzer.getHPOforObservation();
        });

    }

 */

}