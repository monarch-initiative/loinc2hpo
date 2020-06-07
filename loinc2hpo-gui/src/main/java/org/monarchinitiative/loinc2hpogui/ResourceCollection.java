package org.monarchinitiative.loinc2hpogui;

import com.google.common.collect.ImmutableMap;
import org.monarchinitiative.loinc2hpocore.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpogui.io.LoincOfInterest;
import org.monarchinitiative.loinc2hpocore.annotationmodel.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpocore.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class manages all resources that are required for this app. Whenever a new resource is required, just call a getter to retrieve it.
 */
public class ResourceCollection {

    private static Logger logger = LoggerFactory.getLogger(ResourceCollection.class);

    private String loincEntryPath;
    private String hpoOboPath;
    private String hpoOwlPath;
    private String annotationMapPath;
    private String loincCategoriesDirPath; //the path to the directory where we keep lists of loinc categories
    private Ontology hpo;
    private Map<String, Term> termnameTermMap;
    private ImmutableMap<LoincId, LoincEntry> loincEntryMap;
    private Map<LoincId, LOINC2HpoAnnotationImpl> loincAnnotationMap;
    private Map<String, Set<LoincId>> loincCategories;

    public void setLoincEntryPath(String path) {
        this.loincEntryPath = path;
    }

    public void setHpoOboPath(String path) {
        this.hpoOboPath = path;
    }

    public void setHpoOwlPath(String path) {
        this.hpoOwlPath = path;
    }

    public void setAnnotationMapPath(String path) {
        this.annotationMapPath = path;
    }

    public void setLoincCategoriesDirPath(String path) { this.loincCategoriesDirPath = path; }

    public ImmutableMap<LoincId, LoincEntry> loincEntryMap() {
        logger.trace("enter loincEntryMap()");
        logger.trace(String.format("loincEntryPath is null: %s", this.loincEntryPath == null));
        if (this.loincEntryPath == null) {
            return null;
        }
        if (loincEntryMap == null) {
            this.loincEntryMap = LoincEntry.getLoincEntryMap(this.loincEntryPath);
        }
        return this.loincEntryMap;
    }

    private void parseHPO() {
        this.hpo = OntologyLoader.loadOntology(new File(this.hpoOwlPath));
    }

    public Map<TermId, Term> hpoTermMap() {
        return this.hpo.getTermMap();
    }

    public Map<String, Term> hpoTermMapFromName() {
        if (this.termnameTermMap != null){
            return this.termnameTermMap;
        }

        this.termnameTermMap =
                this.hpo.getTermMap().values().stream().collect(Collectors.toMap(t -> t.getName(), t->t, (a, b) -> a));

        return this.termnameTermMap;
    }

    public Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap() throws Exception {
        if (this.loincAnnotationMap != null) {
            return this.loincAnnotationMap;
        }

        this.loincAnnotationMap =
                LOINC2HpoAnnotationImpl.from_csv(this.annotationMapPath);
        return this.loincAnnotationMap;
    }

    public Ontology getHPO() {
        if (this.hpo != null) {
            return this.hpo;
        }

        parseHPO();

        return this.hpo;
    }

    public Map<String, Set<LoincId>> getLoincCategories() {
        if (loincCategories != null) {
            return this.loincCategories;
        }

        this.loincCategories = new HashMap<>();
        File loinc_category_folder = new File(loincCategoriesDirPath);
        if (!loinc_category_folder.exists() || !loinc_category_folder.isDirectory()) {
            return this.loincCategories;
        }

        File[] files = loinc_category_folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.getName().endsWith(".txt")) {
                    continue;
                }
                if (file.getName().endsWith("DS_Store.txt")){
                    continue;
                }
                try {
                    LoincOfInterest loincCategory = new LoincOfInterest(file.getAbsolutePath());
                    Set<String> loincIdStrings = loincCategory.getLoincOfInterest();
                    String categoryName = file.getName().substring(0, file.getName().length() - 4);;

                    Set<LoincId> loincIds = loincIdStrings.stream().map(p -> {
                        try {
                            return new LoincId(p);
                        } catch (MalformedLoincCodeException e1) {
                            logger.error("malformed LOINC id: " + p);
                            logger.error("in:" + file.getAbsolutePath());
                        }
                        return null;
                    }).collect(Collectors.toSet());
                    this.loincCategories.putIfAbsent(categoryName, loincIds);
                    logger.info("loinc list: " + categoryName + "\t" + loincIds.size());
                } catch (FileNotFoundException e1) {
                    logger.error("file not found:" + file.getAbsolutePath());
                }
            }
        }
        return loincCategories;
    }
}
