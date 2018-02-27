package org.monarchinitiative.loinc2hpo.loinc;

import com.github.phenomics.ontolib.formats.hpo.HpoOntology;
import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.github.phenomics.ontolib.io.obo.hpo.HpoOboParser;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;
import org.monarchinitiative.loinc2hpo.fhir.FhirObservationAnalyzerTest;
import org.monarchinitiative.loinc2hpo.io.WriteToFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class UniversalLoinc2HPOAnnotationTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();


    @Test
    public void testToString() throws Exception {
        Map<String, HpoTerm> hpoTermMap;
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
        Map<LoincId, UniversalLoinc2HPOAnnotation> testmap = new HashMap<>();
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



        loincId = new LoincId("600-7");
        loincScale = LoincScale.string2enum("Nom");
        TermId forCode1 = hpoTermMap.get("Recurrent E. coli infections").getId();
        TermId forCode2 = hpoTermMap.get("Recurrent Staphylococcus aureus infections").getId();
        TermId positive = hpoTermMap.get("Recurrent bacterial infections").getId();

        Code code1 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("112283007");
        Code code2 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("3092008");

        UniversalLoinc2HPOAnnotation bacterialAnnotation = new UniversalLoinc2HPOAnnotation(loincId, loincScale)
                .addAnnotation(code1, new HpoTermId4LoincTest(forCode1, false))
                .addAnnotation(code2, new HpoTermId4LoincTest(forCode2, false))
                .addAnnotation(internalCodes.get("P"), new HpoTermId4LoincTest(positive, false));

        testmap.put(loincId, bacterialAnnotation);

        //testmap.entrySet().forEach(System.out::println);

        String path = temporaryFolder.newFile("testoutput.tsv").getPath();
        WriteToFile.writeToFile("", path);
        testmap.forEach((k, v) -> {
            WriteToFile.appendToFile(v.toString(), path);
            WriteToFile.appendToFile("\n", path);
        });
        //WriteToFile.appendToFile(glucoseAnnotation.toString(), path);

        BufferedReader reader = new BufferedReader(new FileReader(path));

        //System.out.println(UniversalLoinc2HPOAnnotation.getHeader());
        //reader.lines().forEach(System.out::println);
        String content = "15074-8\tQn\thttp://jax.org/loinc2hpo\tL\tHP:0001943\tfalse\tnull\tfalse\t0.0\tnull\tnull\tnull\tnull\n" +
                "15074-8\tQn\thttp://jax.org/loinc2hpo\tN\tHP:0011015\ttrue\tnull\tfalse\t0.0\tnull\tnull\tnull\tnull\n" +
                "15074-8\tQn\thttp://jax.org/loinc2hpo\tH\tHP:0003074\tfalse\tnull\tfalse\t0.0\tnull\tnull\tnull\tnull\n" +
                "15074-8\tQn\thttp://jax.org/loinc2hpo\tA\tHP:0011015\tfalse\tnull\tfalse\t0.0\tnull\tnull\tnull\tnull\n" +
                "600-7\tNom\thttp://snomed.info/sct\t112283007\tHP:0002740\tfalse\tnull\tfalse\t0.0\tnull\tnull\tnull\tnull\n" +
                "600-7\tNom\thttp://jax.org/loinc2hpo\tP\tHP:0002718\tfalse\tnull\tfalse\t0.0\tnull\tnull\tnull\tnull\n" +
                "600-7\tNom\thttp://snomed.info/sct\t3092008\tHP:0002726\tfalse\tnull\tfalse\t0.0\tnull\tnull\tnull\tnull";
        assertEquals(7, reader.lines().collect(Collectors.toList()).size());
    }

}