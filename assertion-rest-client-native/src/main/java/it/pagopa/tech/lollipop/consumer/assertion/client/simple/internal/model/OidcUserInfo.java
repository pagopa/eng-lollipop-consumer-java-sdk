/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.StringJoiner;

/** OidcUserInfo */
@JsonPropertyOrder({OidcUserInfo.JSON_PROPERTY_ID_TOKEN, OidcUserInfo.JSON_PROPERTY_CLAIMS_TOKEN})
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-04-04T15:48:28.175942900+02:00[Europe/Paris]")
public class OidcUserInfo {
    public static final String JSON_PROPERTY_ID_TOKEN = "id_token";
    private String idToken;

    public static final String JSON_PROPERTY_CLAIMS_TOKEN = "claims_token";
    private String claimsToken;

    public OidcUserInfo() {}

    public OidcUserInfo idToken(String idToken) {
        this.idToken = idToken;
        return this;
    }

    /**
     * A JWT representation of a signed SPID/CIE OIDC Idp
     *
     * @return idToken
     */
    @javax.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_ID_TOKEN)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getIdToken() {
        return idToken;
    }

    @JsonProperty(JSON_PROPERTY_ID_TOKEN)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public OidcUserInfo claimsToken(String claimsToken) {
        this.claimsToken = claimsToken;
        return this;
    }

    /**
     * A JWT representation of a signed SPID/CIE OIDC Idp
     *
     * @return claimsToken
     */
    @javax.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_CLAIMS_TOKEN)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getClaimsToken() {
        return claimsToken;
    }

    @JsonProperty(JSON_PROPERTY_CLAIMS_TOKEN)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setClaimsToken(String claimsToken) {
        this.claimsToken = claimsToken;
    }

    /** Return true if this OidcUserInfo object is equal to o. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OidcUserInfo oidcUserInfo = (OidcUserInfo) o;
        return Objects.equals(this.idToken, oidcUserInfo.idToken)
                && Objects.equals(this.claimsToken, oidcUserInfo.claimsToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idToken, claimsToken);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OidcUserInfo {\n");
        sb.append("    idToken: ").append(toIndentedString(idToken)).append("\n");
        sb.append("    claimsToken: ").append(toIndentedString(claimsToken)).append("\n");
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

        // add `id_token` to the URL query string
        if (getIdToken() != null) {
            joiner.add(
                    String.format(
                            "%sid_token%s=%s",
                            prefix,
                            suffix,
                            URLEncoder.encode(String.valueOf(getIdToken()), StandardCharsets.UTF_8)
                                    .replaceAll("\\+", "%20")));
        }

        // add `claims_token` to the URL query string
        if (getClaimsToken() != null) {
            joiner.add(
                    String.format(
                            "%sclaims_token%s=%s",
                            prefix,
                            suffix,
                            URLEncoder.encode(
                                            String.valueOf(getClaimsToken()),
                                            StandardCharsets.UTF_8)
                                    .replaceAll("\\+", "%20")));
        }

        return joiner.toString();
    }
}
