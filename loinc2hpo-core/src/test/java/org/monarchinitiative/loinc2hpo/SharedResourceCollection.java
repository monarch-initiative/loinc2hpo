package org.monarchinitiative.loinc2hpo;

import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.LoincPanel;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;
import java.util.Set;

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
        resourceCollection.setHpoOboPath(hpo_obo);
        resourceCollection.setHpoOwlPath(hpo_owl);
        resourceCollection.setAnnotationMapPath(loincAnnotationPath);
    }
}
