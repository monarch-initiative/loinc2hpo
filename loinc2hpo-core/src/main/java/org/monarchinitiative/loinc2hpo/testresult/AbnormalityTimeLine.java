package org.monarchinitiative.loinc2hpo.testresult;

import java.sql.Date;

public interface AbnormalityTimeLine {

    void insert(AbnormalityComponent abnormalityComponent);

    void delete(AbnormalityComponent abnormalityComponent);

    AbnormalityComponent current(Date date);
}
