package org.monarchinitiative.loinc2hpo.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import com.fasterxml.jackson.databind.JsonNode;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.monarchinitiative.loinc2hpo.exception.AmbiguousSubjectException;
import org.monarchinitiative.loinc2hpo.exception.Loinc2HpoException;
import org.monarchinitiative.loinc2hpo.exception.SubjectNotFoundException;
import org.monarchinitiative.loinc2hpo.exception.WrongElementException;
import org.monarchinitiative.loinc2hpo.loinc.HpoTermId4LoincTest;
import org.monarchinitiative.loinc2hpo.loinc.Loinc2HPOAnnotation;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.ObservationResultInInternalCode;
import org.monarchinitiative.loinc2hpo.testresult.BasicLabTestResultInHPO;
import org.monarchinitiative.loinc2hpo.testresult.LabTestResultInHPO;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FhirResourceRetriever {


    static final String LOINC_SYSTEM = "http://loinc.org";
    //let user set the base url from the GUI
    //By default, we use hapi-fhir test server
    public static String BASEURL = "http://fhirtest.uhn.ca/baseDstu3";
    private static final Logger logger = LogManager.getLogger();
    public static final FhirContext ctx = FhirContext.forDstu3();
    static final IGenericClient client = ctx.newRestfulGenericClient(BASEURL);
    public static final IParser jsonParser = ctx.newJsonParser();


    /**
     * This function parses a json file stored locally to an hapi-fhir Observation object
     * @param filepath
     * @return
     */
    public static Observation parseJsonFile2Observation(String filepath) {
        Observation observation = null;
        try {
            File file = new File(filepath);
            byte[] bytes = new byte[(int)file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytes);
            //logger.debug(new String(bytes));
            observation = (Observation) jsonParser.parseResource(new String(bytes));
            fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (DataFormatException e) {
            logger.error("Json file " + filepath + " is not a valid observation");
        }
        return observation;
    }

    /**
     * @TODO: implement it
     * retrieve a patient's observations from FHIR server
     * @param patient
     * @return
     */
    public static List<Observation> retrieveObservationFromServer(Patient patient) {

        List<Observation> observationList = new ArrayList<>();
        String id = patient.getId();
        if (id != null) {
            Bundle observationBundle = client.search().forResource(Observation.class)
                    .where(new ReferenceClientParam("subject").hasId(id))
                    .prettyPrint()
                    .returnBundle(Bundle.class)
                    .execute();

            while(true) {
                 observationBundle.getEntry()
                        .forEach(p -> observationList.add((Observation) p.getResource()));
                if (observationBundle.getLink(IBaseBundle.LINK_NEXT ) != null) {
                    observationBundle = client.loadPage().next(observationBundle).execute();
                } else {
                    break;
                }
            }
        }
        return observationList;

    }

    /**
     * Retrieve a patient from the reference field of an observation
     * @param subject
     * @return
     */
    public static Patient retrievePatientFromServer(Reference subject) throws SubjectNotFoundException, AmbiguousSubjectException {

        List<Patient> patients = new ArrayList<>();
        if (subject.hasReference()) {
            String ref = subject.getReference();
            if (!ref.startsWith(BASEURL) && ref.startsWith("Patient")) {
                ref = BASEURL + "/" + ref;
            }
            Bundle patientBundle = client.search().byUrl(ref).returnBundle(Bundle.class).execute();

            while (true) {
                 patientBundle.getEntry()
                                .forEach(p -> patients.add((Patient) p.getResource()));
                if (patientBundle.getLink(IBaseBundle.LINK_NEXT) != null){
                    patientBundle = client.loadPage().next(patientBundle).execute();
                } else {
                    break;
                }
            }
        } else if (subject.hasIdentifier()) {
            Identifier identifier = subject.getIdentifier();
            //TODO: find patient through the identifier
        }
        if (patients.size() == 1) {
            return patients.iterator().next();
        } else if (patients.isEmpty()) {
            throw new SubjectNotFoundException("Expect one subject, but found none");
        } else {
            throw new AmbiguousSubjectException("Except one subject, but found multiple");
        }

    }

    public void setBaseURL(String url) {
        BASEURL = url;
    }

    public String getBaseURL(){
        return BASEURL;
    }


    @Deprecated
    public static LabTestResultInHPO fhir2testrest(JsonNode node, Map<LoincId, Loinc2HPOAnnotation> testmap) throws Loinc2HpoException {
        boolean interpretationDetected = false;
        String interpretationCode = null;

        LoincId lid;
        ObservationResultInInternalCode observation;
        String comment;


        System.out.println(node.toString());

        String resourcetype = node.get("resourceType").asText();
        if (!resourcetype.equals("Observation"))
            throw new WrongElementException("Unexpected resource type " + resourcetype + "(expected: LabTestResultInHPO)");


        JsonNode codeNode = node.get("code");
        lid = getLoincId(codeNode);
        JsonNode interpretationNode = node.get("interpretation");
        observation = getInterpretationCode(interpretationNode);
        if (observation != null && lid != null) {
            Loinc2HPOAnnotation test = testmap.get(lid);
            if (test==null) {
                logger.error("Could not retrieve test for " + lid.toString());
                debugPrint(testmap);
                return null;
            }
            HpoTermId4LoincTest hpoId = test.loincInterpretationToHpo(observation);
            return new BasicLabTestResultInHPO(hpoId, observation, "?");
        }

        logger.info("interpretation node: " + interpretationNode.asText());
        if (interpretationNode.isMissingNode()) {
            //
        } else {
            JsonNode codingNode = interpretationNode.get("coding");
            logger.info("coding node: " + codingNode.asText());
            if (codingNode.isMissingNode()) {
                logger.info("coding node is not array");
            } else {
                if (!codingNode.isArray()) {
                    //
                } else {
                    logger.info("coding node is array");
                    for (JsonNode loinc : codingNode) {
                        if (!loinc.path("code").isMissingNode()) {
                            interpretationDetected = true;
                            interpretationCode = loinc.path("code").asText();
                            logger.info("code is detected: " + interpretationCode);
//                            if (interpretationCode.equalsIgnoreCase("H")) {
//                                return new BasicLabTestResultInHPO()
//                            }
                        }
                        /**
                         String system = loinc.path("system").asText();
                         logger.info("system: " + system);
                         if (!system.equals("http://loinc.org")) {
                         System.err.println("[ERROR] Non Loinc code detected...");
                         //continue;
                         }

                         String display = loinc.path("display").asText();
                         logger.info("display: " + display);
                         **/
                    }
                }
            }
        }

        if (!interpretationDetected) {
            double valueObserved = 9999.0;
            JsonNode valueNode = node.get("valueQuantity");
            //System.out.println("found valueNode: " + valueNode.asText());
            try {
                logger.info("value observed: " + valueNode.get("value")
                        .asText());
                valueObserved = Double.parseDouble(valueNode.get("value")
                        .asText());
                logger.info("value retrieved: " + valueObserved);
            } catch (NumberFormatException e) {
                logger.error("measured value is not a double");
            }
            if (Math.abs(valueObserved - 9999.0) > 0.00001) {
                //we want to compare the observed value with the normal range
                if (valueObserved < 0) {
                    interpretationCode = "L";
                }
                if (valueObserved > 1) {
                    interpretationCode = "H";
                } else {
                    interpretationCode = "N";
                }
            }
        }
        return null;
        //return LabTestResultInHPO(interpretationCode);
    }


    @Deprecated
    static void debugPrint(Map<LoincId, Loinc2HPOAnnotation> testmap) {
        logger.trace("tests in map");
        for (LoincId id : testmap.keySet()) {
            logger.trace(id.toString() + ": "+ testmap.get(id).toString());
        }
    }

    /**
    * This function parses an {@code interpretation} stanza of FHIR JSON.
    * <pre>
    *  "interpretation": {
     *"coding": [
     *     {
     *       "system": "http://hl7.org/fhir/v2/0078",
     *       "code": "H",
     *       "display": "High"
     *     }
     *]
      *},
     * </pre>
     * It returns the corresponding {@link ObservationResultInInternalCode} object.
     * TODO -- add some text corresponding to the result.
     */
    @Deprecated
    static ObservationResultInInternalCode getInterpretationCode(JsonNode node) throws Loinc2HpoException {
        JsonNode codingNode = node.get("coding");
        if (codingNode == null) {
            throw new Loinc2HpoException("Could not find coding node in interpretation");
        }
        if (!codingNode.isArray()) {
            throw new Loinc2HpoException("coding node in interpretation was not array");
        }
        for (JsonNode n : codingNode) {
            if (!n.path("code").isMissingNode()) {
                String lcode = n.path("code").asText();
                return new ObservationResultInInternalCode(lcode);
            }
        }
        throw new Loinc2HpoException("could not find interpretation code");
    }


    /*
    "code": {
    "coding": [
      {
        "system": "http://loinc.org",
        "code": "15074-8",
        "display": "Glucose [Moles/volume] in Blood"
      }
    ]
  },
     */
    @Deprecated
    static LoincId getLoincId(JsonNode node) throws Loinc2HpoException {
        JsonNode codingNode = node.get("coding");
        if (codingNode == null) {
            logger.error("Could not retrieve coding");
            throw new Loinc2HpoException("Malformed json, could not retrieve coding element");
        }
        if (!codingNode.isArray()) {
            throw new Loinc2HpoException("coding node was not array as expected");
        }
        for (JsonNode n : codingNode) {
            if (!n.path("code").isMissingNode()) {
                String lcode = n.path("code").asText();
                return new LoincId(lcode);
            }
        }
        throw new Loinc2HpoException("Did not find loinc code element");
    }


}

