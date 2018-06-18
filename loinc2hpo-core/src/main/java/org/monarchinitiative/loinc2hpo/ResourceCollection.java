package org.monarchinitiative.loinc2hpo;

import com.google.common.collect.ImmutableMap;
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.exception.UnrecognizedLoincCodeException;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.LoincPanel;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * This class manages all resources that are required for this app. Whenever a new resource is required, just call a getter to retrieve it.
 */
public class ResourceCollection {

    private String loincEntryPath;
    private String hpoOboPath;
    private String hpoOwlPath;
    private String annotationMapPath;
    private String loincPanelPath;
    private String loincPanelAnnotationPath;
    private ImmutableMap<LoincId, LoincEntry> loincEntryMap;
    private Set<LoincId> loincIdSet;

    public void setLoincEntryFile(String path) {
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

    public void setLoincPanelAnnotationPath(String path){
        this.loincPanelAnnotationPath = path;
    }
    
    public ImmutableMap<LoincId, LoincEntry> loincEntryMap() {
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

    public Map<TermId, Term> hpoTermMap() {
        return null;
    }

    public Map<String, Term> hpoTermMapFromName() {
        return null;
    }

    public Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap() {
        return null;
    }

    public Map<LoincId, LoincPanel> getLoincPanelMap() throws MalformedLoincCodeException, IOException, UnrecognizedLoincCodeException {
        return LoincPanel.getPanels(this.loincPanelPath, this.loincEntryMap);
    }

}
