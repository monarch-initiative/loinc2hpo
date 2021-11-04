package fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.dstu3.model.Observation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

/**
 * Import a JSON file with a FHIR interpretation for testing.
 */
public class ObservationImporter {

    private static final FhirContext ctxDstu3 = FhirContext.forDstu3();
    private static final IParser jsonparserDstu3 = ctxDstu3.newJsonParser();

    public static org.hl7.fhir.dstu3.model.Observation importDstu3Observation(String path) throws IOException {
        URL url = ObservationImporter.class.getClassLoader().getResource(path);
        if (url == null) {
            throw new FileNotFoundException("Could not find " + path + " for testing");
        }
        File f = new File(url.getFile());
        if (! f.isFile()) {
            throw new FileNotFoundException("Could not find " + path + " for testing");
        }
        Observation observation =
                (Observation) jsonparserDstu3.parseResource(new FileReader(f));
        return observation;
    }



}
