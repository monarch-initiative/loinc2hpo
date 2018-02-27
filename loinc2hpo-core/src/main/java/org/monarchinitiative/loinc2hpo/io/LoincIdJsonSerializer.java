package org.monarchinitiative.loinc2hpo.io;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

import java.io.IOException;
import java.io.StringWriter;

public class LoincIdJsonSerializer extends JsonSerializer<LoincId> {

    private ObjectMapper mapper = new ObjectMapper();
    @Override
    public void serialize(LoincId value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, value);
        gen.writeFieldName(writer.toString());


    }
}
