package org.monarchinitiative.loinc2hpo.testresult;

import org.monarchinitiative.phenol.formats.hpo.HpoTerm;

import java.util.Date;

public interface AbnormalityComponent {
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
     * Phenotype abnormality
     * @return
     */
    HpoTerm abnormality();

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
