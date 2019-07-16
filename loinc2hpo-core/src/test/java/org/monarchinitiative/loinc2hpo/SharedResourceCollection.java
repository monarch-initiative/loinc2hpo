package org.monarchinitiative.loinc2hpo;


public class SharedResourceCollection {

    public static ResourceCollection resourceCollection = new ResourceCollection();

    private static final String loincCoreTable = "/Users/zhangx/Downloads/LOINC_2/LoincTableCore.csv";
    private static final String loincPanels = "/Users/zhangx/git/loinc2hpoAnnotation/Data/LoincPanel/loincpanelAnnotation.tsv";
    private static final String hpo_obo = "/Users/zhangx/git/human-phenotype-ontology/src/ontology/hp.obo";
    private static final String hpo_owl = "/Users/zhangx/git/human-phenotype-ontology/src/ontology/hp.owl";
    private static final String loincAnnotationPath = "/Users/zhangx/git/loinc2hpoAnnotation/Data/TSVSingleFile/annotations.tsv";

    static {
        resourceCollection.setLoincEntryPath(loincCoreTable);
        resourceCollection.setLoincPanelPath(loincPanels);
        //resourceCollection.setHpoOboPath(hpo_obo);
        resourceCollection.setHpoOboPath(SharedResourceCollection.class.getResource("/obo/hp_test.obo").getPath());
        //resourceCollection.setHpoOwlPath(hpo_owl);
        resourceCollection.setHpoOwlPath(SharedResourceCollection.class.getResource("/hp.owl").getPath());
        resourceCollection.setAnnotationMapPath(loincAnnotationPath);
    }
}
