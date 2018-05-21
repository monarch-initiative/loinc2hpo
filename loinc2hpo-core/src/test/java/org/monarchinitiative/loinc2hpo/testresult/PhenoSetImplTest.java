package org.monarchinitiative.loinc2hpo.testresult;

import com.google.common.collect.ImmutableMap;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.io.LoincAnnotationSerializationFactory;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.formats.hpo.HpoTerm;
import org.monarchinitiative.phenol.io.obo.hpo.HpoOboParser;
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
    private static Map<String, HpoTerm> hpoTermMap;
    private static Map<TermId, HpoTerm> hpoTermMap2;
    private static Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap;
    private static PhenoSetUnionFind unionFind;

    @BeforeClass
    public static void setup() {
        String hpo_obo = "/Users/zhangx/git/human-phenotype-ontology/src/ontology/hp.obo";
        HpoOboParser hpoOboParser = new HpoOboParser(new File(hpo_obo));
        HpoOntology hpo = null;
        try {
            hpo = hpoOboParser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImmutableMap.Builder<String,HpoTerm> termmap = new ImmutableMap.Builder<>();
        ImmutableMap.Builder<TermId, HpoTerm> termMap2 = new ImmutableMap.Builder<>();
        if (hpo !=null) {
            List<HpoTerm> res = hpo.getTermMap().values().stream().distinct()
                    .collect(Collectors.toList());
            res.forEach( term -> {
                termmap.put(term.getName(),term);
                termMap2.put(term.getId(), term);
            });
        }
        hpoTermMap = termmap.build();
        hpoTermMap2 = termMap2.build();

        String tsvSingleFile = "/Users/zhangx/git/loinc2hpoAnnotation/Data/TSVSingleFile/annotations.tsv";
        try {
            annotationMap = LoincAnnotationSerializationFactory.parseFromFile(tsvSingleFile, hpoTermMap2, LoincAnnotationSerializationFactory.SerializationFormat.TSVSingleFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("annotationMap size:" + annotationMap.size());

        unionFind = new PhenoSetUnionFind(hpo.getTermMap().values().stream().collect(Collectors.toSet()), annotationMap);



    }
    @Test
    public void sameSet() throws Exception {

        PhenoSet phenoSet = new PhenoSetImpl(unionFind.getUnionFind());

        HpoTerm term1 = hpoTermMap.get("Hypocapnia");
        HpoTerm term2 = hpoTermMap.get("Hypercapnia");
        assertFalse(phenoSet.sameSet(term2));
        phenoSet.add(term1);
        assertTrue(phenoSet.sameSet(term2));


    }

    @Test
    public void hasOccurred() throws Exception {
        PhenoSet phenoSet = new PhenoSetImpl(unionFind.getUnionFind());

        HpoTerm term1 = hpoTermMap.get("Hypocapnia");
        HpoTerm term2 = hpoTermMap.get("Hypercapnia");
        assertFalse(phenoSet.hasOccurred(term1));
        phenoSet.add(term1);
        assertTrue(phenoSet.hasOccurred(term1));
        assertFalse(phenoSet.hasOccurred(term2));
        phenoSet.add(term2);
        assertTrue(phenoSet.hasOccurred(term2));
    }


}