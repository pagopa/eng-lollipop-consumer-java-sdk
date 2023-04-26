/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.utils;

import static it.pagopa.tech.lollipop.consumer.command.impl.LollipopConsumerCommandImpl.VERIFICATION_SUCCESS_CODE;

import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class LollipopConsumerConverterTest {

    String REQUEST_BODY_STRING = "{\"message\":\"a valid message payload\"}";
    String COMMAND_RESPONSE_SUCCESS = "SAML assertion validated successfully";
    String COMMAND_RESPONSE_FAILED = "Validation of SAML assertion failed, authentication failed";
    byte[] REQUEST_BODY = REQUEST_BODY_STRING.getBytes();

    static Enumeration<String> REQUEST_HEADERS;

    static MockHttpServletRequest mockRequest;

    @BeforeAll
    static void setUp() {
        mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("content-digest", "sha-256=:test:");
        mockRequest.setParameter("testParam", "value1", "value2");
    }

    @Test
    void convertGetHttpRequest() throws IOException {
        mockRequest.setMethod("GET");

        LollipopConsumerRequest request =
                LollipopConsumerConverter.convertToLollipopRequest(mockRequest);
        Assertions.assertNotNull(request.getHeaderParams());
        Assertions.assertNotNull(request.getRequestParams());
    }

    @Test
    void convertPostHttpRequest() throws IOException {
        mockRequest.setMethod("POST");
        mockRequest.setContent(REQUEST_BODY);

        LollipopConsumerRequest request =
                LollipopConsumerConverter.convertToLollipopRequest(mockRequest);
        Assertions.assertNotNull(request.getRequestBody());
        Assertions.assertNotNull(request.getHeaderParams());
        Assertions.assertNotNull(request.getRequestParams());
    }

    @Test
    void convertSuccessResponse() throws IOException {
        CommandResult result =
                new CommandResult(VERIFICATION_SUCCESS_CODE, COMMAND_RESPONSE_SUCCESS);
        int MOCK_RESPONSE_STATUS = 200;
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        mockResponse.setStatus(MOCK_RESPONSE_STATUS);

        HttpServletResponse response =
                LollipopConsumerConverter.interceptResult(result, mockResponse);

        Assertions.assertEquals(MOCK_RESPONSE_STATUS, response.getStatus());
    }

    @Test
    void convertUnauthorizedResponse() throws IOException {
        CommandResult result = new CommandResult("FAILED", COMMAND_RESPONSE_FAILED);
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        HttpServletResponse response =
                LollipopConsumerConverter.interceptResult(result, mockResponse);

        Assertions.assertEquals(401, response.getStatus());
        Assertions.assertSame(
                COMMAND_RESPONSE_FAILED, ((MockHttpServletResponse) response).getErrorMessage());
    }
}
