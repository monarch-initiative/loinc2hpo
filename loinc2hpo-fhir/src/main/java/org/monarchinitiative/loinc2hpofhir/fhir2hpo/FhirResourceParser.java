package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.hl7.fhir.instance.model.api.IBaseResource;

import java.io.File;
import java.io.IOException;

public interface FhirResourceParser {

    void setPrettyPrint(boolean choice);

    /**
     * Encode a resource to json string
     * @param resource
     * @return
     */
    String toJson(IBaseResource resource);

    /**
     * Encode a resource to xml string
     * @param resource
     * @return
     */
    String toXML(IBaseResource resource);

    /**
     * Parse a file to resource. Cast to resource types
     * @param file
     * @return
     */
    IBaseResource parse(File file) throws IOException;

    /**
     * Parse a string to resource. Cast to resource types
     * @param jsonString
     * @return
     */
    IBaseResource parse(String jsonString);

}
