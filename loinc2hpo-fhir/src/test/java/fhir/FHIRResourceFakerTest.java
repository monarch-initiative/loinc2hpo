package fhir;


import org.hl7.fhir.dstu3.model.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.FhirResourceFaker;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.FhirResourceFakerImpl;
import org.monarchinitiative.loinc2hpocore.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;


@Disabled
public class FHIRResourceFakerTest {

    private static FhirResourceFaker resourceGenerator;
    private static List<Patient> randPatients;


    @BeforeAll
    public static void setup() {
        Path path = Paths.get("src", "test", "resources", "LoincTableCoreTiny.csv");
       Map<LoincId, LoincEntry> loincEntryMap = LoincEntry.getLoincEntryMap(path.toAbsolutePath().toString());

        resourceGenerator = new FhirResourceFakerImpl(loincEntryMap);
       randPatients = resourceGenerator.fakePatients(10);
    }

//    @Test
//    public void testLoincEntryMapSize() {
//        assertNotNull(loincEntryMap);
//        assertTrue(loincEntryMap.size() > 1000);
//    }

   @Test
    public void testFaker() {
       Patient patient = resourceGenerator.fakePatient();
        String name = patient.getNameFirstRep().getNameAsSingleString();
       System.out.println(name);

        HumanName hname = patient.getName().get(0);
        System.out.println(hname.getGiven());

        Address address = patient.getAddress().get(0);
       System.out.println(address.getCity() );

        System.out.println(address);

//        System.out.println(faker.phoneNumber().phoneNumber());
//
//        System.out.println(faker.address().streetAddress(true));

        //System.out.println(faker.);

    }
//    @Test
//    public void generateObservation() throws Exception {
//        LOINCEXAMPLE[] testLoinc = LOINCEXAMPLE.values();
//        for (LOINCEXAMPLE loincexample : testLoinc) {
//            LoincId loincId = new LoincId(loincexample.toString());
//            for (Patient patient : randPatients) {
//                System.out.println(FhirResourceRetriever.toJsonString(resourceGenerator.fakeObservation(loincId, patient)));
//            }
//        }
//    }
//
//    @Test
//    public void generatePatient() throws Exception {
//
//        assertEquals(10, randPatients.size());
//        randPatients.forEach(p -> System.out.println(FhirResourceRetriever.toJsonString(p)));
//        //System.out.println(FhirResourceRetriever.toJsonString(randPatients.get(0)));
//
//    }
//
//    @Test
//    public void generatePatientObservationList() {
//        List<LoincId> loincIds = LOINCEXAMPLE.loincExamples();
//        Map<Patient, List<Observation>> patientObservationsList = resourceGenerator.fakeObservations(randPatients, loincIds);
//        assertEquals(randPatients.size(), patientObservationsList.size());
//        patientObservationsList.get(randPatients.get(0)).forEach(o -> System.out.print(FhirResourceRetriever.toJsonString(o)));
//    }
//
//    @Test
//    public void testUpload() {
//        //List<Patient> patient = resourceGenerator.getPatients(1);
//        Patient patient = new Patient();
//        patient.setId("jaxpatient/008");
//        patient.addName().addGiven("James").setFamily("Bond").addGiven("008");
//        patient.addIdentifier().setSystem("www.jax.org").setValue("little mouse2");
//        System.out.println(FhirResourceRetriever.toJsonString(patient));
//        MethodOutcome outcome = FhirResourceRetriever.upload(patient);
//        System.out.println(outcome.getId().getValue());
//
//    }
//
//    @Test
//    public void testUpload2() {
//        Patient patient = resourceGenerator.fakePatients(1).get(0);
//        System.out.println(FhirResourceRetriever.toJsonString(patient));
//        MethodOutcome outcome = FhirResourceRetriever.upload(patient);
//        System.out.println(outcome.getId().getValue());
//    }
//
//    @Test
//    public void testUploadPatient() {
//        Patient patient = resourceGenerator.fakePatient();
//        MethodOutcome outcome = FhirResourceRetriever.upload(patient);
//        System.out.println(outcome.getId().getValue());
//        System.out.println(outcome.getId().getIdPart());
//    }
//
//    @Test
//    @Disabled
//    public void testUploadLoincPanels() throws Exception {
//        Patient patient = resourceGenerator.fakePatient();
//        LoincId panelId = new LoincId("35094-2");
//        LoincId sys = new LoincId("8480-6");
//        LoincId dias = new LoincId("8462-4");
//        Observation obsPanel = new Observation();//resourceGenerator.fakeObservation(panelId, patient);
//        obsPanel.setCode(new CodeableConcept().addCoding(new Coding().setSystem(Constants.LOINCSYSTEM).setCode("35094-2")));
//        Identifier identifier = new Identifier();
//        identifier.setSystem("Jax.org").setValue("Little mouse");
//        obsPanel.addIdentifier(identifier);
//
//        Observation compSys = new Observation();//resourceGenerator.fakeObservation(sys, patient);
//        Observation compDias = new Observation(); //resourceGenerator.fakeObservation(dias, patient);
////        Observation.ObservationComponentComponent compSys = new Observation.ObservationComponentComponent();
//        compSys.setCode(new CodeableConcept().addCoding(new Coding().setSystem(Constants.LOINCSYSTEM).setCode("8480-6")))
//                .setValue(new Quantity(80).setUnit("mm Hg"))
//                .addIdentifier(identifier);
////        Observation.ObservationComponentComponent compDias = new Observation.ObservationComponentComponent();
//        compDias.setCode(new CodeableConcept().addCoding(new Coding().setSystem(Constants.LOINCSYSTEM).setCode("8462-4")))
//                .setValue(new Quantity(120).setUnit("mm Hg"))
//                .addIdentifier(identifier);
////        obsPanel.addComponent(compSys).addComponent(compDias);
//        obsPanel.addContained(compSys).addContained(compDias);
//        System.out.println("no. resources contained: " + obsPanel.getContained().size());
//        System.out.println(FhirResourceRetriever.jsonParser.setPrettyPrint(true).encodeResourceToString(obsPanel));
////        obsPanel.getContained().forEach(c -> System.out.println(FhirResourceRetriever.jsonParser.setPrettyPrint(true).encodeResourceToString(c)));
////        MethodOutcome outcome = FhirResourceRetriever.upload(obsPanel);
////        System.out.println(outcome.getId().getValue());
////        System.out.println(outcome.getId().getIdPart());
//    }
//
//    @Test
//    public void testObservationComponenet() throws Exception {
//
//        LoincId panelId = new LoincId("35094-2");
//        LoincId sys = new LoincId("8480-6");
//        LoincId dias = new LoincId("8462-4");
//
//        Observation obsPanel = new Observation()
//                .setCode(new CodeableConcept().addCoding(new Coding().setSystem(Constants.LOINCSYSTEM).setCode("35094-2")))
//                .addIdentifier(new Identifier().setSystem("Jax.org").setValue("Little mouse"));
//
//        //blood pressure reference
//        SimpleQuantity lowSys = new SimpleQuantity();
//        lowSys.setValue(80).setUnit("mm Hg");
//        SimpleQuantity highSys = new SimpleQuantity();
//        highSys.setValue(120).setUnit("mm Hg");
//
//        SimpleQuantity lowDias = new SimpleQuantity();
//        lowDias.setValue(80).setUnit("mm Hg");
//        SimpleQuantity highDias = new SimpleQuantity();
//        highDias.setValue(120).setUnit("mm Hg");
//
//        Observation.ObservationComponentComponent compSys = new Observation.ObservationComponentComponent();
//        compSys.setCode(new CodeableConcept().addCoding(new Coding().setSystem(Constants.LOINCSYSTEM).setCode("8480-6")))
//                .setValue(new Quantity(80).setUnit("mm Hg"))
//                .addReferenceRange(new Observation.ObservationReferenceRangeComponent()
//                        .setHigh(highSys).setLow(lowSys));
//
//        Observation.ObservationComponentComponent compDias = new Observation.ObservationComponentComponent();
//        compDias.setCode(new CodeableConcept().addCoding(new Coding().setSystem(Constants.LOINCSYSTEM).setCode("8462-4")))
//                .setValue(new Quantity(120).setUnit("mm Hg"))
//                .addReferenceRange(new Observation.ObservationReferenceRangeComponent()
//                        .setHigh(highDias).setLow(lowDias));
//
//        obsPanel.addComponent(compSys).addComponent(compDias);
//
//        System.out.println(FhirResourceRetriever.jsonParser.setPrettyPrint(true).encodeResourceToString(obsPanel));
//
//    }

}