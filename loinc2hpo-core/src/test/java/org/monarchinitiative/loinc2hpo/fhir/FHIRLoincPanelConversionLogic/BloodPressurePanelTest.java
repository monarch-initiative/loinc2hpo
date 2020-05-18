package org.monarchinitiative.loinc2hpo.fhir.FHIRLoincPanelConversionLogic;

import org.hl7.fhir.dstu3.model.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpo.Constants;
import org.monarchinitiative.loinc2hpo.ResourceCollection;
import org.monarchinitiative.loinc2hpo.SharedResourceCollection;
import org.monarchinitiative.loinc2hpo.fhir.*;
import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public class BloodPressurePanelTest {

    private static ResourceCollection resources = SharedResourceCollection.resourceCollection;

    private static FHIRLoincPanel bpPanel;
    private static FHIRLoincPanelFactory panelFactory = new FHIRLoincPanelFactoryLazy();

    @BeforeAll
    public static void setup() throws Exception{
        //create a blood pressure FHIR resource for testing
        Observation systolic = new Observation();
        Observation dystolic = new Observation();
        Reference subject = new Reference().setReference("patient/p007");
        systolic.setSubject(subject)
                .setCode(new CodeableConcept().addCoding(new Coding().setSystem(Constants.LOINCSYSTEM).setCode("8480-6")))
                .setValue(new Quantity().setValue(150).setUnit("mmHg"))
                .setInterpretation(new CodeableConcept().addCoding(new Coding().setSystem(Constants.V2OBSERVATIONINTERPRETATION).setCode("H")));

        dystolic.setSubject(subject)
                .setCode(new CodeableConcept().addCoding(new Coding().setSystem(Constants.LOINCSYSTEM).setCode("8462-4")))
                .setValue(new Quantity().setValue(100).setUnit("mmHg"))
                .setInterpretation(new CodeableConcept().addCoding(new Coding().setSystem(Constants.V2OBSERVATIONINTERPRETATION).setCode("H")));
        Observation.ObservationRelatedComponent related1 = new Observation.ObservationRelatedComponent();
        //related1.setTarget()

        Observation loincPanel = new Observation();
        loincPanel.setSubject(subject)
                .setCode(new CodeableConcept().addCoding(new Coding().setSystem(Constants.LOINCSYSTEM).setCode("35094-2")))
                .setSubject(new Reference().setIdentifier(new Identifier().setSystem("org.jax").setValue("Mouse Jerry")));
        loincPanel.addRelated(related1);
                //.setInterpretation(new CodeableConcept().addCoding(new Coding().setSystem("http://hl7.org/fhir/v2/0078").setCode("H")));


        bpPanel = panelFactory.createFhirLoincPanel(new LoincId("35094-2"));
        Map<LoincId, Observation> components = new HashMap<>();
        components.put(new LoincId("8480-6"), systolic);
        components.put(new LoincId("8462-4"), dystolic);
        bpPanel.addComponents(components);
        FhirObservationAnalyzer.init(resources.loincIdSet(), resources.annotationMap());
        FHIRLoincPanel.initResources(resources.loincIdSet(), resources.loincEntryMap(), resources.annotationMap());
        assertEquals(2, bpPanel.panelComponents().size());
        assertNotNull(resources.loincIdSet());
        assertTrue(resources.loincIdSet().size() > 5000);
        assertNotNull(resources.annotationMap());


    }

    @Test
    public void getHPOforObservation() throws Exception {
        HpoTerm4TestOutcome outcome = bpPanel.getHPOforObservation();
        assertNotNull(outcome);
        System.out.println(outcome.getId().getValue());

    }

}