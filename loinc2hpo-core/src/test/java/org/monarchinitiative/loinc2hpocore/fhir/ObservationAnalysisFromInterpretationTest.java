package org.monarchinitiative.loinc2hpocore.fhir;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.exception.AmbiguousResultsFoundException;
import org.monarchinitiative.loinc2hpocore.fhir2hpo.ObservationAnalysisFromInterpretation;
import org.monarchinitiative.loinc2hpocore.annotationmodel.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.annotationmodel.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ObservationAnalysisFromInterpretationTest {

    @Test
    public void getHPOforObservation() throws Exception {

        Observation observation = mock(Observation.class);
        LoincId loincId = new LoincId("15074-8");
        when(observation.getCode()).thenReturn(new CodeableConcept().addCoding(new Coding("http://loinc.org", "15074-8", "")));
        Coding exHigh = new Coding("http://hl7.org/fhir/v2/0078", "H", "High");
        when(observation.getInterpretation()).thenReturn(new CodeableConcept().addCoding(exHigh));

        Code inHigh = mock(Code.class);
        HpoTerm4TestOutcome hpoForHigh = mock(HpoTerm4TestOutcome.class);
        LOINC2HpoAnnotationImpl forGlucose =
                mock(LOINC2HpoAnnotationImpl.class);
        Map<Code, HpoTerm4TestOutcome> map = new HashMap<>();
        map.put(inHigh, hpoForHigh);
        when(forGlucose.getCandidateHpoTerms()).thenReturn((HashMap<Code,
                HpoTerm4TestOutcome>) map);

        Map<LoincId, LOINC2HpoAnnotationImpl> loinc2HpoAnnotationMap =
                new HashMap<>();
        loinc2HpoAnnotationMap.put(loincId, forGlucose);

        Loinc2Hpo loinc2Hpo = mock(Loinc2Hpo.class);
        when(loinc2Hpo.convertToInternal(new Code(exHigh.getSystem(),
                exHigh.getCode(), ""))).thenReturn(inHigh);
        when(loinc2Hpo.query(loincId, inHigh)).thenReturn(hpoForHigh);


        ObservationAnalysisFromInterpretation analyzer =
                new ObservationAnalysisFromInterpretation(loinc2Hpo,
                        observation);

        HpoTerm4TestOutcome hpoterm = analyzer.getHPOforObservation();
        assertEquals(hpoForHigh, hpoterm);

    }


    @Test
    public void getHPOforObservationTestException() throws Exception {

        Observation observation = mock(Observation.class);
        when(observation.getCode()).thenReturn(new CodeableConcept().addCoding(new Coding("http://loinc.org", "15074-8", "")));
        Coding exHigh = new Coding("http://hl7.org/fhir/v2/0078", "H", "High");
        Coding exLow = new Coding("http://hl7.org/fhir/v2/0078", "L", "Low");
        when(observation.getInterpretation()).thenReturn(new CodeableConcept().addCoding(exHigh).addCoding(exLow));

        Code inHigh = mock(Code.class);
        Code inLow = mock(Code.class);


        Loinc2Hpo loinc2Hpo = mock(Loinc2Hpo.class);
        when(loinc2Hpo.convertToInternal(new Code(exHigh.getSystem(),
                exHigh.getCode(), ""))).thenReturn(inHigh);
        when(loinc2Hpo.convertToInternal(new Code(exLow.getSystem(),
                exLow.getCode(), ""))).thenReturn(inLow);

        Assertions.assertThrows(AmbiguousResultsFoundException.class, () -> {
            ObservationAnalysisFromInterpretation analyzer =
                new ObservationAnalysisFromInterpretation(loinc2Hpo,
                        observation);

            HpoTerm4TestOutcome hpoterm = analyzer.getHPOforObservation();
        });


    }

}