package org.monarchinitiative.loinc2hpo.fhir;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.exception.*;
import org.monarchinitiative.loinc2hpo.loinc.HpoTermId4LoincTest;
import org.monarchinitiative.loinc2hpo.loinc.Loinc2HPOAnnotation;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

import java.util.*;
import java.util.stream.Collectors;

public class ObservationAnalysisFromInterpretation implements ObservationAnalysis {

    private LoincId loincId;
    private CodeableConcept interpretationField;  //this is the interpretation field of a fhir loinc observation
    private Map<LoincId, Loinc2HPOAnnotation> annotationMap; //this is the annotation map that we need to interpret the result

    public ObservationAnalysisFromInterpretation(LoincId loincId, CodeableConcept interpretation, Map<LoincId, Loinc2HPOAnnotation> annotationMap) {
        this.loincId = loincId;
        this.interpretationField = interpretation;
        this.annotationMap = annotationMap;
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
    public HpoTermId4LoincTest getHPOforObservation() throws UnsupportedCodingSystemException, AmbiguousResultsFoundException, AnnotationNotFoundException, UnrecognizedCodeException {
        //here we use a map to store the results: since there could be more than one interpretation coding system,
        //we try them all and store the results in a map <external code, result in internal code>
        Map<Code, Code> results = new HashMap<>();
        Loinc2HPOAnnotation annotationForLoinc = annotationMap.get(this.loincId); //get the annotation class for this loinc code
        if (annotationForLoinc == null) throw new AnnotationNotFoundException();
        Set<Code> interpretationCodes = getInterpretationCodes(); //all interpretation codes in different coding systems. Expect one in most cases.
        interpretationCodes.stream()
                //filter out interpretation codes whose coding system are not mapped by us
                .filter(p -> CodeSystemConvertor.getCodeContainer().getCodeSystemMap().containsKey(p.getSystem()))
                .forEach(p -> {
                    Code internalCode = null;
                    try {
                        internalCode = CodeSystemConvertor.convertToInternalCode(p);
                        results.put(p, internalCode);
                    } catch (InternalCodeNotFoundException e) {
                        e.printStackTrace();
                    }
                });
        List<Code> distinct = results.values().stream().distinct().collect(Collectors.toList());
        if (distinct.size() == 1) {
            HpoTermId4LoincTest hpoTermId4LoincTest = annotationForLoinc.loincInterpretationToHPO(distinct.get(0));
            if (hpoTermId4LoincTest == null) throw new UnrecognizedCodeException();
            return hpoTermId4LoincTest;
        } else {
            throw new AmbiguousResultsFoundException();
        }
    }

}
