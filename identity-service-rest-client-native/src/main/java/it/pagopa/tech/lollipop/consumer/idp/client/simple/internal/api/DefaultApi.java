/* (C)2022-2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiClient;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiException;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiResponse;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.model.CertData;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-04-11T16:21:49.277208500+02:00[Europe/Paris]")
public class DefaultApi {
    private final HttpClient memberVarHttpClient;
    private final ObjectMapper memberVarObjectMapper;
    private final XmlMapper memberVarXMLMapper;
    private final String memberVarBaseUri;
    private final Consumer<HttpRequest.Builder> memberVarInterceptor;
    private final Duration memberVarReadTimeout;
    private final Consumer<HttpResponse<InputStream>> memberVarResponseInterceptor;
    private final Consumer<HttpResponse<String>> memberVarAsyncResponseInterceptor;
    private final String memberIdpKeysCieEndpoint;
    private final String memberIdpKeysSpidEndpoint;

    public DefaultApi(ApiClient apiClient) {
        memberVarHttpClient = apiClient.getHttpClient();
        memberVarObjectMapper = apiClient.getObjectMapper();
        memberVarXMLMapper = apiClient.getXmlMapper();
        memberVarBaseUri = apiClient.getBaseUri();
        memberVarInterceptor = apiClient.getRequestInterceptor();
        memberVarReadTimeout = apiClient.getReadTimeout();
        memberVarResponseInterceptor = apiClient.getResponseInterceptor();
        memberVarAsyncResponseInterceptor = apiClient.getAsyncResponseInterceptor();
        memberIdpKeysCieEndpoint = apiClient.getIdpKeysCieEndpoint();
        memberIdpKeysSpidEndpoint = apiClient.getIdpKeysSpidEndpoint();
    }

    protected ApiException getApiException(String operationId, HttpResponse<InputStream> response)
            throws IOException {
        String body = response.body() == null ? null : new String(response.body().readAllBytes());
        String message = formatExceptionMessage(operationId, response.statusCode(), body);
        return new ApiException(response.statusCode(), message, response.headers(), body);
    }

    private String formatExceptionMessage(String operationId, int statusCode, String body) {
        if (body == null || body.isEmpty()) {
            body = "[no body]";
        }
        return operationId + " call failed with: " + statusCode + " - " + body;
    }

    /**
     * @return List&lt;String&gt;
     * @throws ApiException if fails to make API call
     */
    public List<String> idpKeysCieGet() throws ApiException {
        ApiResponse<List<String>> localVarResponse = idpKeysCieGetWithHttpInfo();
        return localVarResponse.getData();
    }

    /**
     * @return ApiResponse&lt;List&lt;String&gt;&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<List<String>> idpKeysCieGetWithHttpInfo() throws ApiException {
        HttpRequest.Builder localVarRequestBuilder = idpKeysCieGetRequestBuilder();
        try {
            HttpResponse<InputStream> localVarResponse =
                    memberVarHttpClient.send(
                            localVarRequestBuilder.build(),
                            HttpResponse.BodyHandlers.ofInputStream());
            if (memberVarResponseInterceptor != null) {
                memberVarResponseInterceptor.accept(localVarResponse);
            }
            try {
                if (localVarResponse.statusCode() / 100 != 2) {
                    throw getApiException("idpKeysCieGet", localVarResponse);
                }
                return new ApiResponse<List<String>>(
                        localVarResponse.statusCode(),
                        localVarResponse.headers().map(),
                        localVarResponse.body() == null
                                ? null
                                : memberVarObjectMapper.readValue(
                                        localVarResponse.body(),
                                        new TypeReference<
                                                List<String>>() {}) // closes the InputStream
                        );
            } finally {
            }
        } catch (IOException e) {
            throw new ApiException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(e);
        }
    }

    private HttpRequest.Builder idpKeysCieGetRequestBuilder() throws ApiException {

        HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

        localVarRequestBuilder.uri(URI.create(memberVarBaseUri + memberIdpKeysCieEndpoint));

        localVarRequestBuilder.header("Accept", "application/json");

        localVarRequestBuilder.method("GET", HttpRequest.BodyPublishers.noBody());
        if (memberVarReadTimeout != null) {
            localVarRequestBuilder.timeout(memberVarReadTimeout);
        }
        if (memberVarInterceptor != null) {
            memberVarInterceptor.accept(localVarRequestBuilder);
        }
        return localVarRequestBuilder;
    }

    /**
     * @param tag (required)
     * @return CertData
     * @throws ApiException if fails to make API call
     */
    public CertData idpKeysCieTagGet(String tag) throws ApiException {
        ApiResponse<CertData> localVarResponse = idpKeysCieTagGetWithHttpInfo(tag);
        return localVarResponse.getData();
    }

    /**
     * @param tag (required)
     * @return ApiResponse&lt;CertData&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<CertData> idpKeysCieTagGetWithHttpInfo(String tag) throws ApiException {
        HttpRequest.Builder localVarRequestBuilder = idpKeysCieTagGetRequestBuilder(tag);
        try {
            HttpResponse<InputStream> localVarResponse =
                    memberVarHttpClient.send(
                            localVarRequestBuilder.build(),
                            HttpResponse.BodyHandlers.ofInputStream());
            if (memberVarResponseInterceptor != null) {
                memberVarResponseInterceptor.accept(localVarResponse);
            }
            try {
                if (localVarResponse.statusCode() / 100 != 2) {
                    throw getApiException("idpKeysCieTagGet", localVarResponse);
                }
                return new ApiResponse<CertData>(
                        localVarResponse.statusCode(),
                        localVarResponse.headers().map(),
                        localVarResponse.body() == null
                                ? null
                                : memberVarXMLMapper.readValue(
                                        localVarResponse.body(),
                                        new TypeReference<CertData>() {}) // closes the InputStream
                        );
            } finally {
            }
        } catch (IOException e) {
            throw new ApiException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(e);
        }
    }

    private HttpRequest.Builder idpKeysCieTagGetRequestBuilder(String tag) throws ApiException {
        // verify the required parameter 'tag' is set
        if (tag == null) {
            throw new ApiException(
                    400, "Missing the required parameter 'tag' when calling idpKeysCieTagGet");
        }

        HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

        String localVarPath =
                memberIdpKeysCieEndpoint
                        + "/{tag}".replace("{tag}", ApiClient.urlEncode(tag.toString()));

        localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

        localVarRequestBuilder.header("Accept", "application/xml, application/json");

        localVarRequestBuilder.method("GET", HttpRequest.BodyPublishers.noBody());
        if (memberVarReadTimeout != null) {
            localVarRequestBuilder.timeout(memberVarReadTimeout);
        }
        if (memberVarInterceptor != null) {
            memberVarInterceptor.accept(localVarRequestBuilder);
        }
        return localVarRequestBuilder;
    }

    /**
     * @return List&lt;String&gt;
     * @throws ApiException if fails to make API call
     */
    public List<String> idpKeysSpidGet() throws ApiException {
        ApiResponse<List<String>> localVarResponse = idpKeysSpidGetWithHttpInfo();
        return localVarResponse.getData();
    }

    /**
     * @return ApiResponse&lt;List&lt;String&gt;&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<List<String>> idpKeysSpidGetWithHttpInfo() throws ApiException {
        HttpRequest.Builder localVarRequestBuilder = idpKeysSpidGetRequestBuilder();
        try {
            HttpResponse<InputStream> localVarResponse =
                    memberVarHttpClient.send(
                            localVarRequestBuilder.build(),
                            HttpResponse.BodyHandlers.ofInputStream());
            if (memberVarResponseInterceptor != null) {
                memberVarResponseInterceptor.accept(localVarResponse);
            }
            try {
                if (localVarResponse.statusCode() / 100 != 2) {
                    throw getApiException("idpKeysSpidGet", localVarResponse);
                }
                return new ApiResponse<List<String>>(
                        localVarResponse.statusCode(),
                        localVarResponse.headers().map(),
                        localVarResponse.body() == null
                                ? null
                                : memberVarObjectMapper.readValue(
                                        localVarResponse.body(),
                                        new TypeReference<
                                                List<String>>() {}) // closes the InputStream
                        );
            } finally {
            }
        } catch (IOException e) {
            throw new ApiException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(e);
        }
    }

    private HttpRequest.Builder idpKeysSpidGetRequestBuilder() throws ApiException {

        HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

        localVarRequestBuilder.uri(URI.create(memberVarBaseUri + memberIdpKeysSpidEndpoint));

        localVarRequestBuilder.header("Accept", "application/json");

        localVarRequestBuilder.method("GET", HttpRequest.BodyPublishers.noBody());
        if (memberVarReadTimeout != null) {
            localVarRequestBuilder.timeout(memberVarReadTimeout);
        }
        if (memberVarInterceptor != null) {
            memberVarInterceptor.accept(localVarRequestBuilder);
        }
        return localVarRequestBuilder;
    }

    /**
     * @param tag (required)
     * @return CertData
     * @throws ApiException if fails to make API call
     */
    public CertData idpKeysSpidTagGet(String tag) throws ApiException {
        ApiResponse<CertData> localVarResponse = idpKeysSpidTagGetWithHttpInfo(tag);
        return localVarResponse.getData();
    }

    /**
     * @param tag (required)
     * @return ApiResponse&lt;CertData&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<CertData> idpKeysSpidTagGetWithHttpInfo(String tag) throws ApiException {
        HttpRequest.Builder localVarRequestBuilder = idpKeysSpidTagGetRequestBuilder(tag);
        try {
            HttpResponse<InputStream> localVarResponse =
                    memberVarHttpClient.send(
                            localVarRequestBuilder.build(),
                            HttpResponse.BodyHandlers.ofInputStream());
            if (memberVarResponseInterceptor != null) {
                memberVarResponseInterceptor.accept(localVarResponse);
            }
            try {
                if (localVarResponse.statusCode() / 100 != 2) {
                    throw getApiException("idpKeysSpidTagGet", localVarResponse);
                }
                return new ApiResponse<CertData>(
                        localVarResponse.statusCode(),
                        localVarResponse.headers().map(),
                        localVarResponse.body() == null
                                ? null
                                : memberVarXMLMapper.readValue(
                                        localVarResponse.body(),
                                        new TypeReference<CertData>() {}) // closes the InputStream
                        );
            } finally {
            }
        } catch (IOException e) {
            throw new ApiException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(e);
        }
    }

    private HttpRequest.Builder idpKeysSpidTagGetRequestBuilder(String tag) throws ApiException {
        // verify the required parameter 'tag' is set
        if (tag == null) {
            throw new ApiException(
                    400, "Missing the required parameter 'tag' when calling idpKeysSpidTagGet");
        }

        HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

        String localVarPath =
                memberIdpKeysSpidEndpoint
                        + "/{tag}".replace("{tag}", ApiClient.urlEncode(tag.toString()));

        localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

        localVarRequestBuilder.header("Accept", "application/xml, application/json");

        localVarRequestBuilder.method("GET", HttpRequest.BodyPublishers.noBody());
        if (memberVarReadTimeout != null) {
            localVarRequestBuilder.timeout(memberVarReadTimeout);
        }
        if (memberVarInterceptor != null) {
            memberVarInterceptor.accept(localVarRequestBuilder);
        }
        return localVarRequestBuilder;
    }
}
