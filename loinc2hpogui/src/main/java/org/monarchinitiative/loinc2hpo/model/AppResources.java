package org.monarchinitiative.loinc2hpo.model;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.monarchinitiative.loinc2hpo.Constants;
import org.monarchinitiative.loinc2hpo.ResourceCollection;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.LoincPanel;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
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
    private Map<LoincId, LoincPanel> loincPanelMap;
    private Map<LoincId,LOINC2HpoAnnotationImpl> loincAnnotationMap;
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

    public ResourceCollection getResourceCollection() {
        return resourceCollection;
    }

    public Settings getSettings() {
        return settings;
    }

    public Ontology getHpo() {

        if (this.hpo != null) {
            return this.hpo;
        }

        try {
            this.hpo = resourceCollection.getHPO();
        } catch (PhenolException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return this.hpo;
    }

    public Map<TermId, Term> getTermidTermMap() {
        if (this.termidTermMap != null) {
            return this.termidTermMap;
        }

        try {
            this.termidTermMap = resourceCollection.hpoTermMap();
        } catch (PhenolException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return this.termidTermMap;
    }

    public Map<String, Term> getTermnameTermMap() {
        if (this.termnameTermMap != null) {
            return this.termnameTermMap;
        }

        try {
            this.termnameTermMap = resourceCollection.hpoTermMapFromName();
        } catch (PhenolException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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

    public Map<LoincId, LoincPanel> getLoincPanelMap() {
        throw new UnsupportedOperationException();
    }

    public Map<LoincId, LOINC2HpoAnnotationImpl> getLoincAnnotationMap() throws Exception {
        if (this.loincPanelMap != null) {
            return this.loincAnnotationMap;
        }


        this.loincAnnotationMap = resourceCollection.annotationMap();

        return this.loincAnnotationMap;
    }

    public Map<String, String> getUserCreatedLoincListsColor() {
        if (this.userCreatedLoincListsColor != null) {
            return this.userCreatedLoincListsColor;
        }

        this.userCreatedLoincListsColor = settings.getUserCreatedLoincListsColor();

        return userCreatedLoincListsColor;
    }
}
