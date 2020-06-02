package org.monarchinitiative.loinc2hpocore.fhir2hpo;

import ca.uhn.fhir.context.FhirContext;


/**
 * Always ask for FhirContxt from this class to avoid multi-creation.
 */
public class FhirContxt {

    static FhirContext ctx_2;
    static FhirContext ctx_2_1;
    static FhirContext ctx_3;
    static FhirContext ctx_4;

    public static FhirContext dstu2() {
        if (ctx_2 == null) {
            ctx_2 = FhirContext.forDstu2();
        }
        return ctx_2;
    }

    public static FhirContext dstu2_1() {
        if (ctx_2_1 == null) {
            ctx_2_1 = FhirContext.forDstu2_1();
        }
        return ctx_2_1;
    }

    public static FhirContext dstu3() {
        if (ctx_3 == null) {
            ctx_3 = FhirContext.forDstu3();
        }
        return ctx_3;
    }

    public static FhirContext dstu4() {
        throw new UnsupportedOperationException("Need to update hapi-fhir version in order to implement this");
    }

}
