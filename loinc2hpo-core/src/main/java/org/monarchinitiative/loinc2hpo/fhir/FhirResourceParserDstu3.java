package org.monarchinitiative.loinc2hpo.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FhirResourceParserDstu3 implements FhirResourceParser{
    private boolean usePrettyPrint = false;
    @Override
    public void setPrettyPrint(boolean choice) {
        this.usePrettyPrint = choice;
    }

    @Override
    public String toJson(IBaseResource resource){
        IParser jsonParser = FhirContext.forDstu3().newJsonParser().setPrettyPrint(this.usePrettyPrint);
        return jsonParser.encodeResourceToString(resource);
    }

    @Override
    public String toXML(IBaseResource resource) {
        IParser xmlParser = FhirContext.forDstu3().newXmlParser();
        return xmlParser.encodeResourceToString(resource);
    }

    @Override
    public IBaseResource parse(File file) throws IOException {
        IParser jsonParser = FhirContext.forDstu3().newJsonParser();
        byte[] bytes = new byte[(int)file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(bytes);
        //logger.debug(new String(bytes));
        IBaseResource ibaseResource = jsonParser.parseResource(new String(bytes));
        fileInputStream.close();

        return ibaseResource;
    }

    @Override
    public IBaseResource parse(String jsonString){
        IParser jsonParser = FhirContext.forDstu3().newJsonParser();
        //logger.debug(new String(bytes));
        IBaseResource ibaseResource = jsonParser.parseResource(jsonString);
        return ibaseResource;
    }

}
