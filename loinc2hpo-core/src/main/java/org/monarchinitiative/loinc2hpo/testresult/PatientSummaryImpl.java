package org.monarchinitiative.loinc2hpo.testresult;

import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.phenol.formats.hpo.HpoTerm;
import sun.tools.java.Identifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PatientSummaryImpl implements PatientSummary{

    private Patient patient;
    private List<LabTest> labTests;
    private List<AbnormalityComponent> abnormalityComponents;
    private static Map<HpoTerm, AbnormalitySynonet> synonetMap;

    //do not allow instantiation with new
    private PatientSummaryImpl(){

    }

    static PatientSummaryImpl getInstance(){
        if (synonetMap == null) {
            throw new RuntimeException("synonetMap is not set");
        }
        return new PatientSummaryImpl();
    }

    @Override
    public Patient patient() {
        return this.patient;
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

    /**
     * How to interpret patient abnormalities?
     * check all patient abnormalities, for abnormalities that he is showing:
     * if current test diagnosed that he is showing the same term, no need to do anything except check expiration date;
     * if current test diagnosed that he is showing mutually exclusive terms, terminate the current term and add a new abnormality
     * if current test diagnosed that he is showing novel abnormalities, just add a new abnormality
     * @param test
     */
    private void interpret_new_test(LabTest test) {
        HpoTerm newterm = test.outcome().getHpoTerm();
        for (AbnormalityComponent component : abnormalityComponents) {
            if (component.abnormality().equals(newterm)) {
                //if the patient is already diagnosed with the phenotype, we don't need to do more
                //but if the current test has an effectiveEnd time, we should update it
                if (test.effectiveEnd() != null) {
                    component.setEffectiveEnd(test.effectiveEnd());
                }
            } else if (synonetMap.get(component.abnormality()).has(newterm)){
                component.setEffectiveEnd(test.effectiveEnd());
                component.

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
