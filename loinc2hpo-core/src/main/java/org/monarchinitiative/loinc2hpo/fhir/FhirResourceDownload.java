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
     * @return a list of patient. Expecting one patient.
     */
    List<Patient> getPatient(Reference subject);

    /**
     * Download a patient with a specified identifier
     * @param identifier
     * @return a list of patients. Expecting one patient. Server logic error if otherwise.
     */
    List<Patient> getPatient(Identifier identifier);

    /**
     * Download a patient with a resource id
     * @param resourceId
     * @return a list of patients. Expecting one patient. Server logic error if otherwise.
     */
    List<Patient> getPatient(String resourceId);

    /**
     *
     */
    List<Patient> getPatient(String firstName, String lastName);

    List<Patient> getPatient(String firstName, String lastName, String phone, String zipcode);

    /**
     * Download a list of observations related to a patient
     * @param patient
     * @return a list of observations for a patient. List size range [0, MAX INFINITY).
     */
    List<Observation> getObservation(Patient patient);

}
