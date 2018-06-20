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

//    protected FHIRLoincPanelFactory factory = new FHIRLoincPanelFactoryLazy();
    protected Patient subject;
    protected LoincId panelId;
    protected Map<LoincId, Observation> components;


//    public FHIRLoincPanelImpl(FHIRLoincPanelFactory factory) {
//        this.factory = factory;
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

//    @Override
//    public FHIRLoincPanel instance(LoincId loincId) {
//        return this.factory.createFhirLoincPanel(loincId);
//    }
//
//    @Override
//    public FHIRLoincPanel instance(LoincId loincId, Patient subject) {
//        return this.factory.createFhirLoincPanel(loincId, subject);
//    }

    @Override
    public void setSubject(Patient subject) {
        this.subject = subject;
    }

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
    public void addComponent(LoincId loincId, Observation observation) {
        this.components.put(loincId, observation);
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
