package org.monarchinitiative.loinc2hpo.testresult;

import org.hl7.fhir.dstu3.model.Patient;

import java.util.Date;
import java.util.List;

/**
 * This class is used to represent the summary of a patient. It should have the following components:
 * patient id;
 * lab tests;
 * phenotype abnormalities
 */
public interface PatientSummary {

    /**
     * Reference to the subject
     * @return
     */
    Patient patient();

    //A list of lab tests that the patient performed

    /**
     * Add one more test for the patient
     * If the test outcome can be transformed into a HPO term, add the phenotype to a phenoset timeline.
     */
    void addTest(LabTest test);

    /**
     * Add a list of tests for the patient
     * @param tests
     */
    void addTest(List<LabTest> tests);

    /**
     * Add a phenotype manifest
     */
    //void addPhenoManifest(PhenotypeComponent phenoSetComponent);


    /**
     * Return all lab tests performed on the patient
     * @return
     */
    List<LabTest> tests();

    /**
     * Returns a list of phenotype manifestations during a specified period. Core function of this class.
     * @param start
     * @param end
     * @return a set of phenoset components. It is likely that same phenotypes occur multiple times but with different period associated with them.
     */
    List<PhenotypeComponent> phenoDuring(Date start, Date end);

    /**
     * Return a list of phenotype menifestations in a patient's lifetime.
     * @return
     */
    List<PhenotypeComponent> phenoSinceBorn();


}
