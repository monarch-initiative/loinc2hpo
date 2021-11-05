package fhir;

import org.hl7.fhir.dstu3.model.Observation;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.ObservationDtu3;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.Uberobservation;

import java.io.IOException;
import java.util.Optional;

import static fhir.TestBase.importDstu3Observation;
import static org.junit.jupiter.api.Assertions.*;

public class Dstu3ObservationTest {



    @Test
    void testGlucoseHigh() throws IOException {
        String jsonPath = "json/glucoseHigh.fhir";
        Observation glucoseHigh = importDstu3Observation(jsonPath);
        Uberobservation uberobservation = new ObservationDtu3(glucoseHigh);
        LoincId expectedLoincId = new LoincId("15074-8");
        Optional<LoincId> opt = uberobservation.getLoincId();
        assertTrue(opt.isPresent());
        assertEquals(expectedLoincId, opt.get());
        Optional<Outcome> opt2 = uberobservation.getOutcome();
        assertTrue(opt2.isPresent());
        assertEquals(Outcome.HIGH(), opt2.get());
        assertNotEquals(Outcome.LOW(), opt2.get());
        assertNotEquals(Outcome.NORMAL(), opt2.get());
    }

    @Test
    void testGlucoseLow() throws IOException {
        String jsonPath = "json/glucoseLow.fhir";
        Observation glucoseAbnormal = importDstu3Observation(jsonPath);
        Uberobservation uberobservation = new ObservationDtu3(glucoseAbnormal);
        LoincId expectedLoincId = new LoincId("15074-8");
        Optional<LoincId> opt = uberobservation.getLoincId();
        assertTrue(opt.isPresent());
        assertEquals(expectedLoincId, opt.get());
        Optional<Outcome> opt2 = uberobservation.getOutcome();
        assertTrue(opt2.isPresent());
        assertEquals(Outcome.LOW(), opt2.get());
        assertNotEquals(Outcome.NORMAL(), opt2.get());
        assertNotEquals(Outcome.HIGH(), opt2.get());
    }

    @Test
    void testGlucoseNormal() throws IOException {
        String jsonPath = "json/glucoseNormal.fhir";
        Observation glucoseAbnormal = importDstu3Observation(jsonPath);
        Uberobservation uberobservation = new ObservationDtu3(glucoseAbnormal);
        LoincId expectedLoincId = new LoincId("15074-8");
        Optional<LoincId> opt = uberobservation.getLoincId();
        assertTrue(opt.isPresent());
        assertEquals(expectedLoincId, opt.get());
        Optional<Outcome> opt2 = uberobservation.getOutcome();
        assertTrue(opt2.isPresent());
        assertEquals(Outcome.NORMAL(), opt2.get());
        assertNotEquals(Outcome.LOW(), opt2.get());
        assertNotEquals(Outcome.HIGH(), opt2.get());
    }
}
