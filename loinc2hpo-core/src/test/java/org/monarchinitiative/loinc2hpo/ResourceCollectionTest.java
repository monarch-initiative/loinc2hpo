package org.monarchinitiative.loinc2hpo;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResourceCollectionTest {

    private static ResourceCollection resourceCollection = new ResourceCollection();
    private static final String loincCoreTable = "/Users/zhangx/Downloads/LOINC_2/LoincTableCore.csv";
    private static final String loincPanels = "/Users/zhangx/Downloads/LOINC_2/Accessory/PanelsAndForms/LOINC_263_PanelsAndForms_Panels.csv";
    private static final String hpo_obo = "/Users/zhangx/git/human-phenotype-ontology/src/ontology/hp.obo";
    private static final String hpo_owl = "/Users/zhangx/git/human-phenotype-ontology/src/ontology/hp.owl";
    private static final String loincAnnotationPath = "/Users/zhangx/git/loinc2hpoAnnotation/Data/TSVSingleFile/annotations.tsv";
    private static final String loincPanelAnnotationPath = "/Users/zhangx/git/loinc2hpoAnnotation/Data/LoincPanel/loincPanelAnnotations.tsv";;

    public static void setup() {

    }


    @Test
    public void setLoincEntryFile() throws Exception {
    }

    @Test
    public void setHpoOboPath() throws Exception {
    }

    @Test
    public void setHpoOwlPath() throws Exception {
    }

    @Test
    public void setAnnotationMapPath() throws Exception {
    }

    @Test
    public void setLoincPanelPath() throws Exception {
    }

    @Test
    public void setLoincPanelAnnotationPath() throws Exception {
    }

    @Test
    public void loincEntryMap() throws Exception {
    }

    @Test
    public void loincIdSet() throws Exception {
    }

    @Test
    public void hpoTermMap() throws Exception {
    }

    @Test
    public void hpoTermMapFromName() throws Exception {
    }

    @Test
    public void annotationMap() throws Exception {
    }

    @Test
    public void getLoincPanelMap() throws Exception {
    }

}