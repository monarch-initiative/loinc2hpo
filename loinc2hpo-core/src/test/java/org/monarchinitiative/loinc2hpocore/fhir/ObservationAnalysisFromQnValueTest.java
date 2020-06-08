package org.monarchinitiative.loinc2hpocore.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.dstu3.model.Observation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
import org.monarchinitiative.loinc2hpocore.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpocore.exception.ReferenceNotFoundException;
import org.monarchinitiative.loinc2hpocore.fhir2hpo.ObservationAnalysisFromQnValue;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Loinc2HpoAnnotationModel;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.loinc2hpocore.loinc.LoincScale;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ObservationAnalysisFromQnValueTest {
    private static Observation[] observations = new Observation[2];
    private static Map<LoincId, Loinc2HpoAnnotationModel> testmap = new HashMap<>();
    private static Loinc2Hpo loinc2Hpo;


    @BeforeAll
    public static void setup() throws MalformedLoincCodeException {
        FhirContext ctx = FhirContext.forDstu3();
        IParser parser = ctx.newJsonParser();

        Observation observation1 = (Observation)
                parser.parseResource(ObservationAnalysisFromQnValueTest.class.getClassLoader().getResourceAsStream("json/glucoseHighNoInterpretation.fhir"));

        Observation observation2 =
                (Observation) parser.parseResource(ObservationAnalysisFromQnValueTest.class.getClassLoader().getResourceAsStream("json/glucoseNoInterpretationNoReference.fhir"));
        observations[0] = observation1;
        observations[1] = observation2;

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
        loinc2Hpo = new Loinc2Hpo(testmap, null);
    }

    @Test
    public void testGetHpo() throws Exception {

        ObservationAnalysisFromQnValue analyzer =
                new ObservationAnalysisFromQnValue(loinc2Hpo, observations[0]);
        assertNotNull(analyzer.getHPOforObservation());
        assertEquals("HP:003", analyzer.getHPOforObservation().getId().getValue());
    }


    @Test
    public void testNoRef() {
        Assertions.assertThrows(ReferenceNotFoundException.class, () -> {
            ObservationAnalysisFromQnValue analyzer =
                    new ObservationAnalysisFromQnValue(loinc2Hpo,
                            observations[1]);
            analyzer.getHPOforObservation();
        });

    }

}