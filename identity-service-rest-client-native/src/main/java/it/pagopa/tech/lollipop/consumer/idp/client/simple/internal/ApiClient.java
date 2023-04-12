/* (C)2022-2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple.internal;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClientConfig;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;
import lombok.Getter;
import org.openapitools.jackson.nullable.JsonNullableModule;

/**
 * Configuration and utility class for API clients.
 *
 * <p>This class can be constructed and modified, then used to instantiate the various API classes.
 * The API classes use the settings in this class to configure themselves, but otherwise do not
 * store a link to this class.
 *
 * <p>This class is mutable and not synchronized, so it is not thread-safe. The API classes
 * generated from this are immutable and thread-safe.
 *
 * <p>The setter methods of this class return the current object to facilitate a fluent style of
 * configuration.
 */
@Getter
@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-04-11T16:21:49.277208500+02:00[Europe/Paris]")
public class ApiClient {

    private HttpClient.Builder builder;
    private ObjectMapper mapper;
    private XmlMapper xmlMapper;
    private String scheme;
    private String host;
    private int port;
    private String basePath;
    private Consumer<HttpRequest.Builder> interceptor;
    private Consumer<HttpResponse<InputStream>> responseInterceptor;
    private Consumer<HttpResponse<String>> asyncResponseInterceptor;
    private Duration readTimeout;
    private Duration connectTimeout;
    private String idpKeysCieEndpoint;
    private String idpKeysSpidEndpoint;

    /**
     * URL encode a string in the UTF-8 encoding.
     *
     * @param s String to encode.
     * @return URL-encoded representation of the input string.
     */
    public static String urlEncode(String s) {
        return URLEncoder.encode(s, UTF_8).replaceAll("\\+", "%20");
    }

    /** Create an instance of ApiClient. */
    public ApiClient(IdpCertSimpleClientConfig config) {
        this.builder = createDefaultHttpClientBuilder();
        this.mapper = createDefaultObjectMapper();
        this.xmlMapper = createDefaultXmlMapper();
        updateBaseUri(config.getBaseUri());
        interceptor = null;
        readTimeout = null;
        connectTimeout = null;
        responseInterceptor = null;
        asyncResponseInterceptor = null;
        this.idpKeysCieEndpoint = config.getIdpKeysCieEndpoint();
        this.idpKeysSpidEndpoint = config.getIdpKeysSpidEndpoint();
    }

    protected ObjectMapper createDefaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new JsonNullableModule());
        return mapper;
    }

    protected XmlMapper createDefaultXmlMapper() {
        XmlMapper mapper = new XmlMapper();
        return mapper;
    }

    protected HttpClient.Builder createDefaultHttpClientBuilder() {
        return HttpClient.newBuilder();
    }

    public void updateBaseUri(String baseUri) {
        URI uri = URI.create(baseUri);
        scheme = uri.getScheme();
        host = uri.getHost();
        port = uri.getPort();
        basePath = uri.getRawPath();
    }

    /**
     * Get an {@link HttpClient} based on the current {@link HttpClient.Builder}.
     *
     * <p>The returned object is immutable and thread-safe.
     *
     * @return The HTTP client.
     */
    public HttpClient getHttpClient() {
        return builder.build();
    }

    /**
     * Get a copy of the current {@link ObjectMapper}.
     *
     * @return A copy of the current object mapper.
     */
    public ObjectMapper getObjectMapper() {
        return mapper.copy();
    }

    public ApiClient setXmlMapper(XmlMapper mapper) {
        this.xmlMapper = mapper;
        return this;
    }

    /**
     * Get the base URI to resolve the endpoint paths against.
     *
     * @return The complete base URI that the rest of the API parameters are resolved against.
     */
    public String getBaseUri() {
        return scheme + "://" + host + (port == -1 ? "" : ":" + port) + basePath;
    }

    /**
     * Get the custom interceptor.
     *
     * @return The custom interceptor that was set, or null if there isn't any.
     */
    public Consumer<HttpRequest.Builder> getRequestInterceptor() {
        return interceptor;
    }

    /**
     * Get the custom response interceptor.
     *
     * @return The custom interceptor that was set, or null if there isn't any.
     */
    public Consumer<HttpResponse<InputStream>> getResponseInterceptor() {
        return responseInterceptor;
    }

    /**
     * Get the custom async response interceptor. Use this interceptor when asyncNative is set to
     * 'true'.
     *
     * @return The custom interceptor that was set, or null if there isn't any.
     */
    public Consumer<HttpResponse<String>> getAsyncResponseInterceptor() {
        return asyncResponseInterceptor;
    }

    /**
     * Get the read timeout that was set.
     *
     * @return The read timeout, or null if no timeout was set. Null represents an infinite wait
     *     time.
     */
    public Duration getReadTimeout() {
        return readTimeout;
    }
}
