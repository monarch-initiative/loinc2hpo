package org.monarchinitiative.loinc2hpo.testresult;

import sun.tools.java.Identifier;

import java.util.Date;
import java.util.List;

/**
 * This class is used to represent the summary of a patient. It should be able to answer at least one question:
 * Given a time period, what phenotype abnormalities does the patient have
 */
public interface PatientSummary {

    //subject with identifier

    /**
     * Return subject identifier. This is the preferred way for identification.
     * @return
     */
    List<Identifier> patientIdentifier();

    /**
     * Return the id for the patient
     * @return
     */
    String patientId();

    //A list of lab tests that the patient performed

    /**
     * Add one more test for the patient
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
    void addPhenoManifest(AbnormalityComponent abnormalityComponent);


    /**
     * Return all lab tests performed on the patient
     * @return
     */
    List<LabTest> tests();

    /**
     * Returns a list of phenotype manifestations during a specified period. Core function of this class.
     * @param start
     * @param end
     * @return a set of HPO terms
     */
    List<AbnormalityComponent> phenoDuring(Date start, Date end);

    /**
     * Return a list of phenotype menifestations in a patient's lifetime.
     * @return
     */
    List<AbnormalityComponent> phenoSinceBorn();


}
