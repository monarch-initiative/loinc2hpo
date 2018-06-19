package org.monarchinitiative.loinc2hpo;

import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.LoincPanel;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class ResourceCollectionTest {

    private static Map<LoincId, LoincEntry> loincEntryMap;
    private static Set<LoincId> loincIdSet;
    private static Map<LoincId, LoincPanel> loincPanelMap;
    private static Map<TermId, Term> hpoTermIdMap;
    private static Map<String, Term> hpoTermNameMap;
    private static Map<LoincId, LOINC2HpoAnnotationImpl> annotationMapl;


    public static ResourceCollection resourceCollection = new ResourceCollection();
    private static final String loincCoreTable = "/Users/zhangx/Downloads/LOINC_2/LoincTableCore.csv";
    private static final String loincPanels = "/Users/zhangx/git/loinc2hpoAnnotation/Data/LoincPanel/loincpanelAnnotation.tsv";
    private static final String hpo_obo = "/Users/zhangx/git/human-phenotype-ontology/src/ontology/hp.obo";
    private static final String hpo_owl = "/Users/zhangx/git/human-phenotype-ontology/src/ontology/hp.owl";
    private static final String loincAnnotationPath = "/Users/zhangx/git/loinc2hpoAnnotation/Data/TSVSingleFile/annotations.tsv";
//    private static final String loincPanelAnnotationPath = "/Users/zhangx/git/loinc2hpoAnnotation/Data/LoincPanel/loincPanelAnnotations.tsv";;

    @BeforeClass
    public static void setup() {
        resourceCollection.setLoincEntryPath(loincCoreTable);
        resourceCollection.setLoincPanelPath(loincPanels);
        resourceCollection.setHpoOboPath(hpo_obo);
        resourceCollection.setHpoOwlPath(hpo_owl);
        resourceCollection.setAnnotationMapPath(loincAnnotationPath);
    }

    @Test
    public void loincEntryMap() throws Exception {
        loincEntryMap = resourceCollection.loincEntryMap();
        assertNotNull(loincEntryMap);
        assertTrue(loincEntryMap.size() > 50000);
    }

    @Test
    public void loincIdSet() throws Exception {
        loincIdSet = resourceCollection.loincIdSet();
        assertNotNull(loincIdSet);
        assertTrue(loincIdSet.size() > 50000);
    }

    @Test
    public void hpoTermMap() throws Exception {
        hpoTermIdMap = resourceCollection.hpoTermMap();
        assertNotNull(hpoTermIdMap);
        assertTrue(hpoTermIdMap.size() > 5000);
    }

    @Test
    public void hpoTermMapFromName() throws Exception {
        hpoTermNameMap = resourceCollection.hpoTermMapFromName();
        assertNotNull(hpoTermNameMap);
        assertTrue(hpoTermNameMap.size() > 5000);
    }

    @Test
    public void annotationMap() throws Exception {
        annotationMapl = resourceCollection.annotationMap();
        assertNotNull(annotationMapl);
        assertTrue(annotationMapl.size() > 100);
    }

    @Test
    public void getLoincPanelMap() throws Exception {
        loincPanelMap = resourceCollection.getLoincPanelMap();
        assertNotNull(loincPanelMap);
        assertTrue(loincPanelMap.size() > 3000);
        assertTrue(loincPanelMap.get(new LoincId("35094-2")).isInterpretableInHPO());
        assertEquals(2, loincPanelMap.get(new LoincId("35094-2")).getChildrenRequiredForMapping().size());
    }

}