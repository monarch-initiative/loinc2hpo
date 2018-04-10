package org.monarchinitiative.loinc2hpo.io;


import com.google.common.collect.ImmutableMap;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;
import org.monarchinitiative.loinc2hpo.fhir.FhirObservationAnalyzerTest;
import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.LoincScale;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.formats.hpo.HpoTerm;
import org.monarchinitiative.phenol.io.obo.hpo.HpoOboParser;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class LoincAnnotationSerializerToTSVSingleFileTest {

    private static Map<LoincId, LOINC2HpoAnnotationImpl> testmap = new HashMap<>();
    private static Map<String, HpoTerm> hpoTermMap;
    private static Map<TermId, HpoTerm> hpoTermMap2;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void setup() throws Exception {

        String hpo_obo = FhirObservationAnalyzerTest.class.getClassLoader().getResource("obo/hp.obo").getPath();
        HpoOboParser hpoOboParser = new HpoOboParser(new File(hpo_obo));
        HpoOntology hpo = null;
        try {
            hpo = hpoOboParser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImmutableMap.Builder<String,HpoTerm> termmap = new ImmutableMap.Builder<>();
        ImmutableMap.Builder<TermId, HpoTerm> termMap2 = new ImmutableMap.Builder<>();
        if (hpo !=null) {
            List<HpoTerm> res = hpo.getTermMap().values().stream().distinct()
                    .collect(Collectors.toList());
            res.forEach( term -> {
                termmap.put(term.getName(),term);
                termMap2.put(term.getId(), term);
            });
        }
        hpoTermMap = termmap.build();
        hpoTermMap2 = termMap2.build();


        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        HpoTerm low = hpoTermMap.get("Hypoglycemia");
        HpoTerm normal = hpoTermMap.get("Abnormality of blood glucose concentration");
        HpoTerm hi = hpoTermMap.get("Hyperglycemia");

        Map<String, Code> internalCodes = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);
        LOINC2HpoAnnotationImpl glucoseAnnotation = new LOINC2HpoAnnotationImpl.Builder()
                .setLoincId(loincId)
                .setLoincScale(loincScale)
                .setLowValueHpoTerm(low)
                .setIntermediateValueHpoTerm(normal)
                .setHighValueHpoTerm(hi)
                .setIntermediateNegated(true)
                .build();
        testmap.put(loincId, glucoseAnnotation);



        loincId = new LoincId("600-7");
        loincScale = LoincScale.string2enum("Nom");
        HpoTerm forCode1 = hpoTermMap.get("Recurrent E. coli infections");
        HpoTerm forCode2 = hpoTermMap.get("Recurrent Staphylococcus aureus infections");
        HpoTerm positive = hpoTermMap.get("Recurrent bacterial infections");

        Code code1 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("112283007");
        Code code2 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("3092008");

        LOINC2HpoAnnotationImpl bacterialAnnotation = new LOINC2HpoAnnotationImpl.Builder()
                .setLoincId(loincId)
                .setLoincScale(loincScale)
                .addAdvancedAnnotation(code1, new HpoTerm4TestOutcome(forCode1, false))
                .addAdvancedAnnotation(code2, new HpoTerm4TestOutcome(forCode2, false))
                .addAdvancedAnnotation(internalCodes.get("POS"), new HpoTerm4TestOutcome(positive, false))
                .build();

        testmap.put(loincId, bacterialAnnotation);
    }


    @Test
    public void serialize() throws Exception {

        String tempFile = folder.newFile().getAbsolutePath();
        LoincAnnotationSerializer serializer = new LoincAnnotationSerializerToTSVSingleFile(null);
        serializer.serialize(testmap, tempFile);

        BufferedReader reader = new BufferedReader(new FileReader(tempFile));
        reader.lines().forEach(System.out::println);

    }

    @Test
    public void parse() throws Exception {

        String tempFile = folder.newFile().getAbsolutePath();
        LoincAnnotationSerializer serializer = new LoincAnnotationSerializerToTSVSingleFile(null);
        serializer.serialize(testmap, tempFile);

        LoincAnnotationSerializerToTSVSingleFile serilizer = new LoincAnnotationSerializerToTSVSingleFile(hpoTermMap2);
        Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap = serilizer.parse(tempFile);
        assertNotNull(annotationMap);
        assertEquals(2, annotationMap.size());
        annotationMap.values().forEach(System.out::println);

    }

    @Test
    public void testFactory() throws Exception {

        String tempFile = folder.newFile().getAbsolutePath();
        LoincAnnotationSerializer serializer = new LoincAnnotationSerializerToTSVSingleFile(null);
        serializer.serialize(testmap, tempFile);


        Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap = LoincAnnotationSerializationFactory.parseFromFile(tempFile, hpoTermMap2, LoincAnnotationSerializationFactory.SerializationFormat.TSVSingleFile);
        assertNotNull(annotationMap);
        assertEquals(2, annotationMap.size());
        annotationMap.values().forEach(System.out::println);

    }

}