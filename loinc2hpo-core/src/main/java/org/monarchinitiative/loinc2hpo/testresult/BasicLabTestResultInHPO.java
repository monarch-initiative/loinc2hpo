package org.monarchinitiative.loinc2hpo.testresult;


import ca.uhn.fhir.model.base.composite.BaseIdentifierDt;
import ca.uhn.fhir.model.base.composite.BaseResourceReferenceDt;
import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.phenol.ontology.data.TermId;

import javax.annotation.Nullable;

/**
 * This class represents the final outcome for a lab test. It basically wraps the Observation and the outcome. For memory considerations, the best practice is to save the subject and observation identifier only instead of the entire observation.
 */
public class BasicLabTestResultInHPO implements LabTestResultInHPO {

    private Observation observation;
    private BaseResourceReferenceDt subject;
    private BaseIdentifierDt identifier;
    private HpoTerm4TestOutcome hpoId;

    private String comment;

    public BasicLabTestResultInHPO(HpoTerm4TestOutcome id, @Nullable String text) {
        this.hpoId = id;
        this.comment = text;
    }

    public BasicLabTestResultInHPO(HpoTerm4TestOutcome outcome, @Nullable String comment, @Nullable BaseResourceReferenceDt subject, @Nullable BaseIdentifierDt identifier) {
        this.subject = subject;
        this.identifier = identifier;
        this.hpoId = outcome;
        this.comment = comment;
    }

    public BasicLabTestResultInHPO(HpoTerm4TestOutcome outcome, @Nullable String comment, @Nullable Observation observation) {

        this.observation = observation;
        this.hpoId = outcome;
        this.comment = comment;

    }

    @Override
    public Observation getObservation() {

        throw new UnsupportedOperationException();
        
    }

    @Override
    public BaseIdentifierDt getTestIdentifier() {

        throw new UnsupportedOperationException();
    }

    @Override
    public BaseResourceReferenceDt getSubjectReference() {

        throw new UnsupportedOperationException();

    }

    @Override
    public TermId getTermId() { return hpoId.getId(); }

    @Override
    public boolean isNegated() { return hpoId.isNegated(); }

    @Override
    public String toString() {

        if (hpoId==null) {
            return "error => hpoId is null in testResult";
        }
        if (hpoId.getId()==null) {
            return "error => hpoId.getId() is null in testResult";
        }
        return String.format("BasicLabTestResultInHPO: %s [%s; %s]", hpoId.getId().getIdWithPrefix(),"?", "?");
    }





}
