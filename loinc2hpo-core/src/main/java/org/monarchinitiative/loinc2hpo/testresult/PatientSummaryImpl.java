package org.monarchinitiative.loinc2hpo.testresult;

import org.monarchinitiative.phenol.formats.hpo.HpoTerm;
import sun.tools.java.Identifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PatientSummaryImpl implements PatientSummary{

    private List<Identifier> identifiers;
    private String id;
    private List<LabTest> labTests;
    private List<AbnormalityComponent> abnormalityComponents;
    private Map<HpoTerm, AbnormalitySynonet> synonetMap;

    public PatientSummaryImpl(Map<HpoTerm, AbnormalitySynonet> synonetMap){
        this.synonetMap = synonetMap;
    }

    @Override
    public List<Identifier> patientIdentifier() {
        return new ArrayList<>(identifiers);
    }

    @Override
    public String patientId() {
        return this.id;
    }

    @Override
    public void addTest(LabTest test) {
        this.labTests.add(test);
        //determine abnormality
    }

    @Override
    public void addTest(List<LabTest> tests) {
        tests.forEach(this::addTest);
    }

    private void interpret_new_test(LabTest test) {
        HpoTerm newterm = test.getOutcome().getOutcome().getHpoTerm();
        for (AbnormalityComponent component : abnormalityComponents) {
            if (component.abnormality().equals(newterm)) {
                //if the patient is already diagnosed with the phenotype, we don't need to do more
            } else {

            }
            if (this.synonetMap.get(component.abnormality()).has()) {

            }
        }
    }

    @Override
    public void addPhenoManifest(AbnormalityComponent abnormalityComponent) {
        this.abnormalityComponents.add(abnormalityComponent);

    }

    @Override
    public List<LabTest> tests() {
        return new ArrayList<>(this.labTests);
    }

    @Override
    public List<AbnormalityComponent> phenoDuring(Date start, Date end) {
        return this.abnormalityComponents
                .stream()
                .filter(c -> c.effectiveStart().before(start) && c.effectiveEnd().after(end))
                .collect(Collectors.toList());
    }

    @Override
    public List<AbnormalityComponent> phenoSinceBorn() {
        return new ArrayList<>(this.abnormalityComponents);
    }
}
