package org.monarchinitiative.loinc2hpogui.io;

import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.dstu3.model.*;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.monarchinitiative.loinc2hpocore.io.WriteToFile;
import org.monarchinitiative.loinc2hpocore.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import javax.swing.JFileChooser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a helper class that downloads observations from hapi-fhir test server for developing concepts.
 * it could be deleted when not in use.
 */

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
            String current = jsonParser.setPrettyPrint(true).encodeResourceToString(observation).trim();
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
                firstComplete = jsonParser.setPrettyPrint(true).encodeResourceToString(observation).trim();
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
                firstAcceptable = jsonParser.setPrettyPrint(true).encodeResourceToString(observation).trim();
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

    public static void iteratorHapiFHIRServer() {

        //printObservationInfo();
        //printPatientInfo();
        //BufferedReader bufferedReader = new BufferedReader(new FileReader(""));
        //find20CompleteRecordOfEachType();
        String path = null;
        HashMap<String, StringBuilder> completeObservations = new HashMap<>();
        completeObservations.put("Qn", new StringBuilder());
        completeObservations.put("Ord", new StringBuilder());
        completeObservations.put("Nom", new StringBuilder());
        completeObservations.put("Nar", new StringBuilder());
        completeObservations.put("OrdQn", new StringBuilder());
        completeObservations.put("unkown", new StringBuilder());
        HashMap<String, Integer> countComplete = new HashMap<>();
        countComplete.put("Qn", 0);
        countComplete.put("Ord", 0);
        countComplete.put("Nom", 0);
        countComplete.put("Nar", 0);
        countComplete.put("OrdQn", 0);
        countComplete.put("unknown", 0);
        HashMap<String, StringBuilder> accetableObservations = new HashMap<>();
        accetableObservations.put("Qn", new StringBuilder());
        accetableObservations.put("Ord", new StringBuilder());
        accetableObservations.put("Nom", new StringBuilder());
        accetableObservations.put("Nar", new StringBuilder());
        accetableObservations.put("OrdQn", new StringBuilder());
        accetableObservations.put("unknown", new StringBuilder());
        HashMap<String, Integer> countAccetable = new HashMap<>();
        countAccetable.put("Qn", 0);
        countAccetable.put("Ord", 0);
        countAccetable.put("Nom", 0);
        countAccetable.put("Nar", 0);
        countAccetable.put("OrdQn", 0);
        countAccetable.put("unknown", 0);

        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            path = chooser.getSelectedFile().getAbsolutePath();
            System.out.println(path);
            Map<LoincId, LoincEntry> loincEntryMap = LoincEntry.getLoincEntryMap(path);
            System.out.println("size of loinc table: " + loincEntryMap.size());
            for (LoincEntry loincEntry : loincEntryMap.values()) {
                if (countComplete.containsKey(loincEntry.getScale()) && countComplete.get(loincEntry.getScale()) < 50) {
                    try {
                        List<Observation> results = ObservationDownloader.retrieveObservation(loincEntry.getLOINC_Number().toString());
                        if (results != null && !results.isEmpty()){
                            for (Observation observation : results) {
                                if (isComplte(observation)) {
                                    String aCompleteRecord = jsonParser.setPrettyPrint(true).encodeResourceToString(observation);
                                    if (completeObservations.containsKey(loincEntry.getScale())) {
                                        completeObservations.get(loincEntry.getScale()).append(aCompleteRecord);
                                        completeObservations.get(loincEntry.getScale()).append("\n\n");
                                        completeObservations.get(loincEntry.getScale()).append(Character.toString((char) 12));
                                        countComplete.put(loincEntry.getScale(), countComplete.get(loincEntry.getScale()) + 1);
                                    } else {
                                        completeObservations.get("unknown").append(aCompleteRecord);
                                        completeObservations.get("unknown").append("\n\n");
                                        completeObservations.get("unknown").append(Character.toString((char) 12));
                                        countComplete.put("unknown", countAccetable.get("unknown") + 1);
                                    }
                                    continue;
                                }
                                if (isAcceptable(observation)) {
                                    String aAcceptableRecord = jsonParser.setPrettyPrint(true).encodeResourceToString(observation);
                                    if (accetableObservations.containsKey(loincEntry.getScale())
                                            && countAccetable.get(loincEntry.getScale()) < 50) {
                                        accetableObservations.get(loincEntry.getScale()).append(aAcceptableRecord);
                                        accetableObservations.get(loincEntry.getScale()).append("\n\n");
                                        accetableObservations.get(loincEntry.getScale()).append(Character.toString((char) 12));
                                        countAccetable.put(loincEntry.getScale(), countAccetable.get(loincEntry.getScale()) + 1);
                                    } else if (countAccetable.get("unknown") < 50){
                                        accetableObservations.get("unknown").append(aAcceptableRecord);
                                        accetableObservations.get("unknown").append("\n\n");
                                        accetableObservations.get("unknown").append(Character.toString((char) 12));
                                        countAccetable.put("unknown", countAccetable.get("unknown") + 1);
                                    }
                                }
                            }
                        }

                    } catch (Exception e) {
                        continue;
                    }
                }


            }
        }
        for (String scale : completeObservations.keySet()) {
            if (!completeObservations.get(scale).toString().isEmpty()) {
                WriteToFile.writeToFile(completeObservations.get(scale).toString(), scale + "_complteObservations.txt");
            } else {
                System.out.println("No complete observations were found for loinc scale type: " + scale);
            }
        }
        for (String scale : accetableObservations.keySet()) {
            if (!accetableObservations.get(scale).toString().isEmpty()) {
                WriteToFile.writeToFile(accetableObservations.get(scale).toString(), scale + "_acceptableObservations.txt");
            } else {
                System.out.println("No acceptable observations were found for loinc scale type: " + scale);
            }
        }


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
