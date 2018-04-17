package org.monarchinitiative.loinc2hpo.fhir;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.util.RandomGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class FHIRResourceGeneratorTest {

    private static FHIRResourceGenerator resourceGenerator;
    private static List<Patient> randPatients;

    @BeforeClass
    public static void setup() {
        String path = "/Users/zhangx/Downloads/LOINC_2/LoincTableCore.csv";
        Map<LoincId, LoincEntry> loincEntryMap = LoincEntry.getLoincEntryList(path);
        assertNotNull(loincEntryMap);
        assertTrue(loincEntryMap.size() > 1000);
        resourceGenerator = new FHIRResourceGenerator(loincEntryMap);

        randPatients = resourceGenerator.generatePatient(10);
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
        FHIRResourceGenerator.LOINCEXAMPLE[] testLoinc = FHIRResourceGenerator.LOINCEXAMPLE.values();
        for (FHIRResourceGenerator.LOINCEXAMPLE loincexample : testLoinc) {
            LoincId loincId = new LoincId(loincexample.toString());
            for (Patient patient : randPatients) {
                System.out.println(FhirResourceRetriever.toJsonString(resourceGenerator.generateObservation(loincId, patient)));
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
        List<LoincId> loincIds = resourceGenerator.loincExamples();
        Map<Patient, List<Observation>> patientObservationsList = resourceGenerator.randPatientAndObservation(randPatients, loincIds);
        assertEquals(randPatients.size(), patientObservationsList.size());
        patientObservationsList.get(randPatients.get(0)).forEach(o -> System.out.print(FhirResourceRetriever.toJsonString(o)));
    }

}