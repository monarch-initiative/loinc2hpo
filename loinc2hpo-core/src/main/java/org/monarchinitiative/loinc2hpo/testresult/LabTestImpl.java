package org.monarchinitiative.loinc2hpo.testresult;

import org.apache.maven.model.Build;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

import java.util.Date;
import java.util.List;

/**
 * How to model a lab test?
 * A lab test should contain
 */

public class LabTestImpl implements LabTest{

    private Patient patient;
    private Date effectiveStart;
    private Date effectiveEnd;
    private LoincId loincId;
    private String resourceId;
    private HpoTerm4TestOutcome outcome;


    private LabTestImpl(Patient patient, Date effectiveStart, Date effectiveEnd, LoincId loincId, String resourceId, HpoTerm4TestOutcome outcome) {
        this.patient = patient;
        this.effectiveStart = effectiveStart;
        this.effectiveEnd = effectiveEnd;
        this.loincId = loincId;
        this.resourceId = resourceId;
        this.outcome = outcome;
    }

    @Override
    public Patient subject() {
        return this.patient;
    }

    @Override
    public Date effectiveStart() {
        return this.effectiveStart;
    }

    @Override
    public Date effectiveEnd() {
        return this.effectiveEnd;
    }


    @Override
    public LoincId loinc() {
        return this.loincId;
    }

    @Override
    public String resourceId() {
        return this.resourceId;
    }

    @Override
    public HpoTerm4TestOutcome outcome() {
        return this.outcome;
    }

    static class Builder {
        private Patient patient;
        private Date effectiveStart;
        private Date effectiveEnd;
        private LoincId loincId;
        private String resourceId;
        private HpoTerm4TestOutcome outcome;

        protected Builder patient(Patient patient) {
            this.patient = patient;
            return this;
        }

        protected Builder effectiveStart(Date effectiveStart) {
            this.effectiveStart = effectiveStart;
            return this;
        }

        protected Builder effectiveEnd(Date effectiveEnd) {
            this.effectiveEnd = effectiveEnd;
            return this;
        }

        protected Builder loincId(LoincId loincId) {
            this.loincId = loincId;
            return this;
        }

        protected Builder resourceId(String id) {
            this.resourceId = id;
            return this;
        }

        protected Builder outcome(HpoTerm4TestOutcome outcome) {
            this.outcome = outcome;
            return this;
        }

        protected LabTestImpl build() {
            return new LabTestImpl(patient, effectiveStart, effectiveEnd, loincId, resourceId, outcome);
        }

    }

}
