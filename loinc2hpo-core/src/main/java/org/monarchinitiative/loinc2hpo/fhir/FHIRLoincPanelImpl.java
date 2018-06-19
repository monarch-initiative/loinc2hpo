package org.monarchinitiative.loinc2hpo.fhir;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpo.ResourceCollection;
import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

import java.util.*;
import java.util.stream.Collectors;

public abstract class FHIRLoincPanelImpl implements FHIRLoincPanel {

    protected Patient subject;
    protected LoincId panelId;
    protected Map<LoincId, Observation> components;
    //protected ResourceCollection resourceCollection;

//    protected static Set<LoincId> loincIdSet;
//    protected static Map<LoincId, LoincEntry> loincEntryMap;
//    protected static Map<LoincId, LOINC2HpoAnnotationImpl> loincAnnotationMap;

//    public static void setLoincIds(Set<LoincId> loincIds) {
//        loincIdSet = loincIds;
//    }
//
//    public static void setLoincEntryMap(Map<LoincId, LoincEntry> loincEntryMapE) {
//        loincEntryMap = loincEntryMapE;
//    }
//
//    public static void setLoincAnnotationMap(Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap){
//        loincAnnotationMap = annotationMap;
//    }



    public FHIRLoincPanelImpl(LoincId panelid) {
        this.panelId = panelid;
        //this.resourceCollection = resourceCollection;
    }

    public FHIRLoincPanelImpl(LoincId panelId, Patient patient) {
        this.panelId = panelId;
        this.subject = patient;
        //this.resourceCollection = resourceCollection;
    }

//    static void initResources(Set<LoincId> loincIdSetX, Map<LoincId, LoincEntry> loincEntryMapX, Map<LoincId, LOINC2HpoAnnotationImpl> annotationMapX){
//        loincIdSet = loincIdSetX;
//        loincEntryMap = loincEntryMapX;
//        loincAnnotationMap = annotationMapX;
//    }

    @Override
    public Patient getSubject() {
        return this.subject;
    }

    @Override
    public String getPatientId() {
        if (this.subject == null) {
            return null;
        }

        return this.subject.getId();
    }

    @Override
    public LoincId panelId() {
        return this.panelId;
    }

    @Override
    public List<Observation> panelComponents() {
        return new ArrayList<>(this.components.values());
    }

    @Override
    public void addComponent(Observation observation) {
//        // the observation should not be added twice
//        if (this.components.isEmpty()) {
//            this.components = new ArrayList<>();
//            this.components.add(observation);
//        }
//        for (Observation component : components) {
//            if (component.getId().equals(observation.getId())) {
//                return;
//            }
//            this.components.add(observation);
//        }
        throw new UnsupportedOperationException();
    }

    @Override
    public void addComponents(Map<LoincId, Observation> observationList) {
        if (this.components == null) {
            this.components = new HashMap<>();
        }
        this.components.putAll(observationList);
    }

    @Override
    public abstract HpoTerm4TestOutcome getHPOforObservation() throws Exception;
}
