package org.monarchinitiative.loinc2hpocore.testresult;

import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpocore.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.util.Date;

/**
 * This will be used to represent a more complete lab test. It has the following component:
 * subject,
 * test effective period (start and end date)
 * test id in LOINC,
 * test resource id (in case we want to refer to FHIR resource)
 * LabTestOutcome
 */

public interface LabTest {

    Patient subject();

    /**
     * Test effective start time
     * @return
     */
    Date effectiveStart();

    /**
     * Test effective end time
     * @return
     */
    Date effectiveEnd();

    /**
     * What is the test
     * @return
     */
    LoincId loinc();

    /**
     * Return the FHIR resource ID
     * @return
     */
    String resourceId();

    /**
     * What is the outcome
     * @return
     */
    HpoTerm4TestOutcome outcome();
}
