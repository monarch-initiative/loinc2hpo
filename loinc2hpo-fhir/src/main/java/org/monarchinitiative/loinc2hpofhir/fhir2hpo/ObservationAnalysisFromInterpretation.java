package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.exception.*;
import org.monarchinitiative.loinc2hpocore.annotationmodel.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.util.*;
import java.util.stream.Collectors;

public class ObservationAnalysisFromInterpretation implements ObservationAnalysis {

    private Loinc2Hpo loinc2Hpo;
    private Observation observation;
    public ObservationAnalysisFromInterpretation(Loinc2Hpo loinc2Hpo,
                                                 Observation observation){
        this.loinc2Hpo = loinc2Hpo;
        this.observation = observation;
    }

    public Set<Code> getInterpretationCodes() {
        return this.observation.getInterpretation().getCoding().stream()
                .map(c -> new Code(c.getSystem(), c.getCode(), null))
                .collect(Collectors.toSet());
    }


    @Override
    public HpoTerm4TestOutcome getHPOforObservation() {
        LoincId loincId =
                FhirObservationUtil.getLoincIdOfObservation(this.observation);
        Collection<Code> interpretationCodes = getInterpretationCodes(); //all
        // interpretation codes in different coding systems. Expect one in most cases.

        //here we use a map to store the results: since there could be more than one interpretation coding system,
        //we try them all and store the results in a map <external code, result in internal code>
        Map<Code, Code> results = new HashMap<>();

        interpretationCodes
                .forEach(p -> {
                    Code internalCode = null;
                    try {
                        internalCode = loinc2Hpo.convertToInternal(p);
                        results.put(p, internalCode);
                    } catch (Loinc2HpoRuntimeException e) {
                        e.printStackTrace();
                    }
                });
        List<Code> distinct = results.values().stream().distinct().collect(Collectors.toList());

        if (distinct.size() > 1){
            throw Loinc2HpoRuntimeException.ambiguousResults();
        }

        return loinc2Hpo.query(loincId, distinct.get(0));
    }

}
