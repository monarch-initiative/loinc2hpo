package org.monarchinitiative.loinc2hpo.testresult;

import com.github.phenomics.ontolib.ontology.data.TermId;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

public interface TestResult {


    public TermId getHpoInference();

    public LoincId getLoincId();

    public Observation getObservedValue();



}
