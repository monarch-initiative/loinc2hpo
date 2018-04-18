package org.monarchinitiative.loinc2hpo.fhir;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

import java.util.List;
import java.util.Map;

/**
 * This interface defines classes that generate fake FHIR resources
 */
public interface FhirResourceFaker {

    /**
     * Generate a fake patient object
     * @return
     */
    Patient fakePatient();

    /**
     * Generate a list of fake patient objects.
     * @param num size of the list
     * @return a list of fake patients with specified size
     */
    List<Patient> fakePatients(int num);

    /**
     * Generate a fake observation object
     * @return
     */
    Observation fakeObservation();

    /**
     * Generate a fake observation object with pre-defined LOINC and subject.
     * @param loincId
     * @param patient subject of the observation
     * @return an observation object
     */
    Observation fakeObservation(LoincId loincId, Patient patient);

    /**
     * For every patient, generate a list of fake observations for each LOINC test.
     * @param patientList
     * @param loincList
     * @return a map of patient: observations
     */
    Map<Patient, List<Observation>> fakeObservations(List<Patient> patientList, List<LoincId> loincList);

}
