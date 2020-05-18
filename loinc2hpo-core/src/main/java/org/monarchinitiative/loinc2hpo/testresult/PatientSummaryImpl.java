package org.monarchinitiative.loinc2hpo.testresult;

import org.hl7.fhir.dstu3.model.Patient;
import org.jgrapht.alg.util.UnionFind;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PatientSummaryImpl implements PatientSummary{

    //private static Map<HpoTerm, PhenoSet> phenoSetMap;

    private Patient patient;
    private List<LabTest> labTests;
    private List<PhenoSetTimeLine> phenoSetTimeLines;
    private UnionFind<TermId> hpoTermUnionFind;

    public PatientSummaryImpl(Patient patient, UnionFind<TermId> hpoTermUnionFind){
        this.patient = patient;
        this.labTests = new ArrayList<>();
        this.phenoSetTimeLines = new ArrayList<>();
        this.hpoTermUnionFind = hpoTermUnionFind;
    }

    /**
    static PatientSummaryImpl getInstance(Patient patient){
        if (phenoSetMap == null) {
            throw new RuntimeException("phenoSetMap is not set");
        }
        return new PatientSummaryImpl(patient);
    }
     **/

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

        TermId newterm = test.outcome().getId();
        PhenotypeComponent newComponent = new PhenotypeComponentImpl.Builder()
                .start(test.effectiveStart())
                //default end time
                .hpoTerm(test.outcome().getId())
                .isNegated(test.outcome().isNegated()).build();

        boolean timeLineFound = false;
        PhenoSetTimeLine target = null;
        for (PhenoSetTimeLine timeLine : phenoSetTimeLines) {
            if (timeLine.phenoset().sameSet(newterm)) {
  //System.out.println(timeLine.getTimeLine().get(0).abnormality().getName());
  //System.out.println("Find a timeline");
                timeLineFound = true;
                target = timeLine;
                break;
            }
        }

        if (timeLineFound) {
            target.insert(newComponent);
            target.phenoset().add(newterm);
            //System.out.println("Added to existing timeline. TimeLines: " + this.phenoSetTimeLines.size());

        } else {
            PhenoSet newPhenoset = new PhenoSetImpl(this.hpoTermUnionFind);
            PhenoSetTimeLine newTimeLine = new PhenoSetTimeLineImpl(newPhenoset);
            newTimeLine.insert(newComponent);
            newPhenoset.add(newterm);
            this.phenoSetTimeLines.add(newTimeLine);
            //System.out.println("New timeline. TimeLines: " + this.phenoSetTimeLines.size());
        }
    }

    @Override
    public List<LabTest> tests() {
        return new ArrayList<>(this.labTests);
    }

    @Override
    public List<PhenoSetTimeLine> timeLines() {
        return new ArrayList<>(this.phenoSetTimeLines);
    }

    @Override
    public List<PhenotypeComponent> phenoAt(Date timepoint) {
        return phenoSetTimeLines.stream()
                .map(timeLine -> timeLine.current(timepoint))
                .collect(Collectors.toList());
    }

    @Override
    public List<PhenotypeComponent> phenoPersistedDuring(Date start, Date end) {
        return phenoSetTimeLines.stream()
                .map(timeline -> timeline.persistDuring(start, end))
                .collect(Collectors.toList());
    }

    @Override
    public List<PhenotypeComponent> phenoOccurredDuring(Date start, Date end) {
        List<PhenotypeComponent> patientPhenotypes = new ArrayList<>();
        phenoSetTimeLines.stream()
                .map(timeline -> timeline.occurredDuring(start, end))
                .forEach(patientPhenotypes::addAll);
        return patientPhenotypes;
    }

    @Override
    public List<PhenotypeComponent> phenoSinceBorn() {
        List<PhenotypeComponent> patientPhenotypes = new ArrayList<>();
        phenoSetTimeLines.forEach(timeLine -> patientPhenotypes.addAll(timeLine.getTimeLine()));
        return patientPhenotypes;
    }
}
