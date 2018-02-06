package org.monarchinitiative.loinc2hpo.fhir;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.exception.AmbiguousResultsFoundException;
import org.monarchinitiative.loinc2hpo.exception.UnrecognizedCodeException;
import org.monarchinitiative.loinc2hpo.loinc.HpoTermId4LoincTest;
import org.monarchinitiative.loinc2hpo.loinc.Loinc2HPOAnnotation;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObservationAnalysisFromCodedValues implements ObservationAnalysis {

    private LoincId loincId;
    private CodeableConcept codedValue;
    private Map<LoincId, Loinc2HPOAnnotation> annotationMap;

    public ObservationAnalysisFromCodedValues(LoincId loincId, CodeableConcept codedvalue, Map<LoincId, Loinc2HPOAnnotation> annotationMap) {
        this.loincId = loincId;
        this.codedValue = codedvalue;
        this.annotationMap = annotationMap;
    }

    @Override
    public HpoTermId4LoincTest getHPOforObservation() throws AmbiguousResultsFoundException, UnrecognizedCodeException {
        Set<HpoTermId4LoincTest> results = new HashSet<>();
        codedValue.getCoding()
                .stream()
                .filter(p -> annotationMap.get(loincId).getCodes().contains(new Code(p)))
                .forEach(p -> {
                    results.add(annotationMap.get(loincId).loincInterpretationToHPO(new Code(p)));
                });
        if (results.size() > 1) {
            throw new AmbiguousResultsFoundException();
        }
        if (results.size() == 1) {
            return results.iterator().next();
        } else {
            throw new UnrecognizedCodeException();
        }
    }
}
