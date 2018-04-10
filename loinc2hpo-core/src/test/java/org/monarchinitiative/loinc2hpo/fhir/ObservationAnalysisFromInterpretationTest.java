package org.monarchinitiative.loinc2hpo.fhir;


import com.google.common.collect.ImmutableMap;
import org.hl7.fhir.dstu3.model.Observation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.exception.AmbiguousResultsFoundException;
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.loinc.*;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.formats.hpo.HpoTerm;
import org.monarchinitiative.phenol.io.obo.hpo.HpoOboParser;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ObservationAnalysisFromInterpretationTest {

    private static Observation[] observations = new Observation[2];
    private static Map<String, HpoTerm> hpoTermMap;
    private static Map<TermId, HpoTerm> hpoTermMap2;
    private static Map<LoincId, LOINC2HpoAnnotationImpl> testmap = new HashMap<>();

    @BeforeClass
    public static void setup() throws MalformedLoincCodeException {
        String path = FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/glucoseHigh.fhir").getPath();
        Observation observation1 = FhirResourceRetriever.parseJsonFile2Observation(path);
        path = FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/glucoseConflictingInterpretation.fhir").getPath();
        Observation observation2 = FhirResourceRetriever.parseJsonFile2Observation(path);
        observations[0] = observation1;
        observations[1] = observation2;

        String hpo_obo = FhirObservationAnalyzerTest.class.getClassLoader().getResource("obo/hp.obo").getPath();
        HpoOboParser hpoOboParser = new HpoOboParser(new File(hpo_obo));
        HpoOntology hpo = null;
        try {
            hpo = hpoOboParser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImmutableMap.Builder<String,HpoTerm> termmap = new ImmutableMap.Builder<>();
        ImmutableMap.Builder<TermId,HpoTerm> termmap2 = new ImmutableMap.Builder<>();
        if (hpo !=null) {
            List<HpoTerm> res = hpo.getTermMap().values().stream().distinct()
                    .collect(Collectors.toList());
            res.forEach( term -> {
                termmap.put(term.getName(),term);
                termmap2.put(term.getId(), term);
            });
        }
        hpoTermMap = termmap.build();
        hpoTermMap2 = termmap2.build();


        LOINC2HpoAnnotationImpl.Builder loinc2HpoAnnotationBuilder = new LOINC2HpoAnnotationImpl.Builder();

        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        HpoTerm low = hpoTermMap.get("Hypoglycemia");
        HpoTerm normal = hpoTermMap.get("Abnormality of blood glucose concentration");
        HpoTerm hi = hpoTermMap.get("Hyperglycemia");

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
        HpoTerm forCode1 = hpoTermMap.get("Recurrent E. coli infections");
        HpoTerm forCode2 = hpoTermMap.get("Recurrent Staphylococcus aureus infections");
        HpoTerm positive = hpoTermMap.get("Recurrent bacterial infections");

        Code code1 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("112283007");
        Code code2 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("3092008");

        loinc2HpoAnnotationBuilder.setLoincId(loincId)
                .setLoincScale(loincScale)
                .setHighValueHpoTerm(positive)
                .addAdvancedAnnotation(code1, new HpoTerm4TestOutcome(forCode1, false))
                .addAdvancedAnnotation(code2, new HpoTerm4TestOutcome(forCode2, false));

        LOINC2HpoAnnotationImpl annotation600 = loinc2HpoAnnotationBuilder.build();

        testmap.put(loincId, annotation600);
    }


    @Test
    public void testGetInterpretationCodes() throws Exception{

        LoincId loincId = new LoincId("15074-8");

        ObservationAnalysisFromInterpretation analyzer = new ObservationAnalysisFromInterpretation(loincId, observations[0].getInterpretation(), testmap);
        assertNotNull(analyzer.getInterpretationCodes());
        assertEquals(1, analyzer.getInterpretationCodes().size());
        assertNotEquals(0, analyzer.getInterpretationCodes().size());

        analyzer = new ObservationAnalysisFromInterpretation(loincId, observations[1].getInterpretation(), testmap);
        assertNotNull(analyzer.getInterpretationCodes());
        assertEquals(2, analyzer.getInterpretationCodes().size());
        assertNotEquals(0, analyzer.getInterpretationCodes().size());
    }
    @Test
    public void getHPOforObservation() throws Exception {

        LoincId loincId = new LoincId("15074-8");
        ObservationAnalysisFromInterpretation analyzer = new ObservationAnalysisFromInterpretation(loincId, observations[0].getInterpretation(), testmap);

        assertNotNull(analyzer.getHPOforObservation());
        HpoTerm4TestOutcome hpoterm = analyzer.getHPOforObservation();
        System.out.println(hpoterm.getId());
        assertEquals(false, hpoterm.isNegated());

    }


    @Test (expected = AmbiguousResultsFoundException.class)
    public void getHPOforObservationTestException() throws Exception {

        LoincId loincId = new LoincId("15074-8");

        ObservationAnalysisFromInterpretation analyzer = new ObservationAnalysisFromInterpretation(loincId, observations[1].getInterpretation(), testmap);
        HpoTerm4TestOutcome hpoterm = analyzer.getHPOforObservation();

    }

}