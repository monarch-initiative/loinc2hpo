package org.monarchinitiative.loinc2hpo.testresult;

import java.sql.Date;
import java.util.List;

public interface PhenoSetTimeLine {

    PhenoSet phenoset();

    List<PhenoSetComponent> getTimeLine();

    void insert(PhenoSetComponent phenoSetComponent);

    void delete(PhenoSetComponent phenoSetComponent);

    PhenoSetComponent current(Date date);
}
