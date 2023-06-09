/* (C)2022-2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.StringJoiner;

/** ProblemJson */
@JsonPropertyOrder({
    ProblemJson.JSON_PROPERTY_TYPE,
    ProblemJson.JSON_PROPERTY_TITLE,
    ProblemJson.JSON_PROPERTY_STATUS,
    ProblemJson.JSON_PROPERTY_DETAIL,
    ProblemJson.JSON_PROPERTY_INSTANCE
})
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-04-11T16:21:49.277208500+02:00[Europe/Paris]")
public class ProblemJson {
    public static final String JSON_PROPERTY_TYPE = "type";
    private URI type = URI.create("about:blank");

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_STATUS = "status";
    private Integer status;

    public static final String JSON_PROPERTY_DETAIL = "detail";
    private String detail;

    public static final String JSON_PROPERTY_INSTANCE = "instance";
    private URI instance;

    public ProblemJson() {}

    public ProblemJson type(URI type) {
        this.type = type;
        return this;
    }

    /**
     * An absolute URI that identifies the problem type. When dereferenced, it SHOULD provide
     * human-readable documentation for the problem type (e.g., using HTML).
     *
     * @return type
     */
    @javax.annotation.Nullable @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public URI getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setType(URI type) {
        this.type = type;
    }

    public ProblemJson title(String title) {
        this.title = title;
        return this;
    }

    /**
     * A short, summary of the problem type. Written in english and readable for engineers (usually
     * not suited for non technical stakeholders and not localized); example: Service Unavailable
     *
     * @return title
     */
    @javax.annotation.Nullable @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTitle(String title) {
        this.title = title;
    }

    public ProblemJson status(Integer status) {
        this.status = status;
        return this;
    }

    /**
     * The HTTP status code generated by the origin server for this occurrence of the problem.
     * minimum: 100 maximum: 600
     *
     * @return status
     */
    @javax.annotation.Nullable @JsonProperty(JSON_PROPERTY_STATUS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getStatus() {
        return status;
    }

    @JsonProperty(JSON_PROPERTY_STATUS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setStatus(Integer status) {
        this.status = status;
    }

    public ProblemJson detail(String detail) {
        this.detail = detail;
        return this;
    }

    /**
     * A human readable explanation specific to this occurrence of the problem.
     *
     * @return detail
     */
    @javax.annotation.Nullable @JsonProperty(JSON_PROPERTY_DETAIL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getDetail() {
        return detail;
    }

    @JsonProperty(JSON_PROPERTY_DETAIL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDetail(String detail) {
        this.detail = detail;
    }

    public ProblemJson instance(URI instance) {
        this.instance = instance;
        return this;
    }

    /**
     * An absolute URI that identifies the specific occurrence of the problem. It may or may not
     * yield further information if dereferenced.
     *
     * @return instance
     */
    @javax.annotation.Nullable @JsonProperty(JSON_PROPERTY_INSTANCE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public URI getInstance() {
        return instance;
    }

    @JsonProperty(JSON_PROPERTY_INSTANCE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setInstance(URI instance) {
        this.instance = instance;
    }

    /** Return true if this ProblemJson object is equal to o. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProblemJson problemJson = (ProblemJson) o;
        return Objects.equals(this.type, problemJson.type)
                && Objects.equals(this.title, problemJson.title)
                && Objects.equals(this.status, problemJson.status)
                && Objects.equals(this.detail, problemJson.detail)
                && Objects.equals(this.instance, problemJson.instance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, title, status, detail, instance);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ProblemJson {\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    detail: ").append(toIndentedString(detail)).append("\n");
        sb.append("    instance: ").append(toIndentedString(instance)).append("\n");
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

        // add `type` to the URL query string
        if (getType() != null) {
            joiner.add(
                    String.format(
                            "%stype%s=%s",
                            prefix,
                            suffix,
                            URLEncoder.encode(String.valueOf(getType()), StandardCharsets.UTF_8)
                                    .replaceAll("\\+", "%20")));
        }

        // add `title` to the URL query string
        if (getTitle() != null) {
            joiner.add(
                    String.format(
                            "%stitle%s=%s",
                            prefix,
                            suffix,
                            URLEncoder.encode(String.valueOf(getTitle()), StandardCharsets.UTF_8)
                                    .replaceAll("\\+", "%20")));
        }

        // add `status` to the URL query string
        if (getStatus() != null) {
            joiner.add(
                    String.format(
                            "%sstatus%s=%s",
                            prefix,
                            suffix,
                            URLEncoder.encode(String.valueOf(getStatus()), StandardCharsets.UTF_8)
                                    .replaceAll("\\+", "%20")));
        }

        // add `detail` to the URL query string
        if (getDetail() != null) {
            joiner.add(
                    String.format(
                            "%sdetail%s=%s",
                            prefix,
                            suffix,
                            URLEncoder.encode(String.valueOf(getDetail()), StandardCharsets.UTF_8)
                                    .replaceAll("\\+", "%20")));
        }

        // add `instance` to the URL query string
        if (getInstance() != null) {
            joiner.add(
                    String.format(
                            "%sinstance%s=%s",
                            prefix,
                            suffix,
                            URLEncoder.encode(String.valueOf(getInstance()), StandardCharsets.UTF_8)
                                    .replaceAll("\\+", "%20")));
        }

        return joiner.toString();
    }
}
