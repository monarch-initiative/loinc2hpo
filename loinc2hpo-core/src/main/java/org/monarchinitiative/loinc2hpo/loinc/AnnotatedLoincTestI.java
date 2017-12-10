package org.monarchinitiative.loinc2hpo.loinc;

import com.github.phenomics.ontolib.formats.hpo.HpoTerm;

public interface AnnotatedLoincTestI {

    public HpoTerm loincValueToHpo(String loincCode, String value, String unit);
}
