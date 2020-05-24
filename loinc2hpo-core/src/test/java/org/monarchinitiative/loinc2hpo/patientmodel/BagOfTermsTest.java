package org.monarchinitiative.loinc2hpo.patientmodel;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.ImmutableOntology;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;


public class BagOfTermsTest {

    private final Ontology hpo = mock(ImmutableOntology.class);
    private final String patientId = "patient001";
    private final String HP_PREFIX = "HP";

    @Test
    public void getPatient() {
        BagOfTerms patient1 = new BagOfTerms(patientId, hpo);
        assertEquals(patient1.getPatient(), patientId);
    }

    @Test
    public void getOriginalTerms() {
        BagOfTerms patient1 = new BagOfTerms(patientId, hpo);
        assertEquals(patient1.getOriginalTerms().size(), 0);
        patient1.addTerm(TermId.of(HP_PREFIX, "0003074"));
        assertEquals(patient1.getOriginalTerms().size(), 1);
        patient1.addTerm(TermId.of(HP_PREFIX, "0011297"));
        assertEquals(patient1.getOriginalTerms().size(), 2);
    }

    @Test
    public void getInferedTerms() {
        BagOfTerms patient1 = new BagOfTerms(patientId, hpo);
        patient1.addTerm(TermId.of(HP_PREFIX, "0003074"));
        patient1.addTerm(TermId.of(HP_PREFIX, "0011297"));
        assertEquals(patient1.getInferedTerms().size(), 0);
    }

}