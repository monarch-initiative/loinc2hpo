package org.monarchinitiative.loinc2hpo.testresult;

import java.util.Date;
import java.util.List;

public interface PhenoSetTimeLine {

    PhenoSet phenoset();

    List<PhenotypeComponent> getTimeLine();

    void insert(PhenotypeComponent phenotypeComponent);

    //void delete(PhenotypeComponent phenotypeComponent);

    PhenotypeComponent current(Date date);

    //a phenotype that persisted during the entire period
    PhenotypeComponent persistDuring(Date start, Date end);

    //a list of phenotypes that occurred at least some time during the period
    List<PhenotypeComponent> occurredDuring(Date start, Date end);
}
