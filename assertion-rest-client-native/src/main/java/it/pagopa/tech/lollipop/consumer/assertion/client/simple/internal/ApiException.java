/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal;

import java.net.http.HttpHeaders;

@javax.annotation.Generated(
        value = "org.openapitools.codegen.languages.JavaClientCodegen",
        date = "2023-04-04T15:48:28.175942900+02:00[Europe/Paris]")
public class ApiException extends RuntimeException {
    private int code = 0;
    private HttpHeaders responseHeaders = null;
    private String responseBody = null;

    public ApiException() {}

    public ApiException(Throwable throwable) {
        super(throwable);
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(
            String message,
            Throwable throwable,
            int code,
            HttpHeaders responseHeaders,
            String responseBody) {
        super(message, throwable);
        this.code = code;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public ApiException(
            String message, int code, HttpHeaders responseHeaders, String responseBody) {
        this(message, (Throwable) null, code, responseHeaders, responseBody);
    }

    public ApiException(
            String message, Throwable throwable, int code, HttpHeaders responseHeaders) {
        this(message, throwable, code, responseHeaders, null);
    }

    public ApiException(int code, HttpHeaders responseHeaders, String responseBody) {
        this((String) null, (Throwable) null, code, responseHeaders, responseBody);
    }

    public ApiException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ApiException(
            int code, String message, HttpHeaders responseHeaders, String responseBody) {
        this(code, message);
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    /**
     * Get the HTTP status code.
     *
     * @return HTTP status code
     */
    public int getCode() {
        return code;
    }

    /**
     * Get the HTTP response headers.
     *
     * @return Headers as an HttpHeaders object
     */
    public HttpHeaders getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * Get the HTTP response body.
     *
     * @return Response body in the form of string
     */
    public String getResponseBody() {
        return responseBody;
    }
}
