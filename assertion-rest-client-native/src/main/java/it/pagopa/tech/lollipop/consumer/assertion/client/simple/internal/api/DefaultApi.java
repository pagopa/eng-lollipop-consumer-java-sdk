/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.ApiClient;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.ApiException;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.ApiResponse;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.model.AssertionRef;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.model.LCUserInfo;
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
        date = "2023-04-04T15:48:28.175942900+02:00[Europe/Paris]")
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
     * Get Assertion related to a given assertion ref
     *
     * @param assertionRef (required)
     * @param xPagopaLollipopAuth (required)
     * @return LCUserInfo
     * @throws ApiException if fails to make API call
     */
    public LCUserInfo getAssertion(AssertionRef assertionRef, String xPagopaLollipopAuth)
            throws ApiException {
        ApiResponse<LCUserInfo> localVarResponse =
                getAssertionWithHttpInfo(assertionRef, xPagopaLollipopAuth);
        return localVarResponse.getData();
    }

    /**
     * Get Assertion related to a given assertion ref
     *
     * @param assertionRef (required)
     * @param xPagopaLollipopAuth (required)
     * @return ApiResponse&lt;LCUserInfo&gt;
     * @throws ApiException if fails to make API call
     */
    public ApiResponse<LCUserInfo> getAssertionWithHttpInfo(
            AssertionRef assertionRef, String xPagopaLollipopAuth) throws ApiException {
        HttpRequest.Builder localVarRequestBuilder =
                getAssertionRequestBuilder(assertionRef, xPagopaLollipopAuth);
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
                    throw getApiException("getAssertion", localVarResponse);
                }
                return new ApiResponse<LCUserInfo>(
                        localVarResponse.statusCode(),
                        localVarResponse.headers().map(),
                        localVarResponse.body() == null
                                ? null
                                : memberVarObjectMapper.readValue(
                                        localVarResponse.body(),
                                        new TypeReference<
                                                LCUserInfo>() {}) // closes the InputStream
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

    private HttpRequest.Builder getAssertionRequestBuilder(
            AssertionRef assertionRef, String xPagopaLollipopAuth) throws ApiException {
        // verify the required parameter 'assertionRef' is set
        if (assertionRef == null) {
            throw new ApiException(
                    400, "Missing the required parameter 'assertionRef' when calling getAssertion");
        }
        // verify the required parameter 'xPagopaLollipopAuth' is set
        if (xPagopaLollipopAuth == null) {
            throw new ApiException(
                    400,
                    "Missing the required parameter 'xPagopaLollipopAuth' when calling"
                            + " getAssertion");
        }

        HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

        String localVarPath =
                "/assertions/{assertion_ref}"
                        .replace(
                                "{assertion_ref}",
                                ApiClient.urlEncode(assertionRef.getActualInstance().toString()));

        localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

        if (xPagopaLollipopAuth != null) {
            localVarRequestBuilder.header("x-pagopa-lollipop-auth", xPagopaLollipopAuth.toString());
        }
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
