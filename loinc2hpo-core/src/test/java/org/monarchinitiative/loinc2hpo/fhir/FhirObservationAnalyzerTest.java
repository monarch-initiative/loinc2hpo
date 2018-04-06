package org.monarchinitiative.loinc2hpo.fhir;


import com.google.common.collect.ImmutableMap;
import org.hl7.fhir.dstu3.model.Observation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;
import org.monarchinitiative.loinc2hpo.loinc.*;
import org.monarchinitiative.loinc2hpo.loinc.Loinc2HPOAnnotation;
import org.monarchinitiative.loinc2hpo.loinc.QnLoinc2HPOAnnotation;
import org.monarchinitiative.loinc2hpo.testresult.LabTestResultInHPO;
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

public class FhirObservationAnalyzerTest {

    private static Observation observation;
    private static Map<String, HpoTerm> hpoTermMap;

    @BeforeClass
    public static void setup(){
        String path = FhirObservationAnalyzerTest.class.getClassLoader().getResource("json/glucoseHigh.fhir").getPath();
        observation = FhirResourceRetriever.parseJsonFile2Observation(path);

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
        assertNull(FhirObservationAnalyzer.getObservation());
    }

    @Test
    public void setObservation() throws Exception {

        //assertNull(FhirObservationAnalyzer.getObservation());
        FhirObservationAnalyzer.setObservation(observation);
        assertNotNull(FhirObservationAnalyzer.getObservation());
    }

    @Test
    public void getHPO4ObservationOutcome() throws Exception {
        FhirObservationAnalyzer.setObservation(observation);
    }

    @Test
    public void getLoincIdOfObservation() throws Exception {

        FhirObservationAnalyzer.setObservation(observation);
        LoincId loincId = FhirObservationAnalyzer.getLoincIdOfObservation();
        assertEquals("15074-8", loincId.toString());

    }

    @Test
    public void getHPOFromInterpretation() throws Exception {

        FhirObservationAnalyzer.setObservation(observation);

        Map<LoincId, UniversalLoinc2HPOAnnotation> testmap = new HashMap<>();
        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        TermId low = hpoTermMap.get("Hypoglycemia").getId();
        TermId normal = hpoTermMap.get("Abnormality of blood glucose concentration").getId();
        TermId hi = hpoTermMap.get("Hyperglycemia").getId();

        Loinc2HPOAnnotation test1 = new QnLoinc2HPOAnnotation(loincId, loincScale,  low,  normal,  hi);

        //testmap.put(loincId, test1);
        //LabTestResultInHPO result = FhirObservationAnalyzer.getHPOFromInterpretation(FhirObservationAnalyzer.getObservation().getInterpretation(), testmap);
        //System.out.println(result);

    }

    @Test
    public void testUniversalAnnotation() throws Exception {

        FhirObservationAnalyzer.setObservation(observation);

        Map<LoincId, UniversalLoinc2HPOAnnotation> testmap = new HashMap<>();
        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        TermId low = hpoTermMap.get("Hypoglycemia").getId();
        TermId normal = hpoTermMap.get("Abnormality of blood glucose concentration").getId();
        TermId hi = hpoTermMap.get("Hyperglycemia").getId();

        Map<String, Code> internalCodes = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);
        /**
        UniversalLoinc2HPOAnnotation glucoseAnnotation = new UniversalLoinc2HPOAnnotation(loincId, loincScale);
        glucoseAnnotation.addAnnotation(internalCodes.get("L"), new HpoTermId4LoincTest(low, false))
                .addAnnotation(internalCodes.get("N"), new HpoTermId4LoincTest(normal, true))
                .addAnnotation(internalCodes.get("A"), new HpoTermId4LoincTest(normal, false))
                .addAnnotation(internalCodes.get("H"), new HpoTermId4LoincTest(hi, false));
         **/
        UniversalLoinc2HPOAnnotation glucoseAnnotation = new UniversalLoinc2HPOAnnotation.Builder()
                .setLoincId(loincId)
                .setLoincScale(loincScale)
                .setLowValueHpoTerm(hpoTermMap.get("Hypoglycemia"))
                .setIntermediateValueHpoTerm(hpoTermMap.get("Abnormality of blood glucose concentration"))
                .setHighValueHpoTerm(hpoTermMap.get("Hyperglycemia"))
                .setIntermediateNegated(true)
                .build();

        testmap.put(loincId, glucoseAnnotation);
        LabTestResultInHPO result = FhirObservationAnalyzer.getHPOFromInterpretation(FhirObservationAnalyzer.getObservation().getInterpretation(), testmap);
        System.out.println(result);
    }

    @Test
    public void getHPOFromRawValue() throws Exception {
    }

    @Test
    public void getHPOFromCodedValue() throws Exception {
    }

}