package org.monarchinitiative.loinc2hpocore.testresult;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Date;

public interface PhenotypeComponent {
    /**
     * When does a patient start to show the phenotype
     * @return
     */
    Date effectiveStart();

    /**
     * When does a patient stops showing the phenotype
     * To simply the issue, let's assume that this data is never specified for new test, until we adjust previous tests based on new outcome
     * @return
     */
    Date effectiveEnd();

    /**
     * Is the phenotype effective at the specified time point?
     * @param timepoint
     * @return
     */
    boolean isEffective(Date timepoint);

    /**
     * Is the phenotype effective during the entire period
     * @param start
     * @param end
     * @return
     */
    boolean isPersistingDuring(Date start, Date end);

    /**
     * Did the phenotype occur during a period
     * @param start
     * @param end
     * @return
     */
    boolean occurredDuring(Date start, Date end);

    /**
     * Phenotype abnormality
     * @return
     */
    TermId abnormality();

    /**
     * Is the abnormality negated
     * @return
     */
    boolean isNegated();

    void changeEffectiveStart(Date date);

    /**
     * Setter to change the effective end time
     * @param end
     */
    void changeEffectiveEnd(Date end);
}
