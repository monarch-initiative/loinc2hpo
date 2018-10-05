package org.monarchinitiative.loinc2hpo.testresult;

import com.google.common.collect.ImmutableMap;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.ResourceCollection;
import org.monarchinitiative.loinc2hpo.SharedResourceCollection;
import org.monarchinitiative.loinc2hpo.io.LoincAnnotationSerializationFactory;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.obo.hpo.HpOboParser;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@Ignore
public class PhenoSetImplTest {

    private static Map<LoincId, LOINC2HpoAnnotationImpl> testmap = new HashMap<>();
    private static Map<String, Term> hpoTermMap;
    private static Map<TermId, Term> hpoTermMap2;
    private static Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap;
    private static PhenoSetUnionFind unionFind;

    @BeforeClass
    public static void setup() throws Exception {

        ResourceCollection resourceCollection = SharedResourceCollection.resourceCollection;

        hpoTermMap = resourceCollection.hpoTermMapFromName();
        hpoTermMap2 = resourceCollection.hpoTermMap();
        HpoOntology hpo = resourceCollection.getHPO();
        Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap = resourceCollection.annotationMap();

        unionFind = new PhenoSetUnionFind(hpo.getTermMap().values().stream().collect(Collectors.toSet()), annotationMap);

    }
    @Test
    public void sameSet() throws Exception {

        PhenoSet phenoSet = new PhenoSetImpl(unionFind.getUnionFind());

        Term term1 = hpoTermMap.get("Hypocapnia");
        Term term2 = hpoTermMap.get("Hypercapnia");
        assertFalse(phenoSet.sameSet(term2));
        phenoSet.add(term1);
        assertTrue(phenoSet.sameSet(term2));


    }

    @Test
    public void hasOccurred() throws Exception {
        PhenoSet phenoSet = new PhenoSetImpl(unionFind.getUnionFind());

        Term term1 = hpoTermMap.get("Hypocapnia");
        Term term2 = hpoTermMap.get("Hypercapnia");
        assertFalse(phenoSet.hasOccurred(term1));
        phenoSet.add(term1);
        assertTrue(phenoSet.hasOccurred(term1));
        assertFalse(phenoSet.hasOccurred(term2));
        phenoSet.add(term2);
        assertTrue(phenoSet.hasOccurred(term2));
    }


}