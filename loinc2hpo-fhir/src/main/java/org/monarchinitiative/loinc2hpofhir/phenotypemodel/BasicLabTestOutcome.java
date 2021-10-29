package org.monarchinitiative.loinc2hpofhir.phenotypemodel;

import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Reference;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Hpo2Outcome;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the final outcome for a lab test. It basically wraps the Observation and the outcome. For memory considerations, the best practice is to save the subject and observation identifier only instead of the entire observation.
 */
public class BasicLabTestOutcome implements LabTestOutcome {

    private Observation observation;
    private Reference subject;
    private List<Identifier> identifiers;
    private final Hpo2Outcome hpoId;

    private final String comment;

    public BasicLabTestOutcome(Hpo2Outcome id, @Nullable String text) {
        this.hpoId = id;
        this.comment = text;
    }

    public BasicLabTestOutcome(Hpo2Outcome outcome, @Nullable String comment, @Nullable Reference subject, @Nullable List<Identifier> identifiers) {
        this.subject = subject;
        this.identifiers = new ArrayList<>();
        if (identifiers != null) {
            this.identifiers.addAll(identifiers);
        }
        this.hpoId = outcome;
        this.comment = comment;
    }

    public BasicLabTestOutcome(Hpo2Outcome outcome, @Nullable String comment, @Nullable Observation observation) {

        this.observation = observation;
        this.hpoId = outcome;
        this.comment = comment;

    }

    @Override
    public Observation getObservation() {

        throw new UnsupportedOperationException();
        
    }

    @Override
    public List<Identifier> getTestIdentifier() {

        return this.identifiers;
    }

    @Override
    public Reference getSubjectReference() {

        return this.subject;

    }

    @Override
    public Hpo2Outcome getOutcome() {

        return this.hpoId;

    }

    @Override
    public String toString() {

        if (hpoId==null) {
            return "error => hpoId is null in testResult";
        }
        if (hpoId.getId()==null) {
            return "error => hpoId.getId() is null in testResult";
        }

        return String.format("BasicLabTestOutcome: %s [%s; %s]", hpoId.getId().getValue(),"?", "?");
    }





}
