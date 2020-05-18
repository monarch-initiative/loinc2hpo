package org.monarchinitiative.loinc2hpo.testresult;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpo.ResourceCollection;
import org.monarchinitiative.loinc2hpo.SharedResourceCollection;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Disabled
public class PhenoSetImplTest {

    private static Map<LoincId, LOINC2HpoAnnotationImpl> testmap = new HashMap<>();
    private static Map<String, Term> hpoTermMap;
    private static Map<TermId, Term> hpoTermMap2;
    private static Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap;
    private static PhenoSetUnionFind unionFind;

    @BeforeAll
    public static void setup() throws Exception {

        ResourceCollection resourceCollection = SharedResourceCollection.resourceCollection;

        hpoTermMap = resourceCollection.hpoTermMapFromName();
        hpoTermMap2 = resourceCollection.hpoTermMap();
        Ontology hpo = resourceCollection.getHPO();
        Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap = resourceCollection.annotationMap();

        unionFind = new PhenoSetUnionFind(hpo.getTermMap().values().stream().map(Term::getId).collect(Collectors.toSet()), annotationMap);

    }
    @Test
    public void sameSet() throws Exception {

        PhenoSet phenoSet = new PhenoSetImpl(unionFind.getUnionFind());

        Term term1 = hpoTermMap.get("Hypocapnia");
        Term term2 = hpoTermMap.get("Hypercapnia");
        assertFalse(phenoSet.sameSet(term2.getId()));
        phenoSet.add(term1.getId());
        assertTrue(phenoSet.sameSet(term2.getId()));


    }

    @Test
    public void hasOccurred() throws Exception {
        PhenoSet phenoSet = new PhenoSetImpl(unionFind.getUnionFind());

        Term term1 = hpoTermMap.get("Hypocapnia");
        Term term2 = hpoTermMap.get("Hypercapnia");
        assertFalse(phenoSet.hasOccurred(term1.getId()));
        phenoSet.add(term1.getId());
        assertTrue(phenoSet.hasOccurred(term1.getId()));
        assertFalse(phenoSet.hasOccurred(term2.getId()));
        phenoSet.add(term2.getId());
        assertTrue(phenoSet.hasOccurred(term2.getId()));
    }


}