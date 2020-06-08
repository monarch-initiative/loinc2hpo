package org.monarchinitiative.loinc2hpogui.model;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.monarchinitiative.loinc2hpocore.Constants;
import org.monarchinitiative.loinc2hpogui.ResourceCollection;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Loinc2HpoAnnotationModel;
import org.monarchinitiative.loinc2hpocore.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The class manages all resources required for this app.
 */
@Singleton
public class AppResources {

    private final static Logger logger = LoggerFactory.getLogger(AppResources.class);
    private Ontology hpo;
    private Map<TermId, Term> termidTermMap;
    private Map<String, Term> termnameTermMap;
    private Map<LoincId, LoincEntry> loincEntryMap;
    private Map<String, LoincEntry> loincEntryMapFromName;
    private Map<LoincId, Loinc2HpoAnnotationModel> loincAnnotationMap;
    private Map<String, Set<LoincId>> userCreatedLoincLists;
    private Map<String, String> userCreatedLoincListsColor;
    private ResourceCollection resourceCollection;
    private Settings settings;

    @Inject
    public AppResources(ResourceCollection resourceCollection, Settings settings) {
        this.resourceCollection = resourceCollection;
        this.settings = settings;
    }

    public void init() {
        this.resourceCollection.setHpoOboPath(settings.getHpoOboPath());
        this.resourceCollection.setHpoOwlPath(settings.getHpoOwlPath());
        this.resourceCollection.setLoincEntryPath(settings.getLoincCoreTablePath());
        String annotationFilePath = settings.getAnnotationFolder() + File.separator
                + Constants.DATAFOLDER + File.separator //"Data" folder
                + Constants.TSVSingleFileFolder + File.separator + //TSVSingleFile
                Constants.TSVSingleFileName; //file name
        this.resourceCollection.setAnnotationMapPath(annotationFilePath);
    }

    public Settings getSettings() {
        return settings;
    }

    public Ontology getHpo() {
        if (this.hpo != null) {
            return this.hpo;
        }
        this.hpo = resourceCollection.getHPO();
        return this.hpo;
    }

    public Map<TermId, Term> getTermidTermMap() {
        if (this.termidTermMap != null) {
            return this.termidTermMap;
        }
        this.termidTermMap = resourceCollection.hpoTermMap();
        return this.termidTermMap;
    }

    public Map<String, Term> getTermnameTermMap() {
        if (this.termnameTermMap != null) {
            return this.termnameTermMap;
        }
        this.termnameTermMap = resourceCollection.hpoTermMapFromName();
        return this.termnameTermMap;
    }

    public Map<LoincId, LoincEntry> getLoincEntryMap() {
        if (this.loincEntryMap != null) {
            return this.loincEntryMap;
        }
        this.loincEntryMap = resourceCollection.loincEntryMap();
        return loincEntryMap;
    }

    public Map<String, LoincEntry> getLoincEntryMapFromName() {
        if (this.loincEntryMapFromName != null) {
            return this.loincEntryMapFromName;
        }

        this.loincEntryMapFromName =
                resourceCollection.loincEntryMap().values().stream()
                        .collect(Collectors.toMap(LoincEntry::getLongName, t -> t, (a, b) -> a));

        return loincEntryMapFromName;
    }

    public Map<LoincId, Loinc2HpoAnnotationModel> getLoincAnnotationMap(){
        if (this.loincAnnotationMap != null) {
            return this.loincAnnotationMap;
        }

        try {
            this.loincAnnotationMap = resourceCollection.annotationMap();
        } catch (Exception e) {
            throw new RuntimeException("Error parsing the annotation map");
        }

        return this.loincAnnotationMap;
    }

    public Map<String, String> getUserCreatedLoincListsColor() {
        if (this.userCreatedLoincListsColor != null) {
            return this.userCreatedLoincListsColor;
        }

        this.userCreatedLoincListsColor = settings.getUserCreatedLoincListsColor();

        return userCreatedLoincListsColor;
    }

    public Map<String, Set<LoincId>> getUserCreatedLoincLists() {
        if (this.userCreatedLoincLists != null) {
            return this.userCreatedLoincLists;
        }

        String loincCategoriesDirPath = settings.getAnnotationFolder() + File.separator + Constants.DATAFOLDER + File.separator + Constants.LOINCCategory;
        resourceCollection.setLoincCategoriesDirPath(loincCategoriesDirPath);
        this.userCreatedLoincLists = resourceCollection.getLoincCategories();
        return this.userCreatedLoincLists;
    }

}
