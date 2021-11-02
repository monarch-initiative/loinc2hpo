package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Hpo2Outcome;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.util.*;

/**
 * Class to represent a FHIR LOINC panel
 */
public abstract class FHIRLoincPanelImpl implements FHIRLoincPanel {

    protected Patient subject;
    protected LoincId panelId;
    protected Map<LoincId, Observation> components;

    public FHIRLoincPanelImpl(LoincId panelid) {
        this.panelId = panelid;
        //this.resourceCollection = resourceCollection;
    }

    public FHIRLoincPanelImpl(LoincId panelId, Patient patient) {
        this.panelId = panelId;
        this.subject = patient;
        //this.resourceCollection = resourceCollection;
    }

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
    public abstract Hpo2Outcome getHPOforObservation() throws Exception;
}
