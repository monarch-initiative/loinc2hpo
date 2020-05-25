package org.monarchinitiative.loinc2hpogui.loinc;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.codesystems.InternalCode;
import org.monarchinitiative.loinc2hpocore.codesystems.InternalCodeSystem;
import org.monarchinitiative.loinc2hpocore.io.WriteToFile;
import org.monarchinitiative.loinc2hpocore.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.loinc2hpocore.loinc.LoincScale;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


public class LOINC2HpoAnnotationImplTest {

    @TempDir
    public File temporaryFolder;

    private static Map<String, Term> hpoTermMap = new HashMap<>();


    @BeforeAll
    public static void setUp(){

        List<Term> terms = Stream.of(
                Term.of("HP:0001943", "Hypoglycemia"),
                Term.of("HP:0011015", "Abnormality of blood glucose " +
                        "concentration"),
                Term.of("HP:0003074", "Hyperglycemia"),
                Term.of("HP:0002740", "Recurrent E. coli infections" ),
                Term.of("HP:0002726", "Recurrent Staphylococcus aureus infections"),
                Term.of("HP:0002718", "Recurrent bacterial infections")
        ).collect(Collectors.toList());

        hpoTermMap = terms.stream().collect(Collectors.toMap(Term::getName,
                t-> t));
    }

    @Test
    public void testToString() throws Exception {

        Map<LoincId, LOINC2HpoAnnotationImpl> testmap = new HashMap<>();

        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        Term low = hpoTermMap.get("Hypoglycemia");
        Term normal = hpoTermMap.get("Abnormality of blood glucose concentration");
        Term hi = hpoTermMap.get("Hyperglycemia");

        LOINC2HpoAnnotationImpl glucoseAnnotation = new LOINC2HpoAnnotationImpl.Builder()
                .setLoincId(loincId)
                .setLoincScale(loincScale)
                .setLowValueHpoTerm(low.getId())
                .setIntermediateValueHpoTerm(normal.getId(), true)
                .setHighValueHpoTerm(hi.getId())
                .build();
        testmap.put(loincId, glucoseAnnotation);



        loincId = new LoincId("600-7");
        loincScale = LoincScale.string2enum("Nom");
        Term forCode1 = hpoTermMap.get("Recurrent E. coli infections");
        Term forCode2 = hpoTermMap.get("Recurrent Staphylococcus aureus infections");
        Term positive = hpoTermMap.get("Recurrent bacterial infections");

        Code code1 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("112283007");
        Code code2 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("3092008");

        LOINC2HpoAnnotationImpl bacterialAnnotation;
        LOINC2HpoAnnotationImpl.Builder bacterialBuilder =
                new LOINC2HpoAnnotationImpl.Builder();
        bacterialAnnotation = bacterialBuilder
                .setLoincId(loincId)
                .setLoincScale(loincScale)
                .addAdvancedAnnotation(code1, new HpoTerm4TestOutcome(forCode1.getId(), false))
                .addAdvancedAnnotation(code2, new HpoTerm4TestOutcome(forCode2.getId(), false))
                .addAdvancedAnnotation(InternalCodeSystem.getCode(InternalCode.POS),
                        new HpoTerm4TestOutcome(positive.getId(), false))
                .build();

        testmap.put(loincId, bacterialAnnotation);

        File temporaryFile = new File(temporaryFolder, "letters.txt");
        String path = temporaryFile.getAbsolutePath();
        WriteToFile.writeToFile("", path);
        testmap.forEach((k, v) -> {
            WriteToFile.appendToFile(v.toString(), path);
            WriteToFile.appendToFile("\n", path);
        });

        BufferedReader reader = new BufferedReader(new FileReader(path));

        String serialized =
                StringUtils.join(reader.lines().collect(Collectors.toList()), "\n");

        String content = "15074-8\tQn\tFHIR\tA\tHP:0011015\tfalse\tnull\tfalse\t0.0\tNA\tNA\tNA\tNA\n" +
                "15074-8\tQn\tFHIR\tN\tHP:0011015\ttrue\tnull\tfalse\t0.0\tNA\tNA\tNA\tNA\n" +
                "15074-8\tQn\tFHIR\tH\tHP:0003074\tfalse\tnull\tfalse\t0.0\tNA\tNA\tNA\tNA\n" +
                "15074-8\tQn\tFHIR\tL\tHP:0001943\tfalse\tnull\tfalse\t0.0\tNA\tNA\tNA\tNA\n" +
                "600-7\tNom\thttp://snomed.info/sct\t112283007\tHP:0002740\tfalse\tnull\tfalse\t0.0\tNA\tNA\tNA\tNA\n" +
                "600-7\tNom\tFHIR\tPOS\tHP:0002718\tfalse\tnull\tfalse\t0.0\tNA\tNA\tNA\tNA\n" +
                "600-7\tNom\thttp://snomed.info/sct\t3092008\tHP:0002726\tfalse\tnull\tfalse\t0.0\tNA\tNA\tNA\tNA";
        assertEquals(content, serialized);
    }

}