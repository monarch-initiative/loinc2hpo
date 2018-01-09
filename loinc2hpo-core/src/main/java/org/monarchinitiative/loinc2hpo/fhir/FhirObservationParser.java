package org.monarchinitiative.loinc2hpo.fhir;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.phenomics.ontolib.ontology.data.TermId;
import org.monarchinitiative.loinc2hpo.Loinc2Hpo;
import org.monarchinitiative.loinc2hpo.exception.Loinc2HpoException;
import org.monarchinitiative.loinc2hpo.exception.MaformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.exception.WrongElementException;
import org.monarchinitiative.loinc2hpo.loinc.Hpo2LoincTermId;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.LoincObservation;
import org.monarchinitiative.loinc2hpo.loinc.LoincTest;
import org.monarchinitiative.loinc2hpo.testresult.QnTestResult;
import org.monarchinitiative.loinc2hpo.testresult.TestResult;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Map;

public class FhirObservationParser {

    /**
     * <	Off scale low
     * >	Off scale high
     * A	Abnormal
     * AA	Critically abnormal
     * AC	Anti-complementary substances present
     * B	Better
     * D	Significant change down
     * DET	Detected
     * H	High
     * HH	Critically high
     * HM	Hold for Medical Review
     * HU	Very high
     * I	Intermediate
     * IE	Insufficient evidence
     * IND	Indeterminate
     * L	Low
     * LL	Critically low
     * LU	Very low
     * MS	Moderately susceptible. Indicates for microbiology susceptibilities only.
     * N	Normal
     * ND	Not Detected
     * NEG	Negative
     * NR	Non-reactive
     * NS	Non-susceptible
     * null	No range defined, or normal ranges don't apply
     * OBX	Interpretation qualifiers in separate OBX segments
     * POS	Positive
     * QCF	Quality Control Failure
     * R	Resistant
     * RR	Reactive
     * S	Susceptible
     * SDD	Susceptible-dose dependent
     * SYN-R	Synergy - resistant
     * SYN-S	Synergy - susceptible
     * TOX	Cytotoxic substance present
     * U	Significant change up
     * VS	Very susceptible. Indicates for microbiology susceptibilities only.
     * W	Worse
     * WR	Weakly reactive
     **/

    private static final Logger logger = LogManager.getLogger();

    public static TestResult fhir2testrest(JsonNode node, Map<LoincId, LoincTest> testmap) throws Loinc2HpoException {
        boolean interpretationDetected = false;
        String interpretationCode = null;

        LoincId lid;
        LoincObservation observation;
        String comment;


        System.out.println(node.toString());

        String resourcetype = node.get("resourceType").asText();
        //System.out.println("rt: \"" + resourcetype + "\"");
        if (!resourcetype.equals("Observation"))
            throw new WrongElementException("Unexpected resource type " + resourcetype + "(expected: TestResult)");
        System.out.println("rt:" + resourcetype);

        JsonNode codeNode = node.get("code");
        lid = getLoincId(codeNode);
        JsonNode interpretationNode = node.get("interpretation");
        observation = getObservation(interpretationNode);
        if (observation != null && lid != null) {
            LoincTest test = testmap.get(lid);
            if (test==null) {
                logger.error("Could not retrieve test for " + lid.toString());
                debugPrint(testmap);
                return null;
            }
            Hpo2LoincTermId hpoId = test.loincValueToHpo(observation);
            return new QnTestResult(hpoId, observation, "?");
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
//                                return new QnTestResult()
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
        //return TestResult(interpretationCode);
    }



    static void debugPrint(Map<LoincId, LoincTest> testmap) {
        logger.trace("tests in map");
        for (LoincId id : testmap.keySet()) {
            logger.trace(id.toString() + ": "+ testmap.get(id).toString());
        }
    }

    /*
    "interpretation": {
        "coding": [
          {
            "system": "http://hl7.org/fhir/v2/0078",
            "code": "H",
            "display": "High"
          }
        ]
      },
     */
    static LoincObservation getObservation(JsonNode node) throws Loinc2HpoException {
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
                return new LoincObservation(lcode, lcode);
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
/*
 public static Patient parsePatient(byte[] jsonData) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode root = objectMapper.readTree(jsonData);
            return parsePatient(root);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }


    public static Patient parsePatient(JsonNode root) {
        Patient patient = null;

//            JsonNode root = objectMapper.readTree(jsonData);
        String resourcetype = root.get("resourceType").asText();
        System.out.println("rt:" + resourcetype);
        String id = root.get("id").asText();
        patient = new Patient(id);
        JsonNode metaNode = root.path("meta");
        if (metaNode.isMissingNode()) {
            // if "name" node is missing
        } else {
            int count = metaNode.path("count").asInt();
            // missing node, just return empty string
            String lastUpdated = metaNode.path("lastUpdated").asText();
            patient.setCount(count);
            patient.setLastUpdated(lastUpdated);
        }
        JsonNode entryArrayNode = root.path("entry");
        if (!entryArrayNode.isArray()) {
            // If this node an Arrray?
        } else {
            for (JsonNode entryNode : entryArrayNode) {
                TestResult observation = new TestResult();
                String fullurl = entryNode.path("fullurl").asText();
                observation.setFullurl(fullurl);
                String entrtyid = entryNode.path("id").asText();
                observation.setId(entrtyid);

                    "code":{"coding":[{"system":"http://loinc.org","code":"","display":"WholeBody_TotalMass_WBSubtotal"}]}

JsonNode codeNode = entryNode.path("code");
                if (codeNode.isMissingNode()) {
                        System.err.println("[ERROR] missing code node");
                        } else {
                        JsonNode coding = codeNode.path("coding");
                        if (!coding.isArray()) {
                        System.err.println("[ERROR] coding node not array");
                        } else {
                        for (JsonNode loinc : coding) {
                        String system = loinc.path("system").asText();
                        if (!system.equals("http://loinc.org")) {
                        System.err.println("[ERROR] Non Loinc code detected...skipping");
                        continue;
                        }
                        String code = loinc.path("code").asText();
                        //TODO Richard needs to update the JSON
                        observation.setCode(code);
                        String display = loinc.path("display").asText();
                        observation.setLoincName(display);
                        }
                        }
                        }
                        JsonNode subjectNode = entryNode.path("subject");
                        if (subjectNode.isMissingNode()) {
                        System.out.println("[ERROR] could not get subject node");
                        } else {
                        String ref = subjectNode.path("reference").asText();
                        observation.setReference(ref);
                        }
                        JsonNode valuesNode = entryNode.path("values");
                        if (valuesNode.isMissingNode()) {
                        System.out.println("[ERROR] could not get values node");
                        } else {
                        JsonNode valueQuantity = valuesNode.path("valueQuantity");
                        if (!valueQuantity.isArray()) {
                        System.err.println("[ERROR] valueQuantity node not array");
                        } else {
                        for (JsonNode vq : valueQuantity) {
                        String value = vq.path("value").asText();
                        String unit = vq.path("unit").asText();
                        observation.setResultvalue(value);
                        observation.setUnit(unit);
                        }
                        }
                        }

                        patient.addObservation(observation);
                        }
                        }

                        return patient;
                        }


                        */