package fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.dstu3.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TestBase {
    /**
     * This page is part of the FHIR Specification (v4.0.1: R4 - Mixed Normative and STU).
     * This is the current published version. It defines v2 ABNORMAL FLAGS,
     * HL7-defined code system of concepts which specify a categorical assessment of an
     * observation value.
     * It is being communicated in FHIR in Observation.interpretation.
     */
    public static final String  hl7Version2Table0078 = "http://hl7.org/fhir/v2/0078";

    private static final FhirContext ctxDstu3 = FhirContext.forDstu3();
    private static final IParser jsonparserDstu3 = ctxDstu3.newJsonParser();

    public static org.hl7.fhir.dstu3.model.Observation importDstu3Observation(String path) throws IOException {
        URL url = TestBase.class.getClassLoader().getResource(path);
        if (url == null) {
            throw new FileNotFoundException("Could not find " + path + " for testing");
        }
        File f = new File(url.getFile());
        if (! f.isFile()) {
            throw new FileNotFoundException("Could not find " + path + " for testing");
        }
        return (Observation) jsonparserDstu3.parseResource(new FileReader(f));
    }


    protected Observation lowHemoglobinObservation() {
        Patient patient = getPatient();
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


    /**
     * A normal range in adults is generally considered to be 4.35 to 5.65 million red blood cells
     * per microliter (mcL)
     * of blood for men and 3.92 to 5.13 million red blood cells per mcL of blood for women.
     * @return A FHIR Observation (dstu3) for high hemoglobin with reference range but no interpretation
     */
    protected Observation highHemoglobinWithValueRangeObservation() {
        Patient patient = getPatient();
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
                        .setValue(6.17)
                        .setUnit("10 trillion/L")
                        .setSystem("http://unitsofmeasure.org")
                        .setCode("3*12/L"));
        SimpleQuantity low = new SimpleQuantity();
        low.setValue(3.92).setSystem("http://unitsofmeasure.org").setUnit("10 trillion/L").setCode("3*12/L");
        observation.getReferenceRangeFirstRep().setLow(low);
        SimpleQuantity high = new SimpleQuantity();
        high.setValue(5.13).setSystem("http://unitsofmeasure.org").setUnit("10 trillion/L").setCode("3*12/L");
        observation.getReferenceRangeFirstRep().setHigh(high);
        observation.setSubject(new Reference(patient.getId()));
        return observation;
    }

    /**
     * Create a Patient object to user to build Observations.
     * @return Patient object with random data.
     */
    private Patient getPatient() {
        Patient patient = new Patient();
        patient.addName().setFamily("Smith").addGiven("Rob").addGiven("Bruce");
        patient.setGender(Enumerations.AdministrativeGender.MALE);
        patient.setActive(true);
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setSystem( "https://fhir.acme.io/fhir/code/thing" );
        coding.setCode( "thing" );
        coding.setDisplay("Thing stuff...");
        List<Coding> codings = new ArrayList<>();
        codings.add(coding);
        codeableConcept.setCoding( codings );
        Reference assigner = new Reference();
        assigner.setDisplay("Patient thing");
        Identifier thing = new Identifier();
        thing.setUse( Identifier.IdentifierUse.USUAL );
        thing.setType( codeableConcept );
        thing.setSystem( "https://debug.acme.io/thing/0" );
        thing.setValue( "99" );
        thing.setAssigner( assigner );
        List< Identifier > identifiers = patient.getIdentifier();
        identifiers.add( thing );
        patient.setIdentifier( identifiers );
        return patient;
    }


    protected Observation ecoliNoInterpretationBloodCulture() {
       Patient patient = getPatient();
        Observation observation = new Observation();
        observation.setStatus(Observation.ObservationStatus.FINAL);
        observation
                .getCode()
                .addCoding()
                .setSystem("http://loinc.org")
                .setCode("600-7")
                .setDisplay("Bacteria identified in Blood by Culture");
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding(new Coding()
                .setSystem("http://snomed.info/sct").setCode("112283007").setDisplay("Escherichia coli"));
        observation.setValue(codeableConcept);
        observation.setSubject(new Reference(patient.getId()));
        return observation;
    }



}
