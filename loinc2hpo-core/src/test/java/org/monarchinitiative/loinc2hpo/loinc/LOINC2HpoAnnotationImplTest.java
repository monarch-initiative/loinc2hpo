package org.monarchinitiative.loinc2hpo.loinc;

import com.google.common.collect.ImmutableMap;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;
import org.monarchinitiative.loinc2hpo.fhir.FhirObservationAnalyzerTest;
import org.monarchinitiative.loinc2hpo.io.WriteToFile;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.obo.hpo.HpoOboParser;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class LOINC2HpoAnnotationImplTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();



    @Test
    public void testToString() throws Exception {
        Map<String, Term> hpoTermMap;
        String hpo_obo = FhirObservationAnalyzerTest.class.getClassLoader().getResource("obo/hp.obo").getPath();
        HpoOboParser hpoOboParser = new HpoOboParser(new File(hpo_obo));
        HpoOntology hpo = null;
        try {
            hpo = hpoOboParser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImmutableMap.Builder<String,Term> termmap = new ImmutableMap.Builder<>();
        if (hpo !=null) {
            List<Term> res = hpo.getTermMap().values().stream().distinct()
                    .collect(Collectors.toList());
            res.forEach( term -> termmap.put(term.getName(),term));
        }
        hpoTermMap = termmap.build();
        Map<LoincId, LOINC2HpoAnnotationImpl> testmap = new HashMap<>();

        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        Term low = hpoTermMap.get("Hypoglycemia");
        Term normal = hpoTermMap.get("Abnormality of blood glucose concentration");
        Term hi = hpoTermMap.get("Hyperglycemia");

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
        Term forCode1 = hpoTermMap.get("Recurrent E. coli infections");
        Term forCode2 = hpoTermMap.get("Recurrent Staphylococcus aureus infections");
        Term positive = hpoTermMap.get("Recurrent bacterial infections");

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

        //testmap.entrySet().forEach(System.out::println);

        String path = temporaryFolder.newFile("testoutput.tsv").getPath();
        WriteToFile.writeToFile("", path);
        testmap.forEach((k, v) -> {
            WriteToFile.appendToFile(v.toString(), path);
            WriteToFile.appendToFile("\n", path);
        });
        //WriteToFile.appendToFile(glucoseAnnotation.toString(), path);

        BufferedReader reader = new BufferedReader(new FileReader(path));

        //System.out.println(LOINC2HpoAnnotationImpl.getHeaderAdvanced());
        //reader.lines().forEach(System.out::println);
        String content = "15074-8\tQn\tFHIR\tA\tHP:0011015\tfalse\tnull\tfalse\t0.0\tNA\tNA\tNA\tNA\n" +
                "15074-8\tQn\tFHIR\tN\tHP:0011015\ttrue\tnull\tfalse\t0.0\tNA\tNA\tNA\tNA\n" +
                "15074-8\tQn\tFHIR\tH\tHP:0003074\tfalse\tnull\tfalse\t0.0\tNA\tNA\tNA\tNA\n" +
                "15074-8\tQn\tFHIR\tL\tHP:0001943\tfalse\tnull\tfalse\t0.0\tNA\tNA\tNA\tNA\n" +
                "600-7\tNom\thttp://snomed.info/sct\t112283007\tHP:0002740\tfalse\tnull\tfalse\t0.0\tNA\tNA\tNA\tNA\n" +
                "600-7\tNom\tFHIR\tPOS\tHP:0002718\tfalse\tnull\tfalse\t0.0\tNA\tNA\tNA\tNA\n" +
                "600-7\tNom\thttp://snomed.info/sct\t3092008\tHP:0002726\tfalse\tnull\tfalse\t0.0\tNA\tNA\tNA\tNA";
        assertEquals(7, reader.lines().collect(Collectors.toList()).size());
    }

    @Test
    public void testBuilderForBasicAnnotation() throws Exception {


        Map<String, Term> hpoTermMap;
        String hpo_obo = FhirObservationAnalyzerTest.class.getClassLoader().getResource("obo/hp.obo").getPath();
        HpoOboParser hpoOboParser = new HpoOboParser(new File(hpo_obo));
        HpoOntology hpo = null;
        try {
            hpo = hpoOboParser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImmutableMap.Builder<String,Term> termmap = new ImmutableMap.Builder<>();
        if (hpo !=null) {
            List<Term> res = hpo.getTermMap().values().stream().distinct()
                    .collect(Collectors.toList());
            res.forEach( term -> termmap.put(term.getName(),term));
        }
        hpoTermMap = termmap.build();


        LOINC2HpoAnnotationImpl.Builder loinc2HpoAnnotationBuilder = new LOINC2HpoAnnotationImpl.Builder();

        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        Term low = hpoTermMap.get("Hypoglycemia");
        Term normal = hpoTermMap.get("Abnormality of blood glucose concentration");
        Term hi = hpoTermMap.get("Hyperglycemia");

        loinc2HpoAnnotationBuilder.setLoincId(loincId)
                .setLoincScale(loincScale)
                .setLowValueHpoTerm(low)
                .setIntermediateValueHpoTerm(normal)
                .setIntermediateNegated(true)
                .setHighValueHpoTerm(hi);

        LOINC2HpoAnnotationImpl annotation15074 = loinc2HpoAnnotationBuilder.build();

        assertEquals("15074-8", annotation15074.getLoincId().toString());
        assertEquals("Qn", annotation15074.getLoincScale().toString());
        Map<String, Code> internalCodes = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);

        Code code4low = internalCodes.get("L");
        assertEquals(low.getId().getIdWithPrefix(), annotation15074.getCandidateHpoTerms().get(code4low).getHpoTerm().getId().getIdWithPrefix());
        assertEquals(false, annotation15074.getCandidateHpoTerms().get(code4low).isNegated());

        Code code4high = internalCodes.get("H");
        assertEquals(hi.getId().getIdWithPrefix(), annotation15074.getCandidateHpoTerms().get(code4high).getHpoTerm().getId().getIdWithPrefix());
        assertEquals(false, annotation15074.getCandidateHpoTerms().get(code4high).isNegated());

        Code code4normal = internalCodes.get("N");
        assertEquals(normal.getId().getIdWithPrefix(), annotation15074.getCandidateHpoTerms().get(code4normal).getHpoTerm().getId().getIdWithPrefix());
        assertEquals(true, annotation15074.getCandidateHpoTerms().get(code4normal).isNegated());

        Code code4Pos = internalCodes.get("POS");
        assertNull(annotation15074.getCandidateHpoTerms().get(code4Pos));

        Code code4NP = internalCodes.get("NEG");
        assertNull(annotation15074.getCandidateHpoTerms().get(code4NP));

    }

    @Test
    public void testBuilderForAdvanced() throws Exception {
        Map<String, Term> hpoTermMap;
        String hpo_obo = FhirObservationAnalyzerTest.class.getClassLoader().getResource("obo/hp.obo").getPath();
        HpoOboParser hpoOboParser = new HpoOboParser(new File(hpo_obo));
        HpoOntology hpo = null;
        try {
            hpo = hpoOboParser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImmutableMap.Builder<String,Term> termmap = new ImmutableMap.Builder<>();
        if (hpo !=null) {
            List<Term> res = hpo.getTermMap().values().stream().distinct()
                    .collect(Collectors.toList());
            res.forEach( term -> termmap.put(term.getName(),term));
        }
        hpoTermMap = termmap.build();


        LOINC2HpoAnnotationImpl.Builder loinc2HpoAnnotationBuilder = new LOINC2HpoAnnotationImpl.Builder();

         LoincId loincId = new LoincId("600-7");
         LoincScale loincScale = LoincScale.string2enum("Nom");
         Term forCode1 = hpoTermMap.get("Recurrent E. coli infections");
         Term forCode2 = hpoTermMap.get("Recurrent Staphylococcus aureus infections");
         Term positive = hpoTermMap.get("Recurrent bacterial infections");

         Code code1 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("112283007");
         Code code2 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("3092008");

         loinc2HpoAnnotationBuilder.setLoincId(loincId)
                 .setLoincScale(loincScale)
                 .setPosValueHpoTerm(positive)
                 .addAdvancedAnnotation(code1, new HpoTerm4TestOutcome(forCode1, false))
                 .addAdvancedAnnotation(code2, new HpoTerm4TestOutcome(forCode2, false));

         LOINC2HpoAnnotationImpl annotation600 = loinc2HpoAnnotationBuilder.build();
         assertEquals("600-7", annotation600.getLoincId().toString());

        Map<String, Code> internalCodes = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);
        Code code4Pos = internalCodes.get("POS");
        assertEquals(positive.getId().getIdWithPrefix(), annotation600.getCandidateHpoTerms().get(code4Pos).getHpoTerm().getId().getIdWithPrefix());
        assertEquals(false, annotation600.getCandidateHpoTerms().get(code4Pos).isNegated());

        Code code4high = internalCodes.get("H");
        assertNull(annotation600.getCandidateHpoTerms().get(code4high));

        assertEquals(forCode1.getId().getIdWithPrefix(), annotation600.getCandidateHpoTerms().get(code1).getHpoTerm().getId().getIdWithPrefix());
        assertEquals(false, annotation600.getCandidateHpoTerms().get(code1).isNegated());

        assertEquals(forCode2.getId().getIdWithPrefix(), annotation600.getCandidateHpoTerms().get(code2).getHpoTerm().getId().getIdWithPrefix());
        assertEquals(false, annotation600.getCandidateHpoTerms().get(code2).isNegated());

    }

    @Test
    @Ignore
    public void testSerializeBasicAnnotation() throws Exception {
        Map<String, Term> hpoTermMap;
        String hpo_obo = FhirObservationAnalyzerTest.class.getClassLoader().getResource("obo/hp.obo").getPath();
        HpoOboParser hpoOboParser = new HpoOboParser(new File(hpo_obo));
        HpoOntology hpo = null;
        try {
            hpo = hpoOboParser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImmutableMap.Builder<String,Term> termmap = new ImmutableMap.Builder<>();
        if (hpo !=null) {
            List<Term> res = hpo.getTermMap().values().stream().distinct()
                    .collect(Collectors.toList());
            res.forEach( term -> termmap.put(term.getName(),term));
        }
        hpoTermMap = termmap.build();


        LOINC2HpoAnnotationImpl.Builder loinc2HpoAnnotationBuilder = new LOINC2HpoAnnotationImpl.Builder();

        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        Term low = hpoTermMap.get("Hypoglycemia");
        Term normal = hpoTermMap.get("Abnormality of blood glucose concentration");
        Term hi = hpoTermMap.get("Hyperglycemia");

        loinc2HpoAnnotationBuilder.setLoincId(loincId)
                .setLoincScale(loincScale)
                .setLowValueHpoTerm(low)
                .setIntermediateValueHpoTerm(normal)
                .setIntermediateNegated(true)
                .setHighValueHpoTerm(hi);

        LOINC2HpoAnnotationImpl annotation15074 = loinc2HpoAnnotationBuilder.build();

        System.out.println(LOINC2HpoAnnotationImpl.getHeaderBasic());
        System.out.println(annotation15074.getBasicAnnotationsString().trim());
    }

    @Test
    public void testSerializeEmptyBasicAnnotation() throws Exception {

        LOINC2HpoAnnotationImpl.Builder loinc2HpoAnnotationBuilder = new LOINC2HpoAnnotationImpl.Builder();

        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        loinc2HpoAnnotationBuilder.setLoincId(loincId)
                .setLoincScale(loincScale);

        LOINC2HpoAnnotationImpl annotationEmpty = loinc2HpoAnnotationBuilder.build();
        //System.out.println(annotationEmpty.getBasicAnnotationsString());
        assertEquals("15074-8	Qn	NA	NA	NA	false	NA	false	0.0	NA	NA	NA	NA", annotationEmpty.getBasicAnnotationsString().trim());

    }

    @Test
    @Ignore
    public void testSerializeAdvancedAnnotation() throws Exception {


        Map<String, Term> hpoTermMap;
        String hpo_obo = FhirObservationAnalyzerTest.class.getClassLoader().getResource("obo/hp.obo").getPath();
        HpoOboParser hpoOboParser = new HpoOboParser(new File(hpo_obo));
        HpoOntology hpo = null;
        try {
            hpo = hpoOboParser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImmutableMap.Builder<String,Term> termmap = new ImmutableMap.Builder<>();
        if (hpo !=null) {
            List<Term> res = hpo.getTermMap().values().stream().distinct()
                    .collect(Collectors.toList());
            res.forEach( term -> termmap.put(term.getName(),term));
        }
        hpoTermMap = termmap.build();


        LOINC2HpoAnnotationImpl.Builder loinc2HpoAnnotationBuilder = new LOINC2HpoAnnotationImpl.Builder();

        LoincId loincId = new LoincId("600-7");
        LoincScale loincScale = LoincScale.string2enum("Nom");
        Term forCode1 = hpoTermMap.get("Recurrent E. coli infections");
        Term forCode2 = hpoTermMap.get("Recurrent Staphylococcus aureus infections");
        Term positive = hpoTermMap.get("Recurrent bacterial infections");

        Code code1 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("112283007");
        Code code2 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("3092008");

        loinc2HpoAnnotationBuilder.setLoincId(loincId)
                .setLoincScale(loincScale)
                .setHighValueHpoTerm(positive)
                .addAdvancedAnnotation(code1, new HpoTerm4TestOutcome(forCode1, false))
                .addAdvancedAnnotation(code2, new HpoTerm4TestOutcome(forCode2, false));

        LOINC2HpoAnnotationImpl annotation600 = loinc2HpoAnnotationBuilder.build();
        System.out.println(annotation600.getAdvancedAnnotationsString());
    }




}