package org.monarchinitiative.loinc2hpo.fhir;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.LoincPanel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FHIRLoincPanelsImpl implements FHIRLoincPanels {

    private Patient subject;
    private String patientId;
    private HashMap<LoincId, List<LoincPanel>> loincPanels;

    public FHIRLoincPanelsImpl(Patient subject) {
        this.subject = subject;
        this.loincPanels = new HashMap<>();
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
    public Set<LoincId> panelIds() {
        return new HashSet<>(this.loincPanels.keySet());
    }

    @Override
    public void addComponent(Observation observation) {

    }

    @Override
    public void addComponents(List<Observation> observations) {

    }

    @Override
    public void addComponentsOfSamePanel(List<Observation> observationsOfPanel) {

    }

    @Override
    public void groupToPanel(Observation observation) {

    }

    @Override
    public void interpret() {

    }
}
