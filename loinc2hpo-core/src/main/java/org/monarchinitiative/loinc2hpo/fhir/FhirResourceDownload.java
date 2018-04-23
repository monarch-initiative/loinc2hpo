package org.monarchinitiative.loinc2hpo.fhir;

import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;

import java.util.List;

public interface FhirResourceDownload {

    /**
     * Download a patient from a test server from the subject field
     * @param subject
     * @return
     */
    Patient getPatient(Reference subject);

    /**
     * Download a patient with a specified identifier
     * @param identifier
     * @return
     */
    Patient getPatient(Identifier identifier);

    /**
     * Download a patient with a resource id
     * @param resourceId
     * @return
     */
    Patient getPatient(String resourceId);

    /**
     * Download a list of observations related to a patient
     * @param patient
     * @return
     */
    List<Observation> getObservation(Patient patient);

}
