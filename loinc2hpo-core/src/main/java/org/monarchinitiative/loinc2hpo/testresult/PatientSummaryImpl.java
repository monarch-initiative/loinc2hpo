package org.monarchinitiative.loinc2hpo.testresult;

import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.phenol.formats.hpo.HpoTerm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PatientSummaryImpl implements PatientSummary{

    private static Map<HpoTerm, PhenoSet> phenoSetMap;

    private Patient patient;
    private List<LabTest> labTests;
    private List<PhenoSetTimeLine> phenoSetTimeLines;

    //do not allow instantiation with new
    private PatientSummaryImpl(){

    }

    static PatientSummaryImpl getInstance(){
        if (phenoSetMap == null) {
            throw new RuntimeException("phenoSetMap is not set");
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
        interpret_new_test(test);
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
        if (test.outcome() == null) { //if test is not transformed into HPO, return
            return;
        }

        HpoTerm newterm = test.outcome().getHpoTerm();
        for (PhenoSetTimeLine timeLine : phenoSetTimeLines) {
            if (timeLine.phenoset().has(newterm)) {
                PhenoSetComponent newComponent = new PhenoSetComponentImpl.Builder()
                        .start(test.effectiveStart())
                        //default end time
                        .hpoTerm(test.outcome().getHpoTerm())
                        .isNegated(test.outcome().isNegated()).build();
                timeLine.insert(newComponent);
            }
        }
    }

    @Override
    public List<LabTest> tests() {
        return new ArrayList<>(this.labTests);
    }

    @Override
    public List<PhenoSetComponent> phenoDuring(Date start, Date end) {
        List<PhenoSetComponent> patientPhenotypes = new ArrayList<>();
        phenoSetTimeLines.stream().forEach(timeline -> {
            timeline.getTimeLine().stream()
                    .filter(component -> component.isEffective(start))
                    .filter(component -> component.isEffective(end))
                    .forEach(patientPhenotypes::add);
        });
        return patientPhenotypes;
    }

    @Override
    public List<PhenoSetComponent> phenoSinceBorn() {

        List<PhenoSetComponent> patientPhenotypes = new ArrayList<>();
        phenoSetTimeLines.stream().forEach(timeLine -> {
            patientPhenotypes.addAll(timeLine.getTimeLine());
        });
        return patientPhenotypes;
    }
}
