package org.monarchinitiative.loinc2hpo.io;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.UniversalLoinc2HPOAnnotation;

import java.io.IOException;
import java.io.StringWriter;

public class AnnotationJsonSerializer extends JsonSerializer<UniversalLoinc2HPOAnnotation>{

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void serialize(UniversalLoinc2HPOAnnotation value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        StringWriter writer = new StringWriter();
        //gen.writeValue;


    }
}
