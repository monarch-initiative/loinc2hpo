package org.monarchinitiative.loinc2hpocore;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Hpo2Outcome;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import static org.junit.jupiter.api.Assertions.*;


public class Loinc2HpoTest {
/*
    private static Loinc2Hpo loinc2Hpo;

    @BeforeAll
    public static void setup(){
        //initialize the Loinc2Hpo app with the annotation file path
        String path = Loinc2HpoTest.class.getClassLoader().getResource("annotations.tsv").getPath();
        loinc2Hpo = new Loinc2Hpo(path);
    }

    @Test
    public void queryWithInterpretationCode() throws Exception {
        //LoincId of current test: LOINC 2823-3 Potassium in Serum or plasma
        LoincId loincId = new LoincId("2823-3");
        //Query with loincId and interpretation code to get HPO term
        Hpo2Outcome hpo_coded_phenotype = loinc2Hpo.query(loincId, ShortCode.L);
        //The result should be HP:0002900, not negated
        assertFalse(hpo_coded_phenotype.isNegated());
        assertEquals(hpo_coded_phenotype.getId().getValue(), "HP:0002900");
        //Assume result if normal, then the interpretation code is N (normal) in FHIR system
        hpo_coded_phenotype = loinc2Hpo.query(loincId, ShortCode.N);
        //The result should be NOT
        assertTrue(hpo_coded_phenotype.isNegated());
        assertEquals(hpo_coded_phenotype.getId().getValue(), "HP:0011042");


        //Try a different lab test: LOINC 234909 Glucose [Presence] in Urine
        loincId = new LoincId("2349-9");

        //Assume result is positive
        hpo_coded_phenotype = loinc2Hpo.query(loincId, ShortCode.PRESENT);
        //The result should be Glycosuria HP:0003076
        assertFalse(hpo_coded_phenotype.isNegated());
        assertEquals(hpo_coded_phenotype.getId().getValue(), "HP:0003076");

        //Assume result is negative, then the result should be NOT Glycosuria HP:0003076
        hpo_coded_phenotype = loinc2Hpo.query(loincId, ShortCode.ABSENT);
        assertTrue(hpo_coded_phenotype.isNegated());
        assertEquals(hpo_coded_phenotype.getId().getValue(), "HP:0003076");


        //Try a final lab test: LOINC 5778-6 Color of Urine
        loincId = new LoincId("5778-6");

        //Assume the result is coded by SNOMED concept Pink Urine: id 449071000124107
        //TODO -- WHAT TO DO ABOUT NOMINALS????
        //OutcomeCodeOLD snomed_code = OutcomeCodeOLD.fromSystemAndCode("snomed-ct", "449071000124107");
        hpo_coded_phenotype = loinc2Hpo.query(loincId, ShortCode.NOM);
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

 */

}