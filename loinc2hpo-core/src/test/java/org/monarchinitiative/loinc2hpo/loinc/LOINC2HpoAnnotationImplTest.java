package org.monarchinitiative.loinc2hpo.loinc;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.monarchinitiative.loinc2hpo.ResourceCollection;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;
import org.monarchinitiative.loinc2hpo.io.LoincAnnotationSerializerToTSVSingleFile;
import org.monarchinitiative.loinc2hpo.io.WriteToFile;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class LOINC2HpoAnnotationImplTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static ResourceCollection resourceCollection;
    private static Map<String, Term> hpoTermMap;


    @BeforeClass
    public static void setUp() throws Exception {
        resourceCollection = new ResourceCollection();
        resourceCollection.setHpoOboPath(LOINC2HpoAnnotationImplTest.class.getResource("/obo/hp_test.obo").getPath());
        hpoTermMap = resourceCollection.hpoTermMapFromName();
    }

    @Test
    public void testBuilderForBasicAnnotation() throws Exception {

        LOINC2HpoAnnotationImpl.Builder loinc2HpoAnnotationBuilder = new LOINC2HpoAnnotationImpl.Builder();

        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        Term low = hpoTermMap.get("Hypoglycemia");
        Term normal = hpoTermMap.get("Abnormality of blood glucose concentration");
        Term hi = hpoTermMap.get("Hyperglycemia");

        loinc2HpoAnnotationBuilder.setLoincId(loincId)
                .setLoincScale(loincScale)
                .setLowValueHpoTerm(low.getId())
                .setIntermediateValueHpoTerm(normal.getId())
                .setIntermediateNegated(true)
                .setHighValueHpoTerm(hi.getId());

        LOINC2HpoAnnotationImpl annotation15074 = loinc2HpoAnnotationBuilder.build();

        assertEquals("15074-8", annotation15074.getLoincId().toString());
        assertEquals("Qn", annotation15074.getLoincScale().toString());
        Map<String, Code> internalCodes = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);

        Code code4low = internalCodes.get("L");
        assertEquals(low.getId().getValue(), annotation15074.getCandidateHpoTerms().get(code4low).getId().getValue());
        assertEquals(false, annotation15074.getCandidateHpoTerms().get(code4low).isNegated());

        Code code4high = internalCodes.get("H");
        assertEquals(hi.getId().getValue(), annotation15074.getCandidateHpoTerms().get(code4high).getId().getValue());
        assertEquals(false, annotation15074.getCandidateHpoTerms().get(code4high).isNegated());

        Code code4normal = internalCodes.get("N");
        assertEquals(normal.getId().getValue(), annotation15074.getCandidateHpoTerms().get(code4normal).getId().getValue());
        assertEquals(true, annotation15074.getCandidateHpoTerms().get(code4normal).isNegated());

        Code code4Pos = internalCodes.get("POS");
        assertNull(annotation15074.getCandidateHpoTerms().get(code4Pos));

        Code code4NP = internalCodes.get("NEG");
        assertNull(annotation15074.getCandidateHpoTerms().get(code4NP));

    }

    @Test
    public void testBuilderForAdvanced() throws Exception {

        LOINC2HpoAnnotationImpl.Builder loinc2HpoAnnotationBuilder = new LOINC2HpoAnnotationImpl.Builder();

         LoincId loincId = new LoincId("600-7");
         LoincScale loincScale = LoincScale.string2enum("Nom");
         Term forCode1 = hpoTermMap.get("Recurrent E. coli infections");
         Term forCode2 = hpoTermMap.get("Recurrent Staphylococcus aureus infections");
         Term positive = hpoTermMap.get("Recurrent bacterial infections");

         TermId low = TermId.of("HP:00000123");

         Code code1 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("112283007");
         Code code2 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("3092008");
         Code code3 = Code.getNewCode().setSystem("FHIR").setCode("L");

         loinc2HpoAnnotationBuilder.setLoincId(loincId)
                 .setLoincScale(loincScale)
                 .setPosValueHpoTerm(positive.getId())
                 .addAdvancedAnnotation(code1, new HpoTerm4TestOutcome(forCode1.getId(), false))
                 .addAdvancedAnnotation(code2, new HpoTerm4TestOutcome(forCode2.getId(), false))
                 .addAdvancedAnnotation(code3, new HpoTerm4TestOutcome(low, true));

         LOINC2HpoAnnotationImpl annotation600 = loinc2HpoAnnotationBuilder.build();
         assertEquals("600-7", annotation600.getLoincId().toString());

        Map<String, Code> internalCodes = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);
        Code code4Pos = internalCodes.get("POS");
        assertEquals(positive.getId().getValue(), annotation600.getCandidateHpoTerms().get(code4Pos).getId().getValue());
        assertEquals(false, annotation600.getCandidateHpoTerms().get(code4Pos).isNegated());

        Code code4high = internalCodes.get("H");
        assertNull(annotation600.getCandidateHpoTerms().get(code4high));

        assertEquals(forCode1.getId().getValue(), annotation600.getCandidateHpoTerms().get(code1).getId().getValue());
        assertEquals(false, annotation600.getCandidateHpoTerms().get(code1).isNegated());

        assertEquals(forCode2.getId().getValue(), annotation600.getCandidateHpoTerms().get(code2).getId().getValue());
        assertEquals(false, annotation600.getCandidateHpoTerms().get(code2).isNegated());

        assertEquals(true, annotation600.getCandidateHpoTerms().get(code3).isNegated());

        System.out.println(new LoincAnnotationSerializerToTSVSingleFile(null).annotationToString(annotation600));

    }

    @Test
    @Ignore
    public void testSerializeBasicAnnotation() throws Exception {

        LOINC2HpoAnnotationImpl.Builder loinc2HpoAnnotationBuilder = new LOINC2HpoAnnotationImpl.Builder();

        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        Term low = hpoTermMap.get("Hypoglycemia");
        Term normal = hpoTermMap.get("Abnormality of blood glucose concentration");
        Term hi = hpoTermMap.get("Hyperglycemia");

        loinc2HpoAnnotationBuilder.setLoincId(loincId)
                .setLoincScale(loincScale)
                .setLowValueHpoTerm(low.getId())
                .setIntermediateValueHpoTerm(normal.getId())
                .setIntermediateNegated(true)
                .setHighValueHpoTerm(hi.getId());

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
                .setHighValueHpoTerm(positive.getId())
                .addAdvancedAnnotation(code1, new HpoTerm4TestOutcome(forCode1.getId(), false))
                .addAdvancedAnnotation(code2, new HpoTerm4TestOutcome(forCode2.getId(), false));

        LOINC2HpoAnnotationImpl annotation600 = loinc2HpoAnnotationBuilder.build();
        System.out.println(annotation600.getAdvancedAnnotationsString());
    }




}