package org.monarchinitiative.loinc2hpo.testresult;

import org.hl7.fhir.dstu3.model.Patient;
import org.jgrapht.alg.util.UnionFind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PatientSummaryImplTest {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private UnionFind<TermId> hpoTermUnionFind;

    private PatientSummary patientSummary;
    private Patient patient;
    private Term hyperglycemia;
    private Term hypoglycemia;
    private Term low_ferritin;

    @BeforeEach
    public void setup() throws Exception{

        LoincId glucose = new LoincId("15074-7");
        LOINC2HpoAnnotationImpl glucoseAnnote =
                mock(LOINC2HpoAnnotationImpl.class);
        LoincId ferritin = new LoincId("2276-4");
        LOINC2HpoAnnotationImpl ferritinAnnotation =
                mock(LOINC2HpoAnnotationImpl.class);
        Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap = new HashMap<>();
        annotationMap.put(glucose, glucoseAnnote);
        annotationMap.put(ferritin, ferritinAnnotation);
        hyperglycemia = Term.of(TermId.of("HP:0003074"), "Hyperglycemia");
        hypoglycemia = Term.of(TermId.of("HP:0001943"), "Hypoglycemia");
        low_ferritin = Term.of(TermId.of("HP:0012343"), "Decreased serum " +
                "ferritin");

        hpoTermUnionFind = mock(UnionFind.class);
        when(hpoTermUnionFind.inSameSet(hyperglycemia.getId(),
                hypoglycemia.getId())).thenReturn(true);
        when(hpoTermUnionFind.inSameSet(hypoglycemia.getId(),
                hyperglycemia.getId())).thenReturn(true);
        when(hpoTermUnionFind.inSameSet(hypoglycemia.getId(),
                low_ferritin.getId())).thenReturn(false);
        when(hpoTermUnionFind.inSameSet(hyperglycemia.getId(),
                low_ferritin.getId())).thenReturn(false);
        when(hpoTermUnionFind.inSameSet(low_ferritin.getId(),
                hypoglycemia.getId())).thenReturn(false);
        when(hpoTermUnionFind.inSameSet(low_ferritin.getId(),
                hyperglycemia.getId())).thenReturn(false);


        patient = mock(Patient.class);


        LabTest test1 = new LabTestImpl.Builder()
                .effectiveStart(dateFormat.parse("2016-10-20 10:30:00"))
                .loincId(new LoincId("15074-7"))
                .outcome(new HpoTerm4TestOutcome(hyperglycemia.getId(), false))
                .patient(patient)
                .resourceId("unknown resource id")
                .build();

        LabTest test2 = new LabTestImpl.Builder()
                .effectiveStart(dateFormat.parse("2016-10-21 10:30:00"))
                .loincId(new LoincId("2276-4"))
                .outcome(new HpoTerm4TestOutcome(low_ferritin.getId(), false))
                .patient(patient)
                .resourceId("unknown resource id")
                .build();

        patientSummary = new PatientSummaryImpl(patient, hpoTermUnionFind);
        patientSummary.addTest(test1);
        patientSummary.addTest(test2);
    }
    @Test
    public void patient() {
        assertNotNull(patientSummary.patient());
    }

    @Test
    public void addTest() throws Exception {
        LabTest test2 = new LabTestImpl.Builder()
                .effectiveStart(dateFormat.parse("2017-10-20 10:30:00"))
                .loincId(new LoincId("15074-7"))
                .outcome(new HpoTerm4TestOutcome(hyperglycemia.getId(), false))
                .patient(patient)
                .resourceId("unknown resource id")
                .build();
        patientSummary.addTest(test2);
    }

    @Test
    public void tests() throws Exception {
        LabTest test2 = new LabTestImpl.Builder()
                .effectiveStart(dateFormat.parse("2017-10-20 10:30:00"))
                .loincId(new LoincId("15074-7"))
                .outcome(new HpoTerm4TestOutcome(hypoglycemia.getId(), false))
                .patient(patient)
                .resourceId("unknown resource id")
                .build();
        patientSummary.addTest(test2);
        assertEquals(3, patientSummary.tests().size());

        LabTest test3 = new LabTestImpl.Builder()
                .effectiveStart(dateFormat.parse("2018-10-20 10:30:00"))
                .loincId(new LoincId("15074-7"))
                .outcome(new HpoTerm4TestOutcome(hyperglycemia.getId(), false))
                .patient(patient)
                .resourceId("unknown resource id")
                .build();
        patientSummary.addTest(test3);
        assertEquals(4, patientSummary.tests().size());
    }

    @Test
    public void phenoAt() throws Exception {

        LabTest test2 = new LabTestImpl.Builder()
                .effectiveStart(dateFormat.parse("2017-10-20 10:30:00"))
                .loincId(new LoincId("15074-7"))
                .outcome(new HpoTerm4TestOutcome(hypoglycemia.getId(), false))
                .patient(patient)
                .resourceId("unknown resource id")
                .build();
        patientSummary.addTest(test2);
        assertEquals(3, patientSummary.tests().size());

        assertEquals(2, patientSummary.timeLines().size());
    }

    @Test
    public void phenoPersistedDuring() throws Exception {
        LabTest test2 = new LabTestImpl.Builder()
                .effectiveStart(dateFormat.parse("2017-10-20 10:30:00"))
                .loincId(new LoincId("15074-7"))
                .outcome(new HpoTerm4TestOutcome(hypoglycemia.getId(), false))
                .patient(patient)
                .resourceId("unknown resource id")
                .build();
        patientSummary.addTest(test2);
        assertEquals(2, patientSummary.phenoPersistedDuring(dateFormat.parse("2016-10-22 10:00:00"), dateFormat.parse("2016-12-31 00:00:00")).size());
        //patientSummary1.phenoPersistedDuring(dateFormat.parse("2016-10-22 10:00:00"), dateFormat.parse("2016-12-31 00:00:00")).stream().forEach(p -> System.out.println(p.abnormality().getName()));
    }

    @Test
    public void phenoOccurredDuring() throws Exception {

        assertEquals(1, patientSummary.phenoOccurredDuring(dateFormat.parse("2015-12-31 00:00:00"), dateFormat.parse("2016-10-20 11:59:59")).size());
        assertEquals(2, patientSummary.phenoOccurredDuring(dateFormat.parse("2015-12-31 00:00:00"), dateFormat.parse("2016-10-22 11:59:59")).size());
        LabTest test2 = new LabTestImpl.Builder()
                .effectiveStart(dateFormat.parse("2017-10-20 10:30:00"))
                .loincId(new LoincId("15074-7"))
                .outcome(new HpoTerm4TestOutcome(hypoglycemia.getId(), false))
                .patient(patient)
                .resourceId("unknown resource id")
                .build();
        patientSummary.addTest(test2);
        assertEquals(3, patientSummary.phenoOccurredDuring(dateFormat.parse("2015-12-31 00:00:00"), dateFormat.parse("2018-10-22 11:59:59")).size());
    }

    @Test
    public void phenoSinceBorn() throws Exception {
        LabTest test2 = new LabTestImpl.Builder()
                .effectiveStart(dateFormat.parse("2017-10-20 10:30:00"))
                .loincId(new LoincId("15074-7"))
                .outcome(new HpoTerm4TestOutcome(hypoglycemia.getId(), false))
                .patient(patient)
                .resourceId("unknown resource id")
                .build();
        patientSummary.addTest(test2);
        assertEquals(3, patientSummary.phenoSinceBorn().size());
    }

}