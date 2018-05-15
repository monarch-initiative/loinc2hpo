package org.monarchinitiative.loinc2hpo.testresult;

import org.monarchinitiative.loinc2hpo.loinc.LoincId;

public class LabTestImpl implements LabTest{

    private LoincId test;
    private LabTestOutcome outcome;

    public LabTestImpl(LoincId test, LabTestOutcome outcome) {
        this.test = test;
        this.outcome = outcome;
    }

    @Override
    public LoincId getTest() {
        return this.test;
    }

    @Override
    public LabTestOutcome getOutcome() {
        return this.outcome;
    }
}
