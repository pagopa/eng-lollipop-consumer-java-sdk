/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.model;

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
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.JSON;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-04-04T15:48:28.175942900+02:00[Europe/Paris]")
@JsonDeserialize(using = AssertionRef.AssertionRefDeserializer.class)
@JsonSerialize(using = AssertionRef.AssertionRefSerializer.class)
public class AssertionRef extends AbstractOpenApiSchema {
    private static final Logger log = Logger.getLogger(AssertionRef.class.getName());

    public static class AssertionRefSerializer extends StdSerializer<AssertionRef> {
        public AssertionRefSerializer(Class<AssertionRef> t) {
            super(t);
        }

        public AssertionRefSerializer() {
            this(null);
        }

        @Override
        public void serialize(AssertionRef value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            jgen.writeObject(value.getActualInstance());
        }
    }

    public static class AssertionRefDeserializer extends StdDeserializer<AssertionRef> {
        public AssertionRefDeserializer() {
            this(AssertionRef.class);
        }

        public AssertionRefDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public AssertionRef deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            JsonNode tree = jp.readValueAsTree();
            Object deserialized = null;
            boolean typeCoercion = ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS);
            int match = 0;
            JsonToken token = tree.traverse(jp.getCodec()).nextToken();
            // deserialize String
            try {
                boolean attemptParsing = true;
                // ensure that we respect type coercion as set on the client ObjectMapper
                if (String.class.equals(Integer.class)
                        || String.class.equals(Long.class)
                        || String.class.equals(Float.class)
                        || String.class.equals(Double.class)
                        || String.class.equals(Boolean.class)
                        || String.class.equals(String.class)) {
                    attemptParsing = typeCoercion;
                    if (!attemptParsing) {
                        attemptParsing |=
                                ((String.class.equals(Integer.class)
                                                || String.class.equals(Long.class))
                                        && token == JsonToken.VALUE_NUMBER_INT);
                        attemptParsing |=
                                ((String.class.equals(Float.class)
                                                || String.class.equals(Double.class))
                                        && token == JsonToken.VALUE_NUMBER_FLOAT);
                        attemptParsing |=
                                (String.class.equals(Boolean.class)
                                        && (token == JsonToken.VALUE_FALSE
                                                || token == JsonToken.VALUE_TRUE));
                        attemptParsing |=
                                (String.class.equals(String.class)
                                        && token == JsonToken.VALUE_STRING);
                    }
                }
                if (attemptParsing) {
                    deserialized = tree.traverse(jp.getCodec()).readValueAs(String.class);
                    // TODO: there is no validation against JSON schema constraints
                    // (min, max, enum, pattern...), this does not perform a strict JSON
                    // validation, which means the 'match' count may be higher than it should be.
                    match++;
                    log.log(Level.FINER, "Input data matches schema 'String'");
                }
            } catch (Exception e) {
                // deserialization failed, continue
                log.log(Level.FINER, "Input data does not match schema 'String'", e);
            }

            if (match == 1) {
                AssertionRef ret = new AssertionRef();
                ret.setActualInstance(deserialized);
                return ret;
            }
            throw new IOException(
                    String.format(
                            "Failed deserialization for AssertionRef: %d classes match result,"
                                    + " expected 1",
                            match));
        }

        /** Handle deserialization of the 'null' value. */
        @Override
        public AssertionRef getNullValue(DeserializationContext ctxt) throws JsonMappingException {
            throw new JsonMappingException(ctxt.getParser(), "AssertionRef cannot be null");
        }
    }

    // store a list of schema names defined in oneOf
    public static final Map<String, Class<?>> schemas = new HashMap<>();

    public AssertionRef() {
        super("oneOf", Boolean.FALSE);
    }

    public AssertionRef(String o) {
        super("oneOf", Boolean.FALSE);
        setActualInstance(o);
    }

    static {
        schemas.put("String", String.class);
        JSON.registerDescendants(AssertionRef.class, Collections.unmodifiableMap(schemas));
    }

    @Override
    public Map<String, Class<?>> getSchemas() {
        return AssertionRef.schemas;
    }

    /**
     * Set the instance that matches the oneOf child schema, check the instance parameter is valid
     * against the oneOf child schemas: String
     *
     * <p>It could be an instance of the 'oneOf' schemas. The oneOf child schemas may themselves be
     * a composed schema (allOf, anyOf, oneOf).
     */
    @Override
    public void setActualInstance(Object instance) {
        if (JSON.isInstanceOf(String.class, instance, new HashSet<Class<?>>())) {
            super.setActualInstance(instance);
            return;
        }

        throw new RuntimeException("Invalid instance type. Must be String");
    }

    /**
     * Get the actual instance, which can be the following: String
     *
     * @return The actual instance (String)
     */
    @Override
    public Object getActualInstance() {
        return super.getActualInstance();
    }

    /**
     * Get the actual instance of `String`. If the actual instance is not `String`, the
     * ClassCastException will be thrown.
     *
     * @return The actual instance of `String`
     * @throws ClassCastException if the instance is not `String`
     */
    public String getString() throws ClassCastException {
        return (String) super.getActualInstance();
    }

    /**
     * Convert the instance into URL query string.
     *
     * @return URL query string
     */
    public String toUrlQueryString() {
        return toUrlQueryString(null);
    }

    /**
     * Convert the instance into URL query string.
     *
     * @param prefix prefix of the query string
     * @return URL query string
     */
    public String toUrlQueryString(String prefix) {
        String suffix = "";
        String containerSuffix = "";
        String containerPrefix = "";
        if (prefix == null) {
            // style=form, explode=true, e.g. /pet?name=cat&type=manx
            prefix = "";
        } else {
            // deepObject style e.g. /pet?id[name]=cat&id[type]=manx
            prefix = prefix + "[";
            suffix = "]";
            containerSuffix = "]";
            containerPrefix = "[";
        }

        StringJoiner joiner = new StringJoiner("&");

        if (getActualInstance() instanceof String) {
            if (getActualInstance() != null) {
                joiner.add(
                        String.format(
                                "%sone_of_0%s=%s",
                                prefix,
                                suffix,
                                URLEncoder.encode(
                                                String.valueOf(getActualInstance()),
                                                StandardCharsets.UTF_8)
                                        .replaceAll("\\+", "%20")));
            }
            return joiner.toString();
        }
        if (getActualInstance() instanceof String) {
            if (getActualInstance() != null) {
                joiner.add(
                        String.format(
                                "%sone_of_1%s=%s",
                                prefix,
                                suffix,
                                URLEncoder.encode(
                                                String.valueOf(getActualInstance()),
                                                StandardCharsets.UTF_8)
                                        .replaceAll("\\+", "%20")));
            }
            return joiner.toString();
        }
        if (getActualInstance() instanceof String) {
            if (getActualInstance() != null) {
                joiner.add(
                        String.format(
                                "%sone_of_2%s=%s",
                                prefix,
                                suffix,
                                URLEncoder.encode(
                                                String.valueOf(getActualInstance()),
                                                StandardCharsets.UTF_8)
                                        .replaceAll("\\+", "%20")));
            }
            return joiner.toString();
        }
        return null;
    }
}
