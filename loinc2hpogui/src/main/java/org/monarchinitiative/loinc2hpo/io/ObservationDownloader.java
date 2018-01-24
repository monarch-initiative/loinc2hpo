package org.monarchinitiative.loinc2hpo.fhir;

import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.dstu3.model.*;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ObservationDownloader {

    static final String HAPI_FHIR_TEST_SERVER = "http://fhirtest.uhn.ca/baseDstu3";
    static final String LOINC_SYSTEM = "http://loinc.org";
    static final FhirContext ctx = FhirContext.forDstu3();
    static final IGenericClient client = ctx.newRestfulGenericClient(HAPI_FHIR_TEST_SERVER);
    static final IParser jsonParser = ctx.newJsonParser();

    static List<Observation> retrieveObservation(String loincCode) {
        List<Observation> results = new ArrayList<>();
        Bundle bundle = client.search().forResource(Observation.class)
                .where(new TokenClientParam("code").exactly().systemAndCode(LOINC_SYSTEM, loincCode))
                .prettyPrint()
                .returnBundle(Bundle.class)
                .execute();
        while (true) {
            for (Bundle.BundleEntryComponent bundleEntryComponent : bundle.getEntry() ){
                Observation observation = (Observation) bundleEntryComponent.getResource();
                results.add(observation);
            }
            if (bundle.getLink(IBaseBundle.LINK_NEXT) != null){
                bundle = client.loadPage().next(bundle).execute();
            } else {
                break;
            }
        }
        return results;
    }

    //select the longest observation that has a measured value in a list of observations
    //make sure it has value / codeable value
    //could return null if none of the observations has a measured value
    static String longestObservation(List<Observation> observations) {
        if (observations.isEmpty()) {
            throw new IllegalArgumentException("Empty list error!");
        }
        String longest = null;
        int size = 0;
        for (Observation observation : observations) {
            String current = jsonParser.encodeResourceToString(observation).trim();
            //System.out.println("size of current observation: " + current.length());
            if ((observation.hasValueQuantity() || observation.hasValueCodeableConcept()) &&
                    current.length() > size) {
                System.out.printf("Found a larger observation\ncurrent observation size : %d\n already found size: %d\n",
                        current.length(), size);
                size = current.length();
                longest = current;

            }
        }
        return longest;
    }

    //select the first complete observation that has a measured value, interpretation, and reference
    static String firstCompleteObservation(List<Observation> observations) {
        if (observations == null) {
            throw new IllegalArgumentException();
        }
        String firstComplete = null;
        for (Observation observation : observations) {
            if (isComplte(observation)) {
                firstComplete = jsonParser.encodeResourceToString(observation).trim();
            }
        }
        return firstComplete;
    }

    //select the first observation that has a measured value && (interpretation || reference range)
    static String firstAcceptableObservation(List<Observation> observations) {
        if (observations == null) {
            throw new IllegalArgumentException();
        }
        String firstAcceptable = null;
        for (Observation observation : observations) {
            if (isAcceptable(observation)) {
                firstAcceptable = jsonParser.encodeResourceToString(observation).trim();
            }
        }
        return firstAcceptable;
    }

    static boolean isComplte(Observation observation){

        return (observation.hasValueQuantity() || observation.hasValueCodeableConcept())
                && observation.hasInterpretation() && observation.hasReferenceRange();
    }

    static boolean isAcceptable(Observation observation){
        return (observation.hasValueQuantity() || observation.hasValueCodeableConcept())
                && (observation.hasInterpretation() || observation.hasReferenceRange());
    }

    static void find20CompleteRecordOfEachType(){
        int i = 0;
        Bundle bundle = client.search().forResource(Observation.class)
                //.where(new TokenClientParam("code").exactly().systemAndCode(LOINC_SYSTEM, loincCode))
                .prettyPrint()
                .returnBundle(Bundle.class)
                .execute();
        while (true) {
            for (Bundle.BundleEntryComponent bundleEntryComponent : bundle.getEntry() ){
                Observation observation = (Observation) bundleEntryComponent.getResource();
                if (isComplte(observation)){
                    String completeRecord = jsonParser.encodeResourceToString(observation);
                    i++;
                    System.out.println(i + ". Find a complete record: \n" + completeRecord );
                };
            }
            if (bundle.getLink(IBaseBundle.LINK_NEXT) != null){
                bundle = client.loadPage().next(bundle).execute();
            } else {
                break;
            }
        }

    }

    static List<Patient> retrievePatient(String given, String family) {
        List<Patient> patients = new ArrayList<>();
        Bundle bundle = client.search().forResource(Patient.class)
                .where(new StringClientParam("given").matches().value(given))
                .where(new StringClientParam("family").matches().value(family))
                .prettyPrint()
                .returnBundle(Bundle.class)
                .execute();

        while (true) {
            for (Bundle.BundleEntryComponent bundleEntryComponent : bundle.getEntry() ){
                Patient patient = (Patient) bundleEntryComponent.getResource();
                patients.add(patient);
            }
            if (bundle.getLink(IBaseBundle.LINK_NEXT) != null){
                bundle = client.loadPage().next(bundle).execute();
            } else {
                break;
            }
        }
        return patients;
    }

    public static void main(String[] args) {

        //printObservationInfo();
        //printPatientInfo();
        //BufferedReader bufferedReader = new BufferedReader(new FileReader(""));
        //find20CompleteRecordOfEachType();

    }

    /**
    static void printPatientInfo(){

        Bundle bundle = retrievePatient("John", "Smith");
        System.out.println("Number of patient retrieved " + bundle.getEntry().size() );
        while (true) {
            //System.out.println("has next page at: " + bundle.getLink(IBaseBundle.LINK_NEXT).getUrl());
            for (Bundle.BundleEntryComponent bundleEntryComponent : bundle.getEntry()) {
                IParser jsonParser = ctx.newJsonParser();
                Patient patient = (Patient) bundleEntryComponent.getResource();
                System.out.println(jsonParser.encodeResourceToString(patient));
            }

            if (bundle.getLink(IBaseBundle.LINK_NEXT) == null) {
                break;
            } else {
                bundle = client.loadPage().next(bundle).execute();
            }
        }
    }

    static void printObservationInfo(){
        String Loinc_test_code = "1558-6";

        List<Observation> observations = retrieveObservation(Loinc_test_code);
        System.out.println(observations.size());
        int i = 1;
        for (Observation observation : observations) {
            System.out.printf("\nRecord %d\n", i++);
            IParser jsonParser = ctx.newJsonParser();
            String observationString = jsonParser.encodeResourceToString(observation);
            System.out.println(observationString);
            System.out.println("Resource type: " + observation.getResourceType());
            if (observation.getValue() != null) {
                try {
                    Quantity valueQuantity = observation.getValueQuantity();
                    System.out.println(valueQuantity.getValue() + " " + valueQuantity.getUnit());

                    //the following two lines can handle valueCodableConcept
                    //String system = observation.getValueCodeableConcept().getCoding().get(0).getSystem();
                    //String code = observation.getValueCodeableConcept().getCoding().get(0).getCode();
                } catch (Exception e) {
                    System.out.println("Cannot find valueQuantity");
                }
            }

            List<Observation.ObservationReferenceRangeComponent> ranges = observation.getReferenceRange();
            if (ranges != null) {
                System.out.printf("Found %d ranges: \n", ranges.size());
                for (Observation.ObservationReferenceRangeComponent range : ranges) {
                    if (range.hasLow()) {
                        System.out.println("Low: :" + range.getLow().getValue() + " " + range.getLow().getUnit());
                    }
                    if (range.hasHigh()) {
                        System.out.println("High :" + range.getHigh().getValue() + " " + range.getHigh().getUnit());
                    }
                }
            } else {
                System.out.println("Could not find ranges");
            }
        }

        Loinc_test_code = "600-7";
        observations = retrieveObservation(Loinc_test_code);
        System.out.println(observations.size());
        i = 1;
        for (Observation observation : observations){
            System.out.println("\nRecord " + (i++) + ":");
            IParser jsonParser = ctx.newJsonParser();
            String observationString = jsonParser.encodeResourceToString(observation);
            System.out.println(observationString);
            Reference subject = observation.getSubject();
            if (subject != null) {
                System.out.println("Patient:");
                if (subject.getReference() != null) {
                    System.out.println(subject.getReference());
                }
                if (subject.getDisplay() != null) {
                    System.out.println(subject.getDisplay());
                }
            }
            try {
                CodeableConcept codeableConcept = observation.getValueCodeableConcept();
                List<Coding> codings = codeableConcept.getCoding();
                for (Coding coding : codings) {
                    System.out.println(coding.getSystem());
                    System.out.println(coding.getCode());
                }
            } catch (Exception e) {
                System.out.println("There is no codeableConcept");
            }
            try {
                CodeableConcept interpretation = observation.getInterpretation();
                System.out.println("Result:");
                List<Coding> codings = interpretation.getCoding();
                for (Coding coding : codings) {
                    System.out.println(coding.getSystem());
                    System.out.println(coding.getCode());
                    if (coding.hasExtension()) {
                        System.out.println("Find extension");
                        System.out.println(coding.getExtension().size());
                        for (Extension extension : coding.getExtension()) {
                            System.out.println(extension.getUrl());
                            System.out.println(extension.getValue());
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("There is no interpretation");
            }
        }
    }


**/

/**
        if (resultBundle.getLink(Bundle.LINK_NEXT) != null) {

            // load next page
            Bundle nextPage = client.loadPage().next(resultBundle).execute();
        }
 **/

        /**
        //ClientConfig clientConfig = new ClientConfig();
        JerseyClient client = JerseyClientBuilder.createClient();
        WebTarget webTarget = client.target(HAPI_FHIR_TEST_SERVER);
        WebTarget resourceWebTarget = webTarget.path("observation");
        WebTarget loinc15586 = resourceWebTarget.queryParam("code", LOINC_SYSTEM + "|"+ Loinc_test_code);

         String fullURL= "http://fhirtest.uhn.ca/baseDstu3/Observation?code=http://loinc.org&_pretty=true";
         String responseEntity = JerseyClientBuilder.newClient()
         //.target(fullURL)
         .target(HAPI_FHIR_TEST_SERVER)
         .path("observation")
         .queryParam("code", Loinc_test_code)
         .request().get(String.class);
         System.out.println(responseEntity);
         **/


        /**
        int pageNumber = 1;
        System.out.printf("Fetching result for page %d: \n", pageNumber);
        String HAPI_FHIR_TEST_SERVER = "http://fhirtest.uhn.ca/baseDstu3";
        String LOINC_SYSTEM = "http://loinc.org";
        String Loinc_test_code = "1558-6";

        //http://fhirtest.uhn.ca/baseDstu3/Observation?code=http://loinc.org|1558-6&_pretty=true

        RestAssured.baseURI = HAPI_FHIR_TEST_SERVER;
        Response response = given()
                .request().with()
                .param("_pretty", true)
                .expect()
                .statusCode(200)
                .get(HAPI_FHIR_TEST_SERVER + "/Observation?code=" + LOINC_SYSTEM + "|" + Loinc_test_code);

        String output = response.getBody().asString();

        System.out.println(output);
        //Configuration config = Configuration.builder().mappingProvider(new JacksonMappingProvider()).build();
        //BioPortalSearchResult[] results = JsonPath.using(config).parse(output).read("$.collection", BioPortalSearchResult[].class);
**/



}
