package org.monarchinitiative.loinc2hpo.fhir;

import com.fasterxml.jackson.databind.JsonNode;
import org.monarchinitiative.loinc2hpo.exception.Loinc2HpoException;
import org.monarchinitiative.loinc2hpo.exception.WrongElementException;
import org.monarchinitiative.loinc2hpo.testresult.Observation;

public class FhirObservationParser {



    public static Observation Fhri2Pojo(JsonNode node) throws Loinc2HpoException {
        System.out.println(node.toString());

        String resourcetype = node.get("resourceType").asText();
        System.out.println("rt: \"" + resourcetype + "\"");
        if (! resourcetype.equals("Observation"))
            throw new WrongElementException("Unexpected resource type " + resourcetype + "(expected: Observation)");
        System.out.println("rt:" + resourcetype);
       return null;
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
                Observation observation = new Observation();
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