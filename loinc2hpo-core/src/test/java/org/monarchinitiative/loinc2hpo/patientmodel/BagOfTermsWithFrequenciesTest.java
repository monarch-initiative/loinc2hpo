package org.monarchinitiative.loinc2hpo.patientmodel;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpo.SharedResourceCollection;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BagOfTermsWithFrequenciesTest {

    public static Ontology hpo;
    private final String patientId = "patient001";
    private final String HP_PREFIX = "HP";

    @BeforeAll
    public static void setup() throws Exception {
        hpo = SharedResourceCollection.resourceCollection.getHPO();
    }

    @Test
    public void addTerm() throws Exception {
        assertNotNull(hpo);
        BagOfTermsWithFrequencies bag1 = new BagOfTermsWithFrequencies(patientId, hpo);
        assertNotNull(bag1);
        //add one term:
        bag1.addTerm(TermId.of(HP_PREFIX, "0003074"), 5);
        assertEquals(bag1.getOriginalTermCounts().size(), 1);

        bag1.addTerm(TermId.of(HP_PREFIX, "0011297"), 1);
        assertEquals(bag1.getOriginalTermCounts().size(), 2);

        bag1.addTerm(TermId.of(HP_PREFIX, "0040064"), 1);
        assertEquals(bag1.getOriginalTermCounts().size(), 3);


    }

    @Test
    public void infer() throws Exception {

        assertNotNull(hpo);
        BagOfTermsWithFrequencies bag1 = new BagOfTermsWithFrequencies(patientId, hpo);
        assertNotNull(bag1);
        //add one term:hyperglycemia
        bag1.addTerm(TermId.of(HP_PREFIX, "0003074"), 5);
        assertEquals(bag1.getOriginalTermCounts().size(), 1);

        bag1.addTerm(TermId.of(HP_PREFIX, "0011297"), 3);

        bag1.addTerm(TermId.of(HP_PREFIX, "0040064"), 1);

        assertEquals(bag1.getOriginalTermCounts().size(), 3);

        bag1.infer();

        Map<TermId, Integer> inferred = bag1.getInferredTermCounts();
        //"All" should be counted 9 times
        TermId all = TermId.of(HP_PREFIX, "0000001");
        assertEquals(inferred.get(all).longValue(), 9);

        TermId phenotypicAbnormality = TermId.of(HP_PREFIX, "0000118");
        assertEquals(inferred.get(phenotypicAbnormality).longValue(), 9);

        TermId abnormalityOfLimbs = TermId.of(HP_PREFIX, "0040064");
        assertEquals(inferred.get(abnormalityOfLimbs).longValue(), 4);

        TermId abnormalGlucoseHomeostasis = TermId.of(HP_PREFIX, "0011014");
        assertEquals(inferred.get(abnormalGlucoseHomeostasis).longValue(), 5);

//        bag1.getInferredTermCounts().entrySet().forEach(e ->
//                System.out.println(bag1.getPatientId() + "\t" + e.getKey() + "\t" + e.getValue()));

    }

}