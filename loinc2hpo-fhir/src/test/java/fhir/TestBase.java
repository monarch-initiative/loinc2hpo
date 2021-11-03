package fhir;

import ca.uhn.fhir.model.primitive.IdDt;
import org.hl7.fhir.dstu3.model.*;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestBase {
    /**
     * This page is part of the FHIR Specification (v4.0.1: R4 - Mixed Normative and STU).
     * This is the current published version. It defines v2 ABNORMAL FLAGS,
     * HL7-defined code system of concepts which specify a categorical assessment of an
     * observation value.
     * It is being communicated in FHIR in Observation.interpretation.
     */
    public static final String  hl7Version2Table0078 = "http://hl7.org/fhir/v2/0078";

    public static final String hl7ObservationInterpretation = "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation";

    public Observation lowHemoglobinObservation() {
        Patient patient = new Patient();
        patient.addIdentifier()
                .setSystem("http://acme.org/mrns")
                .setValue("12345");
        patient.addName()
                .setFamily("Jameson")
                .addGiven("J")
                .addGiven("Jonah");
        patient.setGender(Enumerations.AdministrativeGender.MALE);
        // Give the patient a temporary UUID so that other resources in the transaction can refer to it
        patient.setId(IdDt.newRandomUuid());
        // Create an observation object
        Observation observation = new Observation();
        observation.setStatus(Observation.ObservationStatus.FINAL);
        observation
                .getCode()
                .addCoding()
                .setSystem("http://loinc.org")
                .setCode("789-8")
                .setDisplay("Erythrocytes [#/volume] in Blood by Automated count");
        observation.setValue(
                new SimpleQuantity()
                        .setValue(4.12)
                        .setUnit("10 trillion/L")
                        .setSystem("http://unitsofmeasure.org")
                        .setCode("3*12/L"));
        Coding coding = new Coding().setSystem(hl7Version2Table0078).setCode("L").setDisplay("Low");
        List<Coding> codings = new ArrayList<>();
        codings.add(coding);
        observation.setInterpretation(
                new CodeableConcept()
                    .setCoding(codings)
        );
        // The observation refers to the patient using the ID, which is already set to a temporary UUID
        observation.setSubject(new Reference(patient.getId()));
        return observation;
    }





}
