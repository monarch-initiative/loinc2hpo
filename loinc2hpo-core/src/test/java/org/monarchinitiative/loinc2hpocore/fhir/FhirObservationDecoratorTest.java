package org.monarchinitiative.loinc2hpocore.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
import org.monarchinitiative.loinc2hpocore.annotationmodel.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Loinc2HpoAnnotationCsvEntry;
import org.monarchinitiative.loinc2hpocore.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpocore.fhir2hpo.FhirObservation2Hpo;
import org.monarchinitiative.loinc2hpocore.fhir2hpo.FhirObservationDecorator;
import org.monarchinitiative.loinc2hpocore.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.sql.Ref;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The goal of this test class is to demonstrate how to decorate a FHIR observation for low hemoglobin
 * with a corresponding HPO term. The lonc2hpo annotations we require are in the file used by this test
 * class -- here are the three entries
 * LOINC - 789-8 Erythrocytes [#/volume] in Blood by Automated count
 * <pre>
 * 789-8	Qn	FHIR	N	HP:0020058	true	2018-10-12T14:38:05	JGM:azhang	NA	NA	0.1	true	NA
 * 789-8	Qn	FHIR	H	HP:0020059	false	2018-10-12T14:38:05	JGM:azhang	NA	NA	0.1	true	NA
 * 789-8	Qn	FHIR	L	HP:0020060	false	2018-10-12T14:38:05	JGM:azhang	NA	NA	0.1	true	NA
 * </pre>
 * The test base class also has a map that shows the labels for these three HPO terms.
 */
public class FhirObservationDecoratorTest extends TestBase {
    private final Observation lowHemoglobin = lowHemoglobinObservation();
    private final Map<String, Term> hpoTermMap = getHpoTermMap();
    private final String annotationPath = this.getClass().getClassLoader().getResource("annotations.tsv").getPath();
    private final String coreTablePath = this.getClass().getClassLoader().getResource("LoincTableCoreTiny.csv").getPath();
    private final List<Loinc2HpoAnnotationCsvEntry> entries = Loinc2HpoAnnotationCsvEntry.importAnnotations(annotationPath);
    private final TermId descreasedRbcs = TermId.of("HP:0020060");
    private final CodeSystemConvertor convertor = new CodeSystemConvertor();
    private final Loinc2Hpo loinc2Hpo = new Loinc2Hpo(annotationPath, convertor);
    private final  Map<LoincId, LoincEntry> loincEntryMap = LoincEntry.getLoincEntryMap(coreTablePath);
    private final FhirObservation2Hpo fhirObservation2Hpo = new FhirObservation2Hpo(loinc2Hpo, loincEntryMap.keySet());

    @Test
    public void testObservationRefersToLOINC789_8() {
        List<Coding> codings = lowHemoglobin.getCode().getCoding();
        assertEquals(1, codings.size());
        Coding coding = codings.get(0);
        assertEquals("http://loinc.org", coding.getSystem());
        assertEquals("789-8", coding.getCode());
        assertEquals("Erythrocytes [#/volume] in Blood by Automated count", coding.getDisplay());
        //Query with loincId and interpretation code to get HPO term

    }

    @Test
    public void testObservationHasLowInterpretation() {
        CodeableConcept intepretation = lowHemoglobin.getInterpretation();
        List<Coding> codings = intepretation.getCoding();
        assertEquals(1, codings.size());
        Coding coding = codings.get(0);
        assertEquals("http://hl7.org/fhir/v2/0078", coding.getSystem());
        assertEquals("L", coding.getCode());
        assertEquals("Low", coding.getDisplay());
    }


    @Test
    public void testDecreasedRedBloodCellCount() throws Exception {
        List<Coding> codings = lowHemoglobin.getCode().getCoding();
        assertEquals(1, codings.size());
        Coding coding = codings.get(0);
        String loincCode = coding.getCode();
        CodeableConcept intepretation = lowHemoglobin.getInterpretation();
        codings = intepretation.getCoding();
        assertEquals(1, codings.size());
        Coding intepretationCoding = codings.get(0);
        String interpretationCode = intepretationCoding.getCode();
        Optional<HpoTerm4TestOutcome> opt  = fhirObservation2Hpo.fhir2hpo(lowHemoglobin);
        assertTrue(opt.isPresent());
        HpoTerm4TestOutcome  result = opt.get();
        TermId tid = result.getId();
        assertEquals(descreasedRbcs, tid);
    }

    @Test
    public void testDecorator() {
        Map<TermId, String> id2labelMap = new HashMap<>();
        for (Term t : hpoTermMap.values()) {
            id2labelMap.put(t.getId(), t.getName());
        }
        FhirObservationDecorator decorator = new FhirObservationDecorator(fhirObservation2Hpo, id2labelMap);
        Optional<Observation> opt  = decorator.hpoObservation(lowHemoglobin);
        assertTrue(opt.isPresent());
        Observation hpoObservation = opt.get();

       /*
       This will not work for Dstu3 -- need to upgrade
       FhirContext ctx = FhirContext.forR4();
        IParser parser = ctx.newJsonParser();
        parser.setPrettyPrint(true);
        String serialized = parser.encodeResourceToString(hpoObservation);
        System.out.println(serialized);
        */

        List<Coding> codings = hpoObservation.getCode().getCoding();
        assertEquals(1, codings.size());
        Coding coding = codings.get(0);
        assertEquals("http://hpo.jax.org", coding.getSystem());
        assertEquals("HP:0020060", coding.getCode());
        assertEquals("Decreased red blood cell count", coding.getDisplay());
        assertEquals("true", hpoObservation.getValue().primitiveValue());
        // retrieve the original Observation
       List<Reference> referenceList =  hpoObservation.getBasedOn();
       assertEquals(1, referenceList.size());
       Reference reference = referenceList.get(0);
       Observation original = (Observation) reference.getResource();
       assertEquals(original, lowHemoglobin);
    }

}
