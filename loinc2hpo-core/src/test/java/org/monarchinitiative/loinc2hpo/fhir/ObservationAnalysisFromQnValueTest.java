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
import org.monarchinitiative.loinc2hpo.loinc.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ObservationAnalysisFromQnValueTest {
    private static Observation[] observations = new Observation[2];
    private static Map<String, HpoTerm> hpoTermMap;

    @BeforeClass
    public static void setup() throws MalformedLoincCodeException {
        String path = FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/glucoseHighNoInterpretation.fhir").getPath();
        Observation observation1 = FhirResourceRetriever.parseJsonFile2Observation(path);
        path = FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/glucoseNoInterpretationNoReference.fhir").getPath();
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
        if (hpo !=null) {
            List<HpoTerm> res = hpo.getTermMap().values().stream().distinct()
                    .collect(Collectors.toList());
            res.forEach( term -> termmap.put(term.getName(),term));
        }
        hpoTermMap = termmap.build();
    }
    @Test
    public void testGetInterpretationCodes1() throws Exception {
        Map<LoincId, Loinc2HPOAnnotation> testmap = new HashMap<>();
        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        TermId low = hpoTermMap.get("Hypoglycemia").getId();
        TermId normal = hpoTermMap.get("Abnormality of blood glucose concentration").getId();
        TermId hi = hpoTermMap.get("Hyperglycemia").getId();

        Map<String, Code> internalCodes = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);
        UniversalLoinc2HPOAnnotation glucoseAnnotation = new UniversalLoinc2HPOAnnotation(loincId, loincScale);
        glucoseAnnotation.addAnnotation(internalCodes.get("L"), new HpoTermId4LoincTest(low, false))
                .addAnnotation(internalCodes.get("N"), new HpoTermId4LoincTest(normal, true))
                .addAnnotation(internalCodes.get("A"), new HpoTermId4LoincTest(normal, false))
                .addAnnotation(internalCodes.get("H"), new HpoTermId4LoincTest(hi, false));
        testmap.put(loincId, glucoseAnnotation);
        ObservationAnalysisFromQnValue analyzer = new ObservationAnalysisFromQnValue(loincId, observations[0], testmap);
        assertNotNull(analyzer.getHPOforObservation());
        System.out.println(analyzer.getHPOforObservation().getId());
    }


    @Test (expected = ReferenceNotFoundException.class)
    public void testGetInterpretationCodes2() throws Exception {
        Map<LoincId, Loinc2HPOAnnotation> testmap = new HashMap<>();
        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        TermId low = hpoTermMap.get("Hypoglycemia").getId();
        TermId normal = hpoTermMap.get("Abnormality of blood glucose concentration").getId();
        TermId hi = hpoTermMap.get("Hyperglycemia").getId();

        Map<String, Code> internalCodes = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);
        UniversalLoinc2HPOAnnotation glucoseAnnotation = new UniversalLoinc2HPOAnnotation(loincId, loincScale);
        glucoseAnnotation.addAnnotation(internalCodes.get("L"), new HpoTermId4LoincTest(low, false))
                .addAnnotation(internalCodes.get("N"), new HpoTermId4LoincTest(normal, true))
                .addAnnotation(internalCodes.get("A"), new HpoTermId4LoincTest(normal, false))
                .addAnnotation(internalCodes.get("H"), new HpoTermId4LoincTest(hi, false));
        testmap.put(loincId, glucoseAnnotation);
        ObservationAnalysisFromQnValue analyzer = new ObservationAnalysisFromQnValue(loincId, observations[1], testmap);
        analyzer.getHPOforObservation();
    }
}