package org.monarchinitiative.loinc2hpo;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.exception.UnrecognizedLoincCodeException;
import org.monarchinitiative.loinc2hpo.io.HpoOntologyParser;
import org.monarchinitiative.loinc2hpo.io.LoincAnnotationSerializationFactory;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.LoincPanel;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.obo.hpo.HpOboParser;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
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
    private String loincPanelPath;
    //private String loincPanelAnnotationPath;
    private HpoOntologyParser hpoOntologyParser;
    private HpoOntology hpo;
    private Map<TermId, Term> termidTermMap;
    private Map<String, Term> termnameTermMap;
    private ImmutableMap<LoincId, LoincEntry> loincEntryMap;
    private Set<LoincId> loincIdSet;
    private Map<LoincId, LoincPanel> loincPanelMap;
    private Map<LoincId, LOINC2HpoAnnotationImpl> loincAnnotationMap;

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

    public void setLoincPanelPath(String path) {
        this.loincPanelPath = path;
    }

//    public void setLoincPanelAnnotationPath(String path){
//        this.loincPanelAnnotationPath = path;
//    }

    public ImmutableMap<LoincId, LoincEntry> loincEntryMap() {
        logger.trace("enter loincEntryMap()");
        logger.trace(String.format("loincEntryPath is null: %s", this.loincEntryPath == null));
        if (this.loincEntryPath == null) {
            return null;
        }
        if (loincEntryMap == null) {
            this.loincEntryMap = LoincEntry.getLoincEntryList(this.loincEntryPath);
        }
        return this.loincEntryMap;
    }

    public Set<LoincId> loincIdSet() {
        if (this.loincEntryPath == null) {
            return null;
        }
        if (loincIdSet == null) {
            this.loincIdSet = loincEntryMap().keySet();
        }
        return this.loincIdSet;
    }

    private void parseHPO() throws PhenolException, FileNotFoundException {
        if (this.hpoOboPath != null) {
            hpoOntologyParser = new HpoOntologyParser(this.hpoOboPath);
        } else if (this.hpoOwlPath != null) {
            hpoOntologyParser = new HpoOntologyParser(this.hpoOwlPath);
        }
        hpoOntologyParser.parseOntology();
        this.hpo = hpoOntologyParser.getOntology();
    }

    public Map<TermId, Term> hpoTermMap() throws PhenolException, FileNotFoundException {
        if (this.termidTermMap != null) {
            return this.termidTermMap;
        }

        if (this.hpoOntologyParser == null) {
            parseHPO();
        }

        this.termidTermMap = hpoOntologyParser.getTermMap2();

        return this.termidTermMap;
    }

    public Map<String, Term> hpoTermMapFromName() throws PhenolException, FileNotFoundException {
        if (this.termnameTermMap != null){
            return this.termnameTermMap;
        }

        if (this.hpoOntologyParser == null) {
            parseHPO();
        }

        this.termnameTermMap = hpoOntologyParser.getTermMap();

        return this.termnameTermMap;
    }

    public Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap() throws Exception {
        if (this.annotationMapPath == null) {
            return null;
        }

        this.loincAnnotationMap = LoincAnnotationSerializationFactory.parseFromFile(this.annotationMapPath, hpoTermMap(), LoincAnnotationSerializationFactory.SerializationFormat.TSVSingleFile);
        return this.loincAnnotationMap;
    }

    public Map<LoincId, LoincPanel> getLoincPanelMap() throws MalformedLoincCodeException, IOException, UnrecognizedLoincCodeException {
        if (this.loincPanelMap != null) {
            return this.loincPanelMap;
        }
        this.loincPanelMap = LoincPanel.deserializeLoincPanel(this.loincPanelPath);
        return this.loincPanelMap;
    }

    private void addLoincPanelAnnotation(String loincPanelAnnotationPath, Map<LoincId, LoincPanel> loincPanelMap){

    }

    public HpoOntology getHPO() throws PhenolException, FileNotFoundException {
        if (this.hpo != null) {
            return this.hpo;
        }

        if (this.hpoOntologyParser == null) {
            parseHPO();
        }

        return this.hpo;
    }

}
