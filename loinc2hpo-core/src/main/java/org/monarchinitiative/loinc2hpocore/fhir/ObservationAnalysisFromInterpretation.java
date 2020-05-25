package org.monarchinitiative.loinc2hpocore.fhir;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpocore.exception.*;
import org.monarchinitiative.loinc2hpocore.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.util.*;
import java.util.stream.Collectors;

public class ObservationAnalysisFromInterpretation implements ObservationAnalysis {

    private LoincId loincId;
    private CodeableConcept interpretationField;  //this is the interpretation field of a fhir loinc observation
    private Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap; //this is the annotation map that we need to interpret the result
    private CodeSystemConvertor convertor;

    public ObservationAnalysisFromInterpretation(LoincId loincId,
                                                 CodeableConcept interpretation, Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap, CodeSystemConvertor convertor) {
        this.loincId = loincId;
        this.interpretationField = interpretation;
        this.annotationMap = annotationMap;
        this.convertor = convertor;
    }

    public Set<Code> getInterpretationCodes() {
        Set<Code> interpretationCodes = new HashSet<>();
        interpretationField.getCoding().forEach(x -> {
            Code interpretationcode = Code.getNewCode()
                    .setSystem(x.getSystem())
                    .setCode(x.getCode());
            interpretationCodes.add(interpretationcode);
        });
        return interpretationCodes;
    }


    @Override
    public HpoTerm4TestOutcome getHPOforObservation() throws UnsupportedCodingSystemException, AmbiguousResultsFoundException, AnnotationNotFoundException, UnrecognizedCodeException {
        //here we use a map to store the results: since there could be more than one interpretation coding system,
        //we try them all and store the results in a map <external code, result in internal code>
        Map<Code, Code> results = new HashMap<>();
        LOINC2HpoAnnotationImpl annotationForLoinc = annotationMap.get(this.loincId); //get the annotation class for this loinc code
        if (annotationForLoinc == null) throw new AnnotationNotFoundException();
        Set<Code> interpretationCodes = getInterpretationCodes(); //all interpretation codes in different coding systems. Expect one in most cases.
        Set<String> mappedCodingSystem =
                convertor.getCodeConversionmap().keySet().stream().map(Code::getSystem).collect(Collectors.toSet());
        interpretationCodes.stream()
                //filter out interpretation codes whose coding system are not mapped by us
                .filter(p -> mappedCodingSystem.contains(p.getSystem()))
                .forEach(p -> {
                    Code internalCode = null;
                    try {
                        internalCode = convertor.convertToInternalCode(p);
                        results.put(p, internalCode);
                    } catch (InternalCodeNotFoundException e) {
                        e.printStackTrace();
                    }
                });
        List<Code> distinct = results.values().stream().distinct().collect(Collectors.toList());
        if (distinct.size() == 1) {
            HpoTerm4TestOutcome hpoTerm4TestOutcome = annotationForLoinc.loincInterpretationToHPO(distinct.get(0));
            if (hpoTerm4TestOutcome == null) throw new UnrecognizedCodeException();
            return hpoTerm4TestOutcome;
        } else {
            throw new AmbiguousResultsFoundException();
        }
    }

}
