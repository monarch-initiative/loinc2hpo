package fhir;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.ObservationAnalysisFromInterpretation;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Hpo2Outcome;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ObservationAnalysisFromInterpretationTest {
    /**
     * This page is part of the FHIR Specification (v4.0.1: R4 - Mixed Normative and STU).
     * This is the current published version. It defines v2 ABNORMAL FLAGS,
     * HL7-defined code system of concepts which specify a categorical assessment of an
     * observation value.
     * It is being communicated in FHIR in Observation.interpretation.
     */
    public static final String  hl7Version2Table0078 = "http://hl7.org/fhir/v2/0078/";


    @Test
    public void getHPOforObservation() throws Exception {
    /*
        Observation observation = mock(Observation.class);
        LoincId loincId = new LoincId("15074-8");
        when(observation.getCode()).thenReturn(new CodeableConcept().addCoding(new Coding("http://loinc.org", "15074-8", "")));
        Coding exHigh = new Coding(hl7Version2Table0078, "H", "High");
        when(observation.getInterpretation()).thenReturn(new CodeableConcept().addCoding(exHigh));
        Hpo2Outcome hpoForHigh = mock(Hpo2Outcome.class);
        Loinc2HpoAnnotation forGlucose =
                mock(Loinc2HpoAnnotation.class);
        HashMap<ShortCode, Hpo2Outcome> map = new HashMap<>();
        map.put(ShortCode.H, hpoForHigh);
        when(forGlucose.getCandidateHpoTerms()).thenReturn(map);
        Map<LoincId, Loinc2HpoAnnotation> loinc2HpoAnnotationMap = new HashMap<>();
        loinc2HpoAnnotationMap.put(loincId, forGlucose);
        Loinc2Hpo loinc2Hpo = mock(Loinc2Hpo.class);
        when(loinc2Hpo.query(loincId, ShortCode.H)).thenReturn(hpoForHigh);

        ObservationAnalysisFromInterpretation analyzer =
                new ObservationAnalysisFromInterpretation(loinc2Hpo,
                        observation);

        Hpo2Outcome hpoterm = analyzer.getHPOforObservation();
        Assertions.assertEquals(hpoForHigh, hpoterm);

     */

    }


    @Test
    public void getHPOforObservationTestException() throws Exception {

        Observation observation = mock(Observation.class);
        when(observation.getCode()).thenReturn(new CodeableConcept().addCoding(new Coding("http://loinc.org", "15074-8", "")));
        Coding exHigh = new Coding("http://hl7.org/fhir/v2/0078", "H", "High");
        Coding exLow = new Coding("http://hl7.org/fhir/v2/0078", "L", "Low");
        when(observation.getInterpretation()).thenReturn(new CodeableConcept().addCoding(exHigh).addCoding(exLow));

        Outcome inHigh = mock(Outcome.class);
        Outcome inLow = mock(Outcome.class);


        Loinc2Hpo loinc2Hpo = mock(Loinc2Hpo.class);


//        Assertions.assertThrows(Loinc2HpoRuntimeException.class, () -> {
//            ObservationAnalysisFromInterpretation analyzer =
//                new ObservationAnalysisFromInterpretation(loinc2Hpo,
//                        observation);

           // Hpo2Outcome hpoterm = analyzer.getHPOforObservation();
      //  });


    }

}