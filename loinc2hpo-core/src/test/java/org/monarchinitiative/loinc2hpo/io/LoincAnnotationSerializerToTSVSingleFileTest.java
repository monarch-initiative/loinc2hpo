package org.monarchinitiative.loinc2hpo.io;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.LoincScale;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class LoincAnnotationSerializerToTSVSingleFileTest {

    private static Map<LoincId, LOINC2HpoAnnotationImpl> testmap = new HashMap<>();
    @TempDir
    static File temporaryFolder;
    static String temporaryPath;

    @BeforeAll
    public static void setup() throws Exception {

        LOINC2HpoAnnotationImpl.Builder loinc2HpoAnnotationBuilder = new LOINC2HpoAnnotationImpl.Builder();

        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        TermId low = TermId.of("HP:001");
        TermId normal = TermId.of("HP:002");
        TermId hi = TermId.of("HP:003");

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
        TermId ecoli = TermId.of("HP:004");
        TermId staphaureus = TermId.of("HP:005");
        TermId bacterial = TermId.of("HP:006");

        Code ecoli_snomed = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("112283007");
        Code staph_snomed = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("3092008");

        loinc2HpoAnnotationBuilder.setLoincId(loincId)
                .setLoincScale(loincScale)
                .setHighValueHpoTerm(bacterial)
                .addAdvancedAnnotation(ecoli_snomed, new HpoTerm4TestOutcome(ecoli, false))
                .addAdvancedAnnotation(staph_snomed, new HpoTerm4TestOutcome(staphaureus, false));

        LOINC2HpoAnnotationImpl annotation600 = loinc2HpoAnnotationBuilder.build();

        testmap.put(loincId, annotation600);

        temporaryPath = new File(temporaryFolder, "tempfile").getAbsolutePath();
    }


    @Test
    public void serialize() throws Exception {
        LoincAnnotationSerializer serializer = new LoincAnnotationSerializerToTSVSingleFile(null);
        serializer.serialize(testmap, temporaryPath);
        BufferedReader reader = new BufferedReader(new FileReader(temporaryPath));
        reader.lines().forEach(System.out::println);
    }

    @Test
    public void parse() throws Exception {
        LoincAnnotationSerializer serializer = new LoincAnnotationSerializerToTSVSingleFile(null);
        serializer.serialize(testmap, temporaryPath);
        LoincAnnotationSerializerToTSVSingleFile serilizer = new LoincAnnotationSerializerToTSVSingleFile(null);
        Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap = serilizer.parse(temporaryPath);
        assertNotNull(annotationMap);
        assertEquals(2, annotationMap.size());

    }

    @Test
    public void testFactory() throws Exception {
        LoincAnnotationSerializer serializer = new LoincAnnotationSerializerToTSVSingleFile(null);
        serializer.serialize(testmap, temporaryPath);


        Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap =
                LoincAnnotationSerializationFactory.parseFromFile(temporaryPath, null, LoincAnnotationSerializationFactory.SerializationFormat.TSVSingleFile);
        assertNotNull(annotationMap);
        assertEquals(2, annotationMap.size());

    }

}