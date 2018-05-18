package org.monarchinitiative.loinc2hpo.testresult;

import java.sql.Date;
import java.util.List;

public interface PhenoSetTimeLine {

    PhenoSet phenoset();

    List<PhenotypeComponent> getTimeLine();

    void insert(PhenotypeComponent phenotypeComponent);

    void delete(PhenotypeComponent phenotypeComponent);

    PhenotypeComponent current(Date date);
}
