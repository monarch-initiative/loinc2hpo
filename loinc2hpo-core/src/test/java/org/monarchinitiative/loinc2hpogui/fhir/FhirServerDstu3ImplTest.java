package org.monarchinitiative.loinc2hpogui.fhir;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.Constants;
import org.monarchinitiative.loinc2hpocore.fhir.FhirResourceParser;
import org.monarchinitiative.loinc2hpocore.fhir.FhirResourceParserDstu3;
import org.monarchinitiative.loinc2hpocore.fhir.FhirServer;
import org.monarchinitiative.loinc2hpocore.fhir.FhirServerDstu3Impl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class FhirServerDstu3ImplTest {

    FhirServer fhirServer = new FhirServerDstu3Impl(Constants.HAPIFHIRTESTSERVER);

    @Test
    public void restifulGenericClient()  {
    }

    @Test
    public void getBaseAddress()  {
    }

    @Test
    @Disabled
    public void getPatient()  {
    }

    @Test
    @Disabled
    public void getPatient1()  {
    }

    @Test
    @Disabled
    public void getPatient2()  {

        Patient patient = fhirServer.getPatient("2959435").get(0);
        FhirResourceParser parser = new FhirResourceParserDstu3();
        parser.setPrettyPrint(true);
        //System.out.println(parser.toJson(patient));
        System.out.println(patient.getIdElement().getIdPart());

        List<Observation> observationList = fhirServer.getObservation(patient);
        assertFalse(observationList.isEmpty());
        assertEquals(10, observationList.size());
        //observationList.stream().map(parser::toJson).forEach(System.out::println);

    }

    @Test
    @Disabled
    public void getPatient3() throws Exception {
        List<Patient> patient = fhirServer.getPatient("Harry", "Metz", "002-837-6481", "79442-0781");
        assertFalse(patient.isEmpty());
    }

    @Test
    @Disabled
    public void getObservation() throws Exception {
    }

    @Test
    @Disabled
    public void upload() throws Exception {
    }

    @Test
    @Disabled
    public void upload1() throws Exception {
    }

}