package org.monarchinitiative.loinc2hpo;

import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

import static org.junit.Assert.*;

public class Loinc2HpoTest {

    private static Loinc2Hpo loinc2Hpo;

    @BeforeClass
    public static void setup(){
        //initialize the Loinc2Hpo app with the annotation file path
        String path = Loinc2HpoTest.class.getClassLoader().getResource("annotations.tsv").getPath();
        loinc2Hpo = new Loinc2Hpo(path);
    }

    @Test
    public void queryWithInterpretationCode() throws Exception {
        //LoincId of current test: LOINC 2823-3 Potassium in Serum or plasma
        LoincId loincId = new LoincId("2823-3");

        //Assume result is lower than normal, then interpretation code is L (low) in FHIR system .
        Code low = new Code().setSystem("FHIR").setCode("L");

        //Query with loincId and interpretation code to get HPO term
        HpoTerm4TestOutcome hpo_coded_phenotype = loinc2Hpo.query(loincId, low);
        //The result should be HP:0002900, not negated
        assertFalse(hpo_coded_phenotype.isNegated());
        assertEquals(hpo_coded_phenotype.getId().getValue(), "HP:0002900");

        //Assume result if normal, then the interpretation code is N (normal) in FHIR system
        Code normal = new Code().setSystem("FHIR").setCode("N");
        hpo_coded_phenotype = loinc2Hpo.query(loincId, normal);
        //The result should be NOT
        assertTrue(hpo_coded_phenotype.isNegated());
        assertEquals(hpo_coded_phenotype.getId().getValue(), "HP:0011042");


        //Try a different lab test: LOINC 234909 Glucose [Presence] in Urine
        loincId = new LoincId("2349-9");

        //Assume result is positive
        Code positive = new Code().setSystem("FHIR").setCode("POS");
        hpo_coded_phenotype = loinc2Hpo.query(loincId, positive);
        //The result should be Glycosuria HP:0003076
        assertFalse(hpo_coded_phenotype.isNegated());
        assertEquals(hpo_coded_phenotype.getId().getValue(), "HP:0003076");

        //Assume result is negative, then the result should be NOT Glycosuria HP:0003076
        Code negative = new Code().setSystem("FHIR").setCode("NEG");
        hpo_coded_phenotype = loinc2Hpo.query(loincId, negative);
        assertTrue(hpo_coded_phenotype.isNegated());
        assertEquals(hpo_coded_phenotype.getId().getValue(), "HP:0003076");


        //Try a final lab test: LOINC 5778-6 Color of Urine
        loincId = new LoincId("5778-6");

        //Assume the result is coded by SNOMED concept Pink Urine: id 449071000124107
        Code snomed_code = new Code().setSystem("snomed-ct").setCode("449071000124107");
        hpo_coded_phenotype = loinc2Hpo.query(loincId, snomed_code);
        assertFalse(hpo_coded_phenotype.isNegated());
        assertEquals(hpo_coded_phenotype.getId().getValue(), "HP:0032001");
    }

    @Test
    public void queryWithStrings() throws Exception {
        //A convenient method is using the system and code id for query, instead of having to instantiate a code object
        LoincId loincId = new LoincId("2823-3");
        String system = "FHIR";
        String codeLow = "L";
        assertEquals(loinc2Hpo.query(loincId, system, codeLow).getId().getValue(), "HP:0002900");
    }

}