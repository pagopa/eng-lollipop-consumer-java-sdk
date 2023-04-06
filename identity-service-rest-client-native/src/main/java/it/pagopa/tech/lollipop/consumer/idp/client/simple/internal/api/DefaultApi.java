/* (C)2022-2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiClient;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiException;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;

@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-04-06T16:49:13.589743300+02:00[Europe/Paris]")
public class DefaultApi {
    private final HttpClient memberVarHttpClient;
    private final ObjectMapper memberVarObjectMapper;
    private final String memberVarBaseUri;
    private final Consumer<HttpRequest.Builder> memberVarInterceptor;
    private final Duration memberVarReadTimeout;
    private final Consumer<HttpResponse<InputStream>> memberVarResponseInterceptor;
    private final Consumer<HttpResponse<String>> memberVarAsyncResponseInterceptor;

    public DefaultApi() {
        this(new ApiClient());
    }

    public DefaultApi(ApiClient apiClient) {
        memberVarHttpClient = apiClient.getHttpClient();
        memberVarObjectMapper = apiClient.getObjectMapper();
        memberVarBaseUri = apiClient.getBaseUri();
        memberVarInterceptor = apiClient.getRequestInterceptor();
        memberVarReadTimeout = apiClient.getReadTimeout();
        memberVarResponseInterceptor = apiClient.getResponseInterceptor();
        memberVarAsyncResponseInterceptor = apiClient.getAsyncResponseInterceptor();
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
     * @throws ApiException if fails to make API call
     */
    public void idpKeysCieGet() throws ApiException {
        idpKeysCieGetWithHttpInfo();
    }

    /**
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<Void> idpKeysCieGetWithHttpInfo() throws ApiException {
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
                return new ApiResponse<Void>(
                        localVarResponse.statusCode(), localVarResponse.headers().map(), null);
            } finally {
                // Drain the InputStream
                while (localVarResponse.body().read() != -1) {
                    // Ignore
                }
                localVarResponse.body().close();
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

        String localVarPath = "/idp-keys/cie";

        localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

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
     * @throws ApiException if fails to make API call
     */
    public void idpKeysCieTagGet(String tag) throws ApiException {
        idpKeysCieTagGetWithHttpInfo(tag);
    }

    /**
     * @param tag (required)
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<Void> idpKeysCieTagGetWithHttpInfo(String tag) throws ApiException {
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
                return new ApiResponse<Void>(
                        localVarResponse.statusCode(), localVarResponse.headers().map(), null);
            } finally {
                // Drain the InputStream
                while (localVarResponse.body().read() != -1) {
                    // Ignore
                }
                localVarResponse.body().close();
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
                "/idp-keys/cie/{tag}".replace("{tag}", ApiClient.urlEncode(tag.toString()));

        localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

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
     * @throws ApiException if fails to make API call
     */
    public void idpKeysSpidGet() throws ApiException {
        idpKeysSpidGetWithHttpInfo();
    }

    /**
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<Void> idpKeysSpidGetWithHttpInfo() throws ApiException {
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
                return new ApiResponse<Void>(
                        localVarResponse.statusCode(), localVarResponse.headers().map(), null);
            } finally {
                // Drain the InputStream
                while (localVarResponse.body().read() != -1) {
                    // Ignore
                }
                localVarResponse.body().close();
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

        String localVarPath = "/idp-keys/spid";

        localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

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
     * @throws ApiException if fails to make API call
     */
    public void idpKeysSpidTagGet(String tag) throws ApiException {
        idpKeysSpidTagGetWithHttpInfo(tag);
    }

    /**
     * @param tag (required)
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<Void> idpKeysSpidTagGetWithHttpInfo(String tag) throws ApiException {
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
                return new ApiResponse<Void>(
                        localVarResponse.statusCode(), localVarResponse.headers().map(), null);
            } finally {
                // Drain the InputStream
                while (localVarResponse.body().read() != -1) {
                    // Ignore
                }
                localVarResponse.body().close();
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
                "/idp-keys/spid/{tag}".replace("{tag}", ApiClient.urlEncode(tag.toString()));

        localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

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
}
