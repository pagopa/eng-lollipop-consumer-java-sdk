/* (C)2022-2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;

@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-04-11T16:21:49.277208500+02:00[Europe/Paris]")
@JsonDeserialize(using = CertData.CertDataDeserializer.class)
@JsonSerialize(using = CertData.CertDataSerializer.class)
@Getter
@Setter
public class CertData extends AbstractOpenApiSchema {
    private static final Logger log = Logger.getLogger(CertData.class.getName());

    public static class CertDataSerializer extends StdSerializer<CertData> {
        public CertDataSerializer(Class<CertData> t) {
            super(t);
        }

        public CertDataSerializer() {
            this(null);
        }

        @Override
        public void serialize(CertData value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            jgen.writeObject(value.getActualInstance());
        }
    }

    public static class CertDataDeserializer extends StdDeserializer<CertData> {
        public CertDataDeserializer() {
            this(CertData.class);
        }

        public CertDataDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public CertData deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            JsonNode tree = jp.readValueAsTree();
            Object deserialized = null;
            boolean typeCoercion = ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS);
            int match = 0;
            JsonToken token = tree.traverse(jp.getCodec()).nextToken();
            EntitiesDescriptor entitiesDescriptor = new EntitiesDescriptor();

            try {

                deserialized = tree.traverse(jp.getCodec()).readValueAs(EntityDescriptor.class);

                if (((EntityDescriptor) deserialized).getEntityID() != null) {
                    List<EntityDescriptor> entityList =
                            Arrays.asList(((EntityDescriptor) deserialized));
                    entitiesDescriptor.setEntityList(entityList);
                    log.log(Level.FINER, "Input data matches schema 'EntityDescriptor'");

                    match++;
                } else {
                    try {
                        deserialized =
                                tree.traverse(jp.getCodec()).readValueAs(EntitiesDescriptor.class);

                        if (((EntitiesDescriptor) deserialized).getEntityList() != null) {
                            entitiesDescriptor.setEntityList(
                                    ((EntitiesDescriptor) deserialized).getEntityList());
                            log.log(Level.FINER, "Input data matches schema 'Entities Descriptor'");

                            match++;
                        }
                    } catch (Exception e) {
                        // deserialization failed, continue
                        log.log(
                                Level.FINER,
                                "Input data does not match schema 'Entities Descriptor'",
                                e);
                    }
                }
            } catch (Exception e) {
                // deserialization failed, continue
                log.log(Level.FINER, "Input data does not match schema 'EntityDescriptor'", e);
            }

            if (match == 1) {
                CertData ret = new CertData();
                ret.setActualInstance(entitiesDescriptor);
                return ret;
            }
            throw new IOException(
                    String.format(
                            "Failed deserialization for CertData: %d classes match result, expected"
                                    + " 1",
                            match));
        }

        /** Handle deserialization of the 'null' value. */
        @Override
        public CertData getNullValue(DeserializationContext ctxt) throws JsonMappingException {
            throw new JsonMappingException(ctxt.getParser(), "CertData cannot be null");
        }
    }

    // store a list of schema names defined in oneOf
    public static final Map<String, Class<?>> schemas = new HashMap<>();

    public CertData() {
        super("oneOf", Boolean.FALSE);
    }

    @Override
    public Map<String, Class<?>> getSchemas() {
        return CertData.schemas;
    }
}
