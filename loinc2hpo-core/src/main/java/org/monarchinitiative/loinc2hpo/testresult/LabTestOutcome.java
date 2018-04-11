package org.monarchinitiative.loinc2hpo.testresult;

import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Reference;
import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;

/**
 * This interface defines classes that model the outcome from a lab test after our analysis. It contains the information about the subject of the test, the identifier of the test, and the outcome (in HPO terms).
 */

public interface LabTestOutcome {

    /**
     * Returns the observation resource for the lab test. It is generally not a good idea to keep this information because it would take too much memory. Using the "identifier" and "subject" field would be sufficient to identify the patient and the resource.
     * @return
     */
    Observation getObservation();

    /**
     * Returns the identifier for the observation.
     * @return
     */
    List<Identifier> getTestIdentifier();

    /**
     * Returns the subject for the observation. According to FHIR, all observations should have a subject to make if useful. Some observations may lack a subject in the middle state, such as after a machine generate a report but the subject will not be identified later after using a machine-generated identifier - patient map.
     * @return
     */
    Reference getSubjectReference();

    /**
     * Returns the outcome for the patient in hpo terms.
     * @return
     */
    HpoTerm4TestOutcome getOutcome();

}
