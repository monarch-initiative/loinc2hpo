package org.monarchinitiative.loinc2hpo.fhir;

import com.github.phenomics.ontolib.formats.hpo.HpoOntology;
import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.github.phenomics.ontolib.io.obo.hpo.HpoOboParser;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.google.common.collect.ImmutableMap;
import org.hl7.fhir.dstu3.model.Observation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.exception.ReferenceNotFoundException;
import org.monarchinitiative.loinc2hpo.exception.UnrecognizedCodeException;
import org.monarchinitiative.loinc2hpo.loinc.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ObservationAnalysisFromCodedValuesTest {
    private static Observation[] observations = new Observation[4];
    private static Map<String, HpoTerm> hpoTermMap;
    private static Map<TermId, HpoTerm> hpoTermMap2;
    private static Map<LoincId, UniversalLoinc2HPOAnnotation> testmap = new HashMap<>();


    @BeforeClass
    public static void setup() throws MalformedLoincCodeException {
        String path = FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/staphylococcus.fhir").getPath();
        Observation observation1 = FhirResourceRetriever.parseJsonFile2Observation(path);
        path = FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/staphylococcusNoInterpretation.fhir").getPath();
        Observation observation2 = FhirResourceRetriever.parseJsonFile2Observation(path);
        path = FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/ecoliNoInterpretation.fhir").getPath();
        Observation observation3 = FhirResourceRetriever.parseJsonFile2Observation(path);
        path = FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/neisseriaNoInterpretation.fhir").getPath();
        Observation observation4 = FhirResourceRetriever.parseJsonFile2Observation(path);

        observations[0] = observation1;
        observations[1] = observation2;
        observations[2] = observation3;
        observations[3] = observation4;


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


        UniversalLoinc2HPOAnnotation.Builder loinc2HpoAnnotationBuilder = new UniversalLoinc2HPOAnnotation.Builder();

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

        UniversalLoinc2HPOAnnotation annotation15074 = loinc2HpoAnnotationBuilder.build();


        testmap.put(loincId, annotation15074);

        loinc2HpoAnnotationBuilder = new UniversalLoinc2HPOAnnotation.Builder();

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
                .addAdvancedAnnotation(code1, new HpoTermId4LoincTest(forCode1, false))
                .addAdvancedAnnotation(code2, new HpoTermId4LoincTest(forCode2, false));

        UniversalLoinc2HPOAnnotation annotation600 = loinc2HpoAnnotationBuilder.build();

        testmap.put(loincId, annotation600);
    }

    @Test
    public void testNom1() throws Exception {
        LoincId loincId = new LoincId("600-7");

        ObservationAnalysis analyzer = new ObservationAnalysisFromCodedValues(loincId, observations[0].getValueCodeableConcept(), testmap);
        assertNotNull(analyzer.getHPOforObservation());
        assertEquals("0002726", analyzer.getHPOforObservation().getId().getId());
    }


    @Test (expected = UnrecognizedCodeException.class)
    public void testGetInterpretationCodes2() throws Exception {
        LoincId loincId = new LoincId("600-7");
        ObservationAnalysis analyzer = new ObservationAnalysisFromCodedValues(loincId, observations[3].getValueCodeableConcept(), testmap);
        analyzer.getHPOforObservation();
    }

}
