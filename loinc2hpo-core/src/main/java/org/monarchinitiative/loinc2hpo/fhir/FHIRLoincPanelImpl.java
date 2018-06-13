package org.monarchinitiative.loinc2hpo.fhir;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

import java.util.ArrayList;
import java.util.List;

public class FHIRLoincPanelImpl implements FHIRLoincPanel {

    private Patient subject;
    private LoincId panelId;
    private List<Observation> components;

    public FHIRLoincPanelImpl(LoincId panelid) {
        this.panelId = panelid;
    }

    public FHIRLoincPanelImpl(LoincId panelId, Patient patient) {
        this.panelId = panelId;
        this.subject = patient;
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
        return new ArrayList<>(this.components);
    }

    @Override
    public void addComponent(Observation observation) {
        // the observation should not be added twice
        if (this.components.isEmpty()) {
            this.components = new ArrayList<>();
            this.components.add(observation);
        }
        for (Observation component : components) {
            if (component.getId().equals(observation.getId())) {
                return;
            }
            this.components.add(observation);
        }
    }

    @Override
    public HpoTerm4TestOutcome getHPOforObservation() throws Exception {
        return null;//FhirObservationAnalyzer.getHPO4ObservationOutcome(loincIdSet, annotationMap);
    }
}
