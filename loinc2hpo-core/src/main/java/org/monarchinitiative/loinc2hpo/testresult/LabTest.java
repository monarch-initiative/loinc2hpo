package org.monarchinitiative.loinc2hpo.testresult;

import org.monarchinitiative.loinc2hpo.loinc.LoincId;

/**
 * This will be used to represent a lab test. Implementing class should return what is the test and what is the outcome.
 */
public interface LabTest {
    /**
     * What is the test
     * @return
     */
    LoincId getTest();

    /**
     * What is the outcome
     * @return
     */
    LabTestOutcome getOutcome();
}
