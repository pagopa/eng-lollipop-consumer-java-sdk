/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.StringJoiner;

/** SamlUserInfo */
@JsonPropertyOrder({SamlUserInfo.JSON_PROPERTY_RESPONSE_XML})
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-04-04T15:48:28.175942900+02:00[Europe/Paris]")
public class SamlUserInfo {
    public static final String JSON_PROPERTY_RESPONSE_XML = "response_xml";
    private String responseXml;

    public SamlUserInfo() {}

    public SamlUserInfo responseXml(String responseXml) {
        this.responseXml = responseXml;
        return this;
    }

    /**
     * A string representation of a signed SPID/CIE response
     *
     * @return responseXml
     */
    @javax.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_RESPONSE_XML)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getResponseXml() {
        return responseXml;
    }

    @JsonProperty(JSON_PROPERTY_RESPONSE_XML)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setResponseXml(String responseXml) {
        this.responseXml = responseXml;
    }

    /** Return true if this SamlUserInfo object is equal to o. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SamlUserInfo samlUserInfo = (SamlUserInfo) o;
        return Objects.equals(this.responseXml, samlUserInfo.responseXml);
    }

    @Override
    public int hashCode() {
        return Objects.hash(responseXml);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SamlUserInfo {\n");
        sb.append("    responseXml: ").append(toIndentedString(responseXml)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first
     * line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
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

        // add `response_xml` to the URL query string
        if (getResponseXml() != null) {
            joiner.add(
                    String.format(
                            "%sresponse_xml%s=%s",
                            prefix,
                            suffix,
                            URLEncoder.encode(
                                            String.valueOf(getResponseXml()),
                                            StandardCharsets.UTF_8)
                                    .replaceAll("\\+", "%20")));
        }

        return joiner.toString();
    }
}
