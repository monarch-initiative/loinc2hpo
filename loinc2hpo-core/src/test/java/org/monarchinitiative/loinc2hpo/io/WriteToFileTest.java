package org.monarchinitiative.loinc2hpo.io;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.monarchinitiative.loinc2hpo.loinc.*;

import java.util.Map;

public class WriteToFileTest {

    @JsonSerialize(keyUsing = LoincIdJsonSerializer.class)
    Map<LoincId, String> idSer;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();


    @Test
    public void writeToFile() throws Exception {
    }

    @Test
    public void appendToFile() throws Exception {
    }
/**
    @Test
    public void serialize() throws Exception {

        Map<String, Integer> testmap = new HashMap<>();
        testmap.put("New York", 100);
        testmap.put("Connecticut", 25);
        //String path = "loinc2hpo/loinc2hpo-core/src/resource/serializedmap.sl";
        //String path = "./src/test/resources/serializedmap.sl";
        String path = tempFolder.newFile("serializedmap.sl").getPath();
        WriteToFile.serialize(testmap, path);
        //String path = WriteToFile.class.getClassLoader().getResource("json").getPath();
        //System.out.println(path);
    }

    @Test
    public void deserialize() throws Exception {

        Map<String, Integer> testmap = new HashMap<>();
        testmap.put("New York", 100);
        testmap.put("Connecticut", 25);
        //String path = "loinc2hpo/loinc2hpo-core/src/resource/serializedmap.sl";
        //String path = "./src/test/resources/serializedmap.sl";
        String path = tempFolder.newFile("serializedmap.sl").getPath();
        WriteToFile.serialize(testmap, path);

        Map<String, Integer> deserializedmap = WriteToFile.deserialize(path);
        assertNotNull(testmap);
        assertEquals(2, deserializedmap.size());
        assertEquals(100, deserializedmap.get("New York").intValue());
    }

    @Test
    @Ignore("It will fail because one class in ontolib is not yet serializable")
    public void testSerializeAnnotation() throws Exception{

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
        Map<LoincId, LOINC2HpoAnnotationImpl> testmap = new HashMap<>();
        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        HpoTerm low = hpoTermMap.get("Hypoglycemia");
        HpoTerm normal = hpoTermMap.get("Abnormality of blood glucose concentration");
        HpoTerm hi = hpoTermMap.get("Hyperglycemia");

        Map<String, Code> internalCodes = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);
        LOINC2HpoAnnotationImpl glucoseAnnotation = new LOINC2HpoAnnotationImpl(loincId, loincScale);
        glucoseAnnotation.addAnnotation(internalCodes.get("L"), new HpoTerm4TestOutcome(low, false))
                .addAnnotation(internalCodes.get("N"), new HpoTerm4TestOutcome(normal, true))
                .addAnnotation(internalCodes.get("A"), new HpoTerm4TestOutcome(normal, false))
                .addAnnotation(internalCodes.get("H"), new HpoTerm4TestOutcome(hi, false));
        testmap.put(loincId, glucoseAnnotation);



        loincId = new LoincId("600-7");
        loincScale = LoincScale.string2enum("Nom");
        HpoTerm forCode1 = hpoTermMap.get("Recurrent E. coli infections");
        HpoTerm forCode2 = hpoTermMap.get("Recurrent Staphylococcus aureus infections");
        HpoTerm positive = hpoTermMap.get("Recurrent bacterial infections");

        Code code1 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("112283007");
        Code code2 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("3092008");

        LOINC2HpoAnnotationImpl bacterialAnnotation = new LOINC2HpoAnnotationImpl(loincId, loincScale)
                .addAnnotation(code1, new HpoTerm4TestOutcome(forCode1, false))
                .addAnnotation(code2, new HpoTerm4TestOutcome(forCode2, false))
                .addAnnotation(internalCodes.get("P"), new HpoTerm4TestOutcome(positive, false));

        testmap.put(loincId, bacterialAnnotation);



        //serialize
        String serializedFile = tempFolder.newFile("testAnnotations.ser").getAbsolutePath();
        WriteToFile.serialize(testmap, serializedFile);

        //deserialize
        Map<LoincId, LOINC2HpoAnnotationImpl> deserializedAnnotations = WriteToFile.deserialize(serializedFile);
        assertNotNull(deserializedAnnotations);
        assertEquals(testmap.size(), deserializedAnnotations.size());
        assertEquals(testmap.get(new LoincId("600-7")).loincInterpretationToHPO(code1).getId(), deserializedAnnotations.get(new LoincId("600-7")).loincInterpretationToHPO(code1).getId());


    }

    @Test
    public void testToJson() throws Exception{

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
        Map<LoincId, LOINC2HpoAnnotationImpl> testmap = new HashMap<>();
        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        TermId low = hpoTermMap.get("Hypoglycemia").getId();
        TermId normal = hpoTermMap.get("Abnormality of blood glucose concentration").getId();
        TermId hi = hpoTermMap.get("Hyperglycemia").getId();

        Map<String, Code> internalCodes = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);
        LOINC2HpoAnnotationImpl glucoseAnnotation = new LOINC2HpoAnnotationImpl(loincId, loincScale);
        glucoseAnnotation.addAnnotation(internalCodes.get("L"), new HpoTerm4TestOutcome(low, false))
                .addAnnotation(internalCodes.get("N"), new HpoTerm4TestOutcome(normal, true))
                .addAnnotation(internalCodes.get("A"), new HpoTerm4TestOutcome(normal, false))
                .addAnnotation(internalCodes.get("H"), new HpoTerm4TestOutcome(hi, false));
        testmap.put(loincId, glucoseAnnotation);



        loincId = new LoincId("600-7");
        loincScale = LoincScale.string2enum("Nom");
        TermId forCode1 = hpoTermMap.get("Recurrent E. coli infections").getId();
        TermId forCode2 = hpoTermMap.get("Recurrent Staphylococcus aureus infections").getId();
        TermId positive = hpoTermMap.get("Recurrent bacterial infections").getId();

        Code code1 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("112283007");
        Code code2 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("3092008");

        LOINC2HpoAnnotationImpl bacterialAnnotation = new LOINC2HpoAnnotationImpl(loincId, loincScale)
                .addAnnotation(code1, new HpoTerm4TestOutcome(forCode1, false))
                .addAnnotation(code2, new HpoTerm4TestOutcome(forCode2, false))
                .addAnnotation(internalCodes.get("P"), new HpoTerm4TestOutcome(positive, false));

        testmap.put(loincId, bacterialAnnotation);

        WriteToFile.toJson(testmap);

        idSer = new HashMap<>();
        idSer.put(new LoincId("600-7"), "Bacterial infection");
        idSer.put(new LoincId("15074-8"), "Glucose in blood");
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(idSer);
        System.out.println(jsonResult);

    }

    @Test
    public void testFromTSV() throws Exception{
        Map<String, HpoTerm> hpoTermMapSerialize;
        Map<TermId, HpoTerm> hpoTermMapDeserialize;
        String hpo_obo = FhirObservationAnalyzerTest.class.getClassLoader().getResource("obo/hp.obo").getPath();
        HpoOboParser hpoOboParser = new HpoOboParser(new File(hpo_obo));
        HpoOntology hpo = null;
        try {
            hpo = hpoOboParser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImmutableMap.Builder<String,HpoTerm> termmapSerialize = new ImmutableMap.Builder<>();
        ImmutableMap.Builder<TermId, HpoTerm> termMapDeserialize = new ImmutableMap.Builder<>();
        if (hpo !=null) {
            List<HpoTerm> res = hpo.getTermMap().values().stream().distinct()
                    .collect(Collectors.toList());
            res.forEach( term -> termmapSerialize.put(term.getName(),term));
            res.forEach( term -> termMapDeserialize.put(term.getId(), term));

        }

        hpoTermMapSerialize = termmapSerialize.build();
        hpoTermMapDeserialize = termMapDeserialize.build();

        Map<LoincId, LOINC2HpoAnnotationImpl> testmap = new HashMap<>();
        LoincId loincId = new LoincId("15074-8");
        LoincScale loincScale = LoincScale.string2enum("Qn");
        TermId low = hpoTermMapSerialize.get("Hypoglycemia").getId();
        TermId normal = hpoTermMapSerialize.get("Abnormality of blood glucose concentration").getId();
        TermId hi = hpoTermMapSerialize.get("Hyperglycemia").getId();

        Map<String, Code> internalCodes = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);
        LOINC2HpoAnnotationImpl glucoseAnnotation = new LOINC2HpoAnnotationImpl(loincId, loincScale);
        glucoseAnnotation.addAnnotation(internalCodes.get("L"), new HpoTerm4TestOutcome(low, false))
                .addAnnotation(internalCodes.get("N"), new HpoTerm4TestOutcome(normal, true))
                .addAnnotation(internalCodes.get("A"), new HpoTerm4TestOutcome(normal, false))
                .addAnnotation(internalCodes.get("H"), new HpoTerm4TestOutcome(hi, false));
        testmap.put(loincId, glucoseAnnotation);



        loincId = new LoincId("600-7");
        loincScale = LoincScale.string2enum("Nom");
        TermId forCode1 = hpoTermMapSerialize.get("Recurrent E. coli infections").getId();
        TermId forCode2 = hpoTermMapSerialize.get("Recurrent Staphylococcus aureus infections").getId();
        TermId positive = hpoTermMapSerialize.get("Recurrent bacterial infections").getId();

        Code code1 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("112283007");
        Code code2 = Code.getNewCode().setSystem("http://snomed.info/sct").setCode("3092008");

        LOINC2HpoAnnotationImpl bacterialAnnotation = new LOINC2HpoAnnotationImpl(loincId, loincScale)
                .addAnnotation(code1, new HpoTerm4TestOutcome(forCode1, false))
                .addAnnotation(code2, new HpoTerm4TestOutcome(forCode2, false))
                .addAnnotation(internalCodes.get("P"), new HpoTerm4TestOutcome(positive, false));

        testmap.put(loincId, bacterialAnnotation);

        //testmap.entrySet().forEach(System.out::println);

        String path = tempFolder.newFile("testoutput.tsv").getPath();
        //WriteToFile.writeToFile("", path);
        //testmap.forEach((k, v) -> WriteToFile.appendToFile(v.toString(), path));

        WriteToFile.toTSV(path, testmap);
        BufferedReader reader = new BufferedReader(new FileReader(path));
        reader.lines().forEach(p -> System.out.println(p.toString()));
        reader.close();


        Map<LoincId, LOINC2HpoAnnotationImpl> deserialziedFromTSV = WriteToFile.fromTSV(path, hpoTermMapDeserialize);
        assertNotNull(deserialziedFromTSV);
        assertEquals(testmap.size(), deserialziedFromTSV.size());
        assertEquals(bacterialAnnotation.toString(), deserialziedFromTSV.get(new LoincId("600-7")).toString());
    }

**/

}