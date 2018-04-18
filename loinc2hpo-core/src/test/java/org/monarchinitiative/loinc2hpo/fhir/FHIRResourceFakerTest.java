package org.monarchinitiative.loinc2hpo.fhir;

import ca.uhn.fhir.rest.api.MethodOutcome;
import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.loinc.LOINCEXAMPLE;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class FHIRResourceFakerTest {

    private static FhirResourceFaker resourceGenerator;
    private static List<Patient> randPatients;

    @BeforeClass
    public static void setup() {
        String path = "/Users/zhangx/Downloads/LOINC_2/LoincTableCore.csv";
        Map<LoincId, LoincEntry> loincEntryMap = LoincEntry.getLoincEntryList(path);
        assertNotNull(loincEntryMap);
        assertTrue(loincEntryMap.size() > 1000);
        resourceGenerator = new FhirResourceFakerImpl(loincEntryMap);

        randPatients = resourceGenerator.fakePatients(10);
    }

    @Test
    public void testFaker() {
        Faker faker = new Faker();
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String nameWithMiddle = faker.name().nameWithMiddle();
        System.out.println(firstName);
        System.out.println(lastName);
        System.out.println(nameWithMiddle);


        Name name = faker.name();
        System.out.println(name.firstName() + "\n" + name.lastName() + "\n" + name.nameWithMiddle());

        Address address = faker.address();
        System.out.println(address.streetAddress() + "\n" + address.buildingNumber() + "\n" +
            address.city() + ", " + address.state() + " " + address.zipCode() + "\n" + address.country());

        System.out.println(faker.address().fullAddress());

        System.out.println(faker.phoneNumber().phoneNumber());

        System.out.println(faker.address().streetAddress(true));

        //System.out.println(faker.);

    }
    @Test
    public void generateObservation() throws Exception {
        LOINCEXAMPLE[] testLoinc = LOINCEXAMPLE.values();
        for (LOINCEXAMPLE loincexample : testLoinc) {
            LoincId loincId = new LoincId(loincexample.toString());
            for (Patient patient : randPatients) {
                System.out.println(FhirResourceRetriever.toJsonString(resourceGenerator.fakeObservation(loincId, patient)));
            }
        }
    }

    @Test
    public void generatePatient() throws Exception {

        assertEquals(10, randPatients.size());
        randPatients.forEach(p -> System.out.println(FhirResourceRetriever.toJsonString(p)));
        //System.out.println(FhirResourceRetriever.toJsonString(randPatients.get(0)));

    }

    @Test
    public void generatePatientObservationList() {
        List<LoincId> loincIds = LOINCEXAMPLE.loincExamples();
        Map<Patient, List<Observation>> patientObservationsList = resourceGenerator.fakeObservations(randPatients, loincIds);
        assertEquals(randPatients.size(), patientObservationsList.size());
        patientObservationsList.get(randPatients.get(0)).forEach(o -> System.out.print(FhirResourceRetriever.toJsonString(o)));
    }

    @Test
    public void testUpload() {
        //List<Patient> patient = resourceGenerator.getPatients(1);
        Patient patient = new Patient();
        patient.setId("jaxpatient/008");
        patient.addName().addGiven("James").setFamily("Bond").addGiven("008");
        patient.addIdentifier().setSystem("www.jax.org").setValue("little mouse2");
        System.out.println(FhirResourceRetriever.toJsonString(patient));
        MethodOutcome outcome = FhirResourceRetriever.upload(patient);
        System.out.println(outcome.getId().getValue());

    }

    @Test
    public void testUpload2() {
        Patient patient = resourceGenerator.fakePatients(1).get(0);
        System.out.println(FhirResourceRetriever.toJsonString(patient));
        MethodOutcome outcome = FhirResourceRetriever.upload(patient);
        System.out.println(outcome.getId().getValue());
    }

}