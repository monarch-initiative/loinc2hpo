package org.monarchinitiative.loinc2hpo.patientmodel;

import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.SharedResourceCollection;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermPrefix;

import static org.junit.Assert.*;

public class BagOfTermsTest {

    public static Ontology hpo;
    private final String patientId = "patient001";
    private final TermPrefix HP_PREFIX = new TermPrefix("HP");

    @BeforeClass
    public static void setup() throws Exception {
        hpo = SharedResourceCollection.resourceCollection.getHPO();
    }

    @Test
    public void getPatient() throws Exception {
        BagOfTerms patient1 = new BagOfTerms(patientId, hpo);
        assertEquals(patient1.getPatient(), patientId);
    }

    @Test
    public void getOriginalTerms() throws Exception {
        BagOfTerms patient1 = new BagOfTerms(patientId, hpo);
        assertEquals(patient1.getOriginalTerms().size(), 0);
        patient1.addTerm(new TermId(HP_PREFIX, "0003074"));
        assertEquals(patient1.getOriginalTerms().size(), 1);
        patient1.addTerm(new TermId(HP_PREFIX, "0011297"));
        assertEquals(patient1.getOriginalTerms().size(), 2);
    }

    @Test
    public void getInferedTerms() throws Exception {
        BagOfTerms patient1 = new BagOfTerms(patientId, hpo);
        patient1.addTerm(new TermId(HP_PREFIX, "0003074"));
        patient1.addTerm(new TermId(HP_PREFIX, "0011297"));
        assertEquals(patient1.getInferedTerms().size(), 0);
        patient1.infer();
        assertTrue(patient1.getInferedTerms().size() > 2);
        patient1.getInferedTerms().forEach(System.out::println);
    }

}