package fhir;




import org.hl7.fhir.dstu3.model.Observation;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.model.Outcome;
import org.monarchinitiative.loinc2hpocore.model.LoincId;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.ObservationDtu3;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.Uberobservation;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;



public class ObservationWithCodedValueTest extends TestBase {


    @Test
    public void testEcoliBloodCulture() {
        Observation ecoliObservation = ecoliNoInterpretationBloodCulture();
        Uberobservation uberobservation = new ObservationDtu3(ecoliObservation);
        LoincId loincId = new LoincId("600-7");
        Optional<LoincId> loincOpt = uberobservation.getLoincId();
        assertTrue(loincOpt.isPresent());
        assertEquals(loincId, loincOpt.get());
        Optional<Outcome> outcomeOpt = uberobservation.getOutcome();
        assertTrue(outcomeOpt.isPresent());
        Outcome outcome = outcomeOpt.get();
        assertTrue(outcome.isNominal());
        assertEquals("112283007:http://snomed.info/sct:Escherichia coli", outcome.getOutcome());
    }




}
