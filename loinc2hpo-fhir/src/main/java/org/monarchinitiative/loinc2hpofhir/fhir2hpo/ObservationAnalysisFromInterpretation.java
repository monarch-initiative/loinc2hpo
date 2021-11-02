package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.loinc2hpocore.exception.*;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Hpo2Outcome;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.stream.Collectors;

public class ObservationAnalysisFromInterpretation implements ObservationAnalysis {

    private final Loinc2Hpo loinc2Hpo;
    private final Observation observation;
    public ObservationAnalysisFromInterpretation(Loinc2Hpo loinc2Hpo,
                                                 Observation observation){
        this.loinc2Hpo = loinc2Hpo;
        this.observation = observation;
    }

    public Set<ShortCode> getInterpretationCodes() {
        return this.observation.getInterpretation().getCoding().stream()
                .map(c -> ShortCode.fromShortCode(c.getCode()))
                .collect(Collectors.toSet());
    }


    @Override
    public Hpo2Outcome getHPOforObservation() {
        LoincId loincId =
                FhirObservationUtil.getLoincIdOfObservation(this.observation);
        Collection<ShortCode> interpretationCodes = getInterpretationCodes(); //all
        // interpretation codes in different coding systems. Expect one in most cases.
        List<ShortCode> distinct = interpretationCodes.stream().distinct().collect(Collectors.toList());

        if (distinct.size() > 1){
            throw Loinc2HpoRuntimeException.ambiguousResults();
        }
        // TODO FIX ME
        TermId tid = TermId.of("LNC", loincId.toString());
        return loinc2Hpo.query(tid, Outcome.NORMAL()).get();
    }

}
