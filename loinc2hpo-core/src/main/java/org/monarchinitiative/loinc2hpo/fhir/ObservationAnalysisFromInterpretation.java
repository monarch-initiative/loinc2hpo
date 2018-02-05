package org.monarchinitiative.loinc2hpo.fhir;

import org.apache.jena.tdb.store.Hash;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystem;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.exception.*;
import org.monarchinitiative.loinc2hpo.loinc.HpoTermId4LoincTest;
import org.monarchinitiative.loinc2hpo.loinc.Loinc2HPOAnnotation;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.testresult.BasicLabTestResultInHPO;
import org.monarchinitiative.loinc2hpo.testresult.LabTestResultInHPO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObservationAnalysisFromInterpretation implements ObservationAnalysis {

    private LoincId loincId;
    private CodeableConcept interpretationField;  //this is the interpretation field of a fhir loinc observation
    private Map<LoincId, Loinc2HPOAnnotation> annotationMap; //this is the annotation map that we need to interpret the result

    public ObservationAnalysisFromInterpretation(LoincId loincId, CodeableConcept interpretation, Map<LoincId, Loinc2HPOAnnotation> annotationMap) {
        this.loincId = loincId;
        this.interpretationField = interpretation;
        this.annotationMap = annotationMap;
    }

    private Map<String, Code> getInterpretationCodes() {
        Map<String, Code> interpretationCodes = new HashMap<>();
        interpretationField.getCoding().forEach(x -> {
            String system = x.getSystem();
            Code interpretationcode = Code.getNewCode()
                    .setSystem(x.getSystem())
                    .setCode(x.getCode());
            interpretationCodes.put(system, interpretationcode);
        });
        return interpretationCodes;
    }


    @Override
    public HpoTermId4LoincTest getHPOforObservation() throws InternalCodeNotFoundException, UnsupportedCodingSystemException, AmbiguousResultsFoundException {
        //here we only look at interpretation code system defined by HL7
        Map<String, HpoTermId4LoincTest> results = new HashMap<>();
        Loinc2HPOAnnotation annotationForLoinc = annotationMap.get(this.loincId); //get the annotation class for this loinc code
        Map<String, Code> interpretationCodes = getInterpretationCodes();
        interpretationCodes.entrySet().stream()
                .filter(p -> CodeSystemConvertor.getCodeContainer().getCodeSystemMap().containsKey(p.getKey()))
                .forEach(p -> {
                    Code internalCode = null;
                    try {
                        internalCode = CodeSystemConvertor.convertToInternalCode(p.getValue());
                    } catch (InternalCodeNotFoundException e) {
                        e.printStackTrace();
                    }
                    results.put(p.getKey(), annotationForLoinc.loincInterpretationToHPO(internalCode));
                });
        if (results.values().size() == 1) {
            return results.values().stream().findAny().get();
        } else {
            throw new AmbiguousResultsFoundException();
        }
    }

}
