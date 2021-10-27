package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.hl7.fhir.dstu3.model.*;

import org.monarchinitiative.loinc2hpocore.annotationmodel.HpoTerm4TestOutcome;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FhirObservationDecorator {
    /**
     * This page is part of the FHIR Specification (v4.0.1: R4 - Mixed Normative and STU).
     * This is the current published version. It defines v2 ABNORMAL FLAGS,
     * HL7-defined code system of concepts which specify a categorical assessment of an
     * observation value.
     * It is being communicated in FHIR in Observation.interpretation.
     */
    public static final String  hl7Version2Table0078 = "http://hl7.org/fhir/v2/0078";

    private final FhirObservation2Hpo fhir2hpo;

    private final Map<TermId, String> id2labelMap;

    public FhirObservationDecorator(FhirObservation2Hpo fhir2hpo, Map<TermId, String> id2label) {
        this.fhir2hpo = fhir2hpo;
        this.id2labelMap = id2label;
    }


    public Optional<Observation> hpoObservation(Observation observation) {
        try {
            Optional<HpoTerm4TestOutcome> opt = fhir2hpo.fhir2hpo(observation);
            if (! opt.isPresent()) {
                return Optional.empty();
            }
            HpoTerm4TestOutcome result = opt.get();
            TermId hpoId = result.getId();
            if (! this.id2labelMap.containsKey(hpoId))
                return Optional.empty();
            String hpoLabel = this.id2labelMap.get(hpoId);
            Reference patientId = observation.getSubject();
            Observation hpoObservation = new Observation();
            hpoObservation.setStatus(Observation.ObservationStatus.FINAL);
            hpoObservation
                    .getCode()
                    .addCoding()
                    .setSystem("http://hpo.jax.org")
                    .setCode(hpoId.getValue())
                    .setDisplay(hpoLabel);
            BooleanType btype = new BooleanType(true);
            hpoObservation.setValue(btype);
            hpoObservation.setInterpretation(abnormal());
            Reference reference = new Reference(observation);
            hpoObservation.addBasedOn(reference);
            return Optional.of(hpoObservation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private CodeableConcept abnormal() {
        Coding coding = new Coding().setSystem(hl7Version2Table0078).setCode("L").setDisplay("Low");
        List<Coding> codings = new ArrayList<>();
        codings.add(coding);
        return new CodeableConcept().setCoding(codings);
    }

}
