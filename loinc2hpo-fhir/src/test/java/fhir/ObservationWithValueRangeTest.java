package fhir;

import org.hl7.fhir.dstu3.model.Observation;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.model.Outcome;
import org.monarchinitiative.loinc2hpocore.model.LoincId;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.ObservationDtu3;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.Uberobservation;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test using an Observation without a FHIR interpretation but with numerical values
 */
public class ObservationWithValueRangeTest extends TestBase {

  /**
   * This observation does not have an Interpretation, but we can infer the value to be high
   * based on the reference range.
   */
  @Test
  public void highHbWithReferenceRange() {
    Observation observation = highHemoglobinWithValueRangeObservation();
    Uberobservation uberobservation = new ObservationDtu3(observation);
    LoincId erysInBlood = new LoincId("789-8");
    Optional<LoincId> opt = uberobservation.getLoincId();
    assertTrue(opt.isPresent());
    assertEquals(erysInBlood, opt.get());
    Optional<Outcome> outcomeOpt = uberobservation.getOutcome();
    assertTrue(outcomeOpt.isPresent());
    assertEquals(Outcome.HIGH(), outcomeOpt.get());
  }

}