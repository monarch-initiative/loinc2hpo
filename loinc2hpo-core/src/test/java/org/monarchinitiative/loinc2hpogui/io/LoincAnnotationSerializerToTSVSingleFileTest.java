package org.monarchinitiative.loinc2hpogui.io;


import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.io.LoincAnnotationSerializer;
import org.monarchinitiative.loinc2hpocore.io.LoincAnnotationSerializerToTSVSingleFile;
import org.monarchinitiative.loinc2hpocore.loinc.*;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class LoincAnnotationSerializerToTSVSingleFileTest {

    private static Map<LoincId, LOINC2HpoAnnotationImpl> testmap = new LinkedHashMap<>();
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
                .setIntermediateValueHpoTerm(normal, true)
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
                .addAnnotation(ecoli_snomed, new HpoTerm4TestOutcome(ecoli, false))
                .addAnnotation(staph_snomed, new HpoTerm4TestOutcome(staphaureus, false));

        LOINC2HpoAnnotationImpl annotation600 = loinc2HpoAnnotationBuilder.build();

        testmap.put(loincId, annotation600);

        temporaryPath = new File(temporaryFolder, "tempfile").getAbsolutePath();
    }


    @Test
    public void serialize() throws Exception {
        LoincAnnotationSerializer serializer = new LoincAnnotationSerializerToTSVSingleFile(null);
        serializer.serialize(testmap, temporaryPath);
        BufferedReader reader = new BufferedReader(new FileReader(temporaryPath));
        String content = StringUtils.join(reader.lines().collect(Collectors.toList()), "\n");


        String temporaryPath2 =
                new File(temporaryFolder, "tempfile2").getAbsolutePath();
        LOINC2HpoAnnotationImpl.to_csv_file(testmap, temporaryPath2);
        reader = new BufferedReader(new FileReader(temporaryPath2));
        String content2 =
                StringUtils.join(reader.lines().collect(Collectors.toList()),
                        "\n");
        assertEquals(content, content2);
    }

    @Test
    public void parse() throws Exception {
        LoincAnnotationSerializer serializer = new LoincAnnotationSerializerToTSVSingleFile(null);
        serializer.serialize(testmap, temporaryPath);
        LoincAnnotationSerializerToTSVSingleFile serilizer = new LoincAnnotationSerializerToTSVSingleFile(null);
        Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap = serilizer.parse(temporaryPath);
        assertNotNull(annotationMap);
        assertEquals(2, annotationMap.size());

        Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap2 =
                LOINC2HpoAnnotationImpl.from_csv(temporaryPath);
        assertNotNull(annotationMap2);
        assertEquals(2, annotationMap2.size());

        assertEquals(annotationMap.get(new LoincId("15074-8")).toString(),
                annotationMap2.get(new LoincId("15074-8")).toString());
        assertEquals(annotationMap.get(new LoincId("600-7")).toString(),
                annotationMap2.get(new LoincId("600-7")).toString());

    }

}