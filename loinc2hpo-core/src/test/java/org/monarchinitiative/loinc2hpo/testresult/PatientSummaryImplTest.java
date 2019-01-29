package org.monarchinitiative.loinc2hpo.testresult;

import org.hl7.fhir.dstu3.model.Patient;
import org.jgrapht.alg.util.UnionFind;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.ResourceCollection;
import org.monarchinitiative.loinc2hpo.SharedResourceCollection;
import org.monarchinitiative.loinc2hpo.fhir.FhirResourceFaker;
import org.monarchinitiative.loinc2hpo.fhir.FhirResourceFakerImpl;
import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@Ignore
public class PatientSummaryImplTest {

    private PhenoSetTimeLine glucosetimeLine;
    private static Map<String, Term> hpoTermMap;
    private static Map<TermId, Term> hpoTermMap2;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static UnionFind<Term> hpoTermUnionFind;
    private static FhirResourceFaker resourceGenerator;
    private static List<Patient> randPatients;

    private static PatientSummary patientSummary1;
    private static Patient patient;

    @BeforeClass
    public static void setup() throws Exception{
        ResourceCollection resourceCollection = SharedResourceCollection.resourceCollection;

        hpoTermMap = resourceCollection.hpoTermMapFromName();
        hpoTermMap2 = resourceCollection.hpoTermMap();
        Ontology hpo = resourceCollection.getHPO();
        Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap = resourceCollection.annotationMap();

        hpoTermUnionFind = new PhenoSetUnionFind(hpo.getTermMap().values().stream().collect(Collectors.toSet()), annotationMap).getUnionFind();

        String path = "/Users/zhangx/Downloads/LOINC_2/LoincTableCore.csv";
        Map<LoincId, LoincEntry> loincEntryMap = LoincEntry.getLoincEntryList(path);
        resourceGenerator = new FhirResourceFakerImpl(loincEntryMap);
        patient = resourceGenerator.fakePatient();


        LabTest test1 = new LabTestImpl.Builder()
                .effectiveStart(dateFormat.parse("2016-10-20 10:30:00"))
                .loincId(new LoincId("15074-7"))
                .outcome(new HpoTerm4TestOutcome(hpoTermMap.get("Hyperglycemia"), false))
                .patient(patient)
                .resourceId("unknown resource id")
                .build();

        LabTest test2 = new LabTestImpl.Builder()
                .effectiveStart(dateFormat.parse("2016-10-21 10:30:00"))
                .loincId(new LoincId("2276-4"))
                .outcome(new HpoTerm4TestOutcome(hpoTermMap.get("Decreased serum ferritin"), false))
                .patient(patient)
                .resourceId("unknown resource id")
                .build();

        patientSummary1 = new PatientSummaryImpl(patient, hpoTermUnionFind);
        patientSummary1.addTest(test1);
        patientSummary1.addTest(test2);


    }
    @Test
    public void patient() throws Exception {
        assertNotNull(patientSummary1.patient());
    }

    @Test
    public void addTest() throws Exception {
        LabTest test2 = new LabTestImpl.Builder()
                .effectiveStart(dateFormat.parse("2017-10-20 10:30:00"))
                .loincId(new LoincId("15074-7"))
                .outcome(new HpoTerm4TestOutcome(hpoTermMap.get("Hypoglycemia"), false))
                .patient(patient)
                .resourceId("unknown resource id")
                .build();
        patientSummary1.addTest(test2);
    }

    @Test
    public void tests() throws Exception {
        LabTest test2 = new LabTestImpl.Builder()
                .effectiveStart(dateFormat.parse("2017-10-20 10:30:00"))
                .loincId(new LoincId("15074-7"))
                .outcome(new HpoTerm4TestOutcome(hpoTermMap.get("Hypoglycemia"), false))
                .patient(patient)
                .resourceId("unknown resource id")
                .build();
        patientSummary1.addTest(test2);
        assertEquals(3, patientSummary1.tests().size());

        LabTest test3 = new LabTestImpl.Builder()
                .effectiveStart(dateFormat.parse("2018-10-20 10:30:00"))
                .loincId(new LoincId("15074-7"))
                .outcome(new HpoTerm4TestOutcome(hpoTermMap.get("Hyperglycemia"), false))
                .patient(patient)
                .resourceId("unknown resource id")
                .build();
        patientSummary1.addTest(test3);
        assertEquals(4, patientSummary1.tests().size());
    }

    @Test
    public void phenoAt() throws Exception {

        LabTest test2 = new LabTestImpl.Builder()
                .effectiveStart(dateFormat.parse("2017-10-20 10:30:00"))
                .loincId(new LoincId("15074-7"))
                .outcome(new HpoTerm4TestOutcome(hpoTermMap.get("Hypoglycemia"), false))
                .patient(patient)
                .resourceId("unknown resource id")
                .build();
        patientSummary1.addTest(test2);
        assertEquals(3, patientSummary1.tests().size());

        assertEquals(2, patientSummary1.timeLines().size());

        //assertEquals("Hyperglycemia", patientSummary1.phenoAt(dateFormat.parse("2017-01-01 10:00:00")).get(0).abnormality().getName());

        //assertEquals("Hypoglycemia", patientSummary1.phenoAt(dateFormat.parse("2018-01-01 10:00:00")).get(0).abnormality().getName());
        //assertEquals(2, patientSummary1.phenoAt(dateFormat.parse("2017-01-01 10:00:00")).size());


    }

    @Test
    public void phenoPersistedDuring() throws Exception {
        LabTest test2 = new LabTestImpl.Builder()
                .effectiveStart(dateFormat.parse("2017-10-20 10:30:00"))
                .loincId(new LoincId("15074-7"))
                .outcome(new HpoTerm4TestOutcome(hpoTermMap.get("Hypoglycemia"), false))
                .patient(patient)
                .resourceId("unknown resource id")
                .build();
        patientSummary1.addTest(test2);
        assertEquals(2, patientSummary1.phenoPersistedDuring(dateFormat.parse("2016-10-22 10:00:00"), dateFormat.parse("2016-12-31 00:00:00")).size());
        //patientSummary1.phenoPersistedDuring(dateFormat.parse("2016-10-22 10:00:00"), dateFormat.parse("2016-12-31 00:00:00")).stream().forEach(p -> System.out.println(p.abnormality().getName()));
    }

    @Test
    public void phenoOccurredDuring() throws Exception {

        assertEquals(1, patientSummary1.phenoOccurredDuring(dateFormat.parse("2015-12-31 00:00:00"), dateFormat.parse("2016-10-20 11:59:59")).size());
        assertEquals(2, patientSummary1.phenoOccurredDuring(dateFormat.parse("2015-12-31 00:00:00"), dateFormat.parse("2016-10-22 11:59:59")).size());
        LabTest test2 = new LabTestImpl.Builder()
                .effectiveStart(dateFormat.parse("2017-10-20 10:30:00"))
                .loincId(new LoincId("15074-7"))
                .outcome(new HpoTerm4TestOutcome(hpoTermMap.get("Hypoglycemia"), false))
                .patient(patient)
                .resourceId("unknown resource id")
                .build();
        patientSummary1.addTest(test2);
        assertEquals(3, patientSummary1.phenoOccurredDuring(dateFormat.parse("2015-12-31 00:00:00"), dateFormat.parse("2018-10-22 11:59:59")).size());
    }

    @Test
    public void phenoSinceBorn() throws Exception {
        LabTest test2 = new LabTestImpl.Builder()
                .effectiveStart(dateFormat.parse("2017-10-20 10:30:00"))
                .loincId(new LoincId("15074-7"))
                .outcome(new HpoTerm4TestOutcome(hpoTermMap.get("Hypoglycemia"), false))
                .patient(patient)
                .resourceId("unknown resource id")
                .build();
        patientSummary1.addTest(test2);
        assertEquals(3, patientSummary1.phenoSinceBorn().size());
    }

}