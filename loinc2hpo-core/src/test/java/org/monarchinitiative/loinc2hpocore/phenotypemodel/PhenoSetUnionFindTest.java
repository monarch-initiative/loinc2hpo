package org.monarchinitiative.loinc2hpocore.phenotypemodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.annotationmodel.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;


public class PhenoSetUnionFindTest {

    private Map<String, Term> hpoTermMap;
    private Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap;
    private PhenoSetUnionFind unionFind;

    @BeforeEach
    public void setup(){

        LoincId caliumTest = mock(LoincId.class);
        Term hypocapnia = Term.of(TermId.of("HP:000012"), "Hypocapnia");
        Term hypercapnia = Term.of(TermId.of("HP:000013"), "Hypercapnia");
        LoincId nitritTest = mock(LoincId.class);
        Term nitrituria = Term.of(TermId.of("HP:0000043"), "Nitrituria");

        hpoTermMap =
                Stream.of(hypocapnia, hypercapnia, nitrituria).collect(Collectors.toMap(e -> e.getName(), e-> e));
        LOINC2HpoAnnotationImpl annotation1 =
                new LOINC2HpoAnnotationImpl.Builder()
                .setLoincId(caliumTest)
                .setLowValueHpoTerm(hypocapnia.getId())
                .setHighValueHpoTerm(hypercapnia.getId())
                .build();
        LOINC2HpoAnnotationImpl annotation2 =
                new LOINC2HpoAnnotationImpl.Builder()
                .setLoincId(nitritTest)
                .setPosValueHpoTerm(nitrituria.getId())
                .build();
        annotationMap = new HashMap<>();
        annotationMap.put(caliumTest, annotation1);
        annotationMap.put(nitritTest, annotation2);

        Set<TermId> termIdSet = hpoTermMap.values().stream().map(Term::getId).collect(Collectors.toSet());
        unionFind = new PhenoSetUnionFind(termIdSet, annotationMap);

    }

    @Test
    public void checkDataStructureSetUpCorrectly() {
        Term term1 = hpoTermMap.get("Hypocapnia");
        Term term2 = hpoTermMap.get("Hypercapnia");
        assertTrue(unionFind.getUnionFind().inSameSet(term1.getId(), term2.getId()));
        Term term3 = hpoTermMap.get("Nitrituria");
        assertFalse(unionFind.getUnionFind().inSameSet(term1.getId(), term3.getId()));
    }

}