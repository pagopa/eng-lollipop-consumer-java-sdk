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
@JsonDeserialize(using = LCUserInfo.LCUserInfoDeserializer.class)
@JsonSerialize(using = LCUserInfo.LCUserInfoSerializer.class)
public class LCUserInfo extends AbstractOpenApiSchema {
    private static final Logger log = Logger.getLogger(LCUserInfo.class.getName());

    public static class LCUserInfoSerializer extends StdSerializer<LCUserInfo> {
        public LCUserInfoSerializer(Class<LCUserInfo> t) {
            super(t);
        }

        public LCUserInfoSerializer() {
            this(null);
        }

        @Override
        public void serialize(LCUserInfo value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            jgen.writeObject(value.getActualInstance());
        }
    }

    public static class LCUserInfoDeserializer extends StdDeserializer<LCUserInfo> {
        public LCUserInfoDeserializer() {
            this(LCUserInfo.class);
        }

        public LCUserInfoDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public LCUserInfo deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            JsonNode tree = jp.readValueAsTree();
            Object deserialized = null;
            boolean typeCoercion = ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS);
            int match = 0;
            JsonToken token = tree.traverse(jp.getCodec()).nextToken();
            // deserialize OidcUserInfo
            try {
                deserialized = tree.traverse(jp.getCodec()).readValueAs(OidcUserInfo.class);
                // TODO: there is no validation against JSON schema constraints
                // (min, max, enum, pattern...), this does not perform a strict JSON
                // validation, which means the 'match' count may be higher than it should be.
                if (((OidcUserInfo) deserialized).getIdToken() != null) {
                    match++;

                    log.log(Level.FINER, "Input data matches schema 'OidcUserInfo'");
                }
            } catch (Exception e) {
                // deserialization failed, continue
                log.log(Level.FINER, "Input data does not match schema 'OidcUserInfo'", e);
            }

            // deserialize SamlUserInfo
            try {
                deserialized = tree.traverse(jp.getCodec()).readValueAs(SamlUserInfo.class);
                // TODO: there is no validation against JSON schema constraints
                // (min, max, enum, pattern...), this does not perform a strict JSON
                // validation, which means the 'match' count may be higher than it should be.
                if (((SamlUserInfo) deserialized).getResponseXml() != null) {
                    match++;
                    log.log(Level.FINER, "Input data matches schema 'SamlUserInfo'");
                }
            } catch (Exception e) {
                // deserialization failed, continue
                log.log(Level.FINER, "Input data does not match schema 'SamlUserInfo'", e);
            }

            if (match == 1) {
                LCUserInfo ret = new LCUserInfo();
                ret.setActualInstance(deserialized);
                return ret;
            }
            throw new IOException(
                    String.format(
                            "Failed deserialization for LCUserInfo: %d classes match result,"
                                    + " expected 1",
                            match));
        }

        /** Handle deserialization of the 'null' value. */
        @Override
        public LCUserInfo getNullValue(DeserializationContext ctxt) throws JsonMappingException {
            throw new JsonMappingException(ctxt.getParser(), "LCUserInfo cannot be null");
        }
    }

    // store a list of schema names defined in oneOf
    public static final Map<String, Class<?>> schemas = new HashMap<>();

    public LCUserInfo() {
        super("oneOf", Boolean.FALSE);
    }

    public LCUserInfo(OidcUserInfo o) {
        super("oneOf", Boolean.FALSE);
        setActualInstance(o);
    }

    public LCUserInfo(SamlUserInfo o) {
        super("oneOf", Boolean.FALSE);
        setActualInstance(o);
    }

    static {
        schemas.put("OidcUserInfo", OidcUserInfo.class);
        schemas.put("SamlUserInfo", SamlUserInfo.class);
        JSON.registerDescendants(LCUserInfo.class, Collections.unmodifiableMap(schemas));
    }

    @Override
    public Map<String, Class<?>> getSchemas() {
        return LCUserInfo.schemas;
    }

    /**
     * Set the instance that matches the oneOf child schema, check the instance parameter is valid
     * against the oneOf child schemas: OidcUserInfo, SamlUserInfo
     *
     * <p>It could be an instance of the 'oneOf' schemas. The oneOf child schemas may themselves be
     * a composed schema (allOf, anyOf, oneOf).
     */
    @Override
    public void setActualInstance(Object instance) {
        if (JSON.isInstanceOf(OidcUserInfo.class, instance, new HashSet<Class<?>>())) {
            super.setActualInstance(instance);
            return;
        }

        if (JSON.isInstanceOf(SamlUserInfo.class, instance, new HashSet<Class<?>>())) {
            super.setActualInstance(instance);
            return;
        }

        throw new RuntimeException("Invalid instance type. Must be OidcUserInfo, SamlUserInfo");
    }

    /**
     * Get the actual instance, which can be the following: OidcUserInfo, SamlUserInfo
     *
     * @return The actual instance (OidcUserInfo, SamlUserInfo)
     */
    @Override
    public Object getActualInstance() {
        return super.getActualInstance();
    }

    /**
     * Get the actual instance of `OidcUserInfo`. If the actual instance is not `OidcUserInfo`, the
     * ClassCastException will be thrown.
     *
     * @return The actual instance of `OidcUserInfo`
     * @throws ClassCastException if the instance is not `OidcUserInfo`
     */
    public OidcUserInfo getOidcUserInfo() throws ClassCastException {
        return (OidcUserInfo) super.getActualInstance();
    }

    /**
     * Get the actual instance of `SamlUserInfo`. If the actual instance is not `SamlUserInfo`, the
     * ClassCastException will be thrown.
     *
     * @return The actual instance of `SamlUserInfo`
     * @throws ClassCastException if the instance is not `SamlUserInfo`
     */
    public SamlUserInfo getSamlUserInfo() throws ClassCastException {
        return (SamlUserInfo) super.getActualInstance();
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

        if (getActualInstance() instanceof SamlUserInfo) {
            if (getActualInstance() != null) {
                joiner.add(
                        ((SamlUserInfo) getActualInstance())
                                .toUrlQueryString(prefix + "one_of_0" + suffix));
            }
            return joiner.toString();
        }
        if (getActualInstance() instanceof OidcUserInfo) {
            if (getActualInstance() != null) {
                joiner.add(
                        ((OidcUserInfo) getActualInstance())
                                .toUrlQueryString(prefix + "one_of_1" + suffix));
            }
            return joiner.toString();
        }
        return null;
    }
}
