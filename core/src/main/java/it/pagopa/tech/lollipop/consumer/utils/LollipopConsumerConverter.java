/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.utils;

import static it.pagopa.tech.lollipop.consumer.command.impl.LollipopConsumerCommandImpl.VERIFICATION_SUCCESS_CODE;

import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LollipopConsumerConverter {

    private LollipopConsumerConverter() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Utility method to be used to generate a LollipopConsumerRequest from a HttpServletRequest
     *
     * @param httpServletRequest http request to be converted into a lollipop request
     * @return instance of {@link LollipopConsumerRequest} produced from the httpServletRequest
     * @throws IOException exception return if body extraction fails
     */
    public static LollipopConsumerRequest convertToLollipopRequest(
            HttpServletRequest httpServletRequest) throws IOException {

        byte[] requestBody = null;

        String method = httpServletRequest.getMethod();

        if (method != null && (!method.equals("GET") && !method.equals("DELETE"))) {
            InputStream requestInputStream = httpServletRequest.getInputStream();
            requestBody = requestInputStream.readAllBytes();
        }

        return LollipopConsumerRequest.builder()
                .requestBody(requestBody != null ? new String(requestBody) : null)
                .headerParams(
                        Collections.list(httpServletRequest.getHeaderNames()).stream()
                                .collect(Collectors.toMap(h -> h, httpServletRequest::getHeader)))
                .requestParams(httpServletRequest.getParameterMap())
                .build();
    }

    /**
     * Utility method used to convert the commandResult in a HttpServletResponse
     *
     * @param commandResult results of the LollipopConsumerCommand's doExecute
     * @return instance of HttpServletResponse with the commandResult status code and response
     * @throws IOException when failed to send error
     */
    public static HttpServletResponse interceptResult(
            CommandResult commandResult, HttpServletResponse httpResponse) throws IOException {

        if (!commandResult.getResultCode().equals(VERIFICATION_SUCCESS_CODE)) {
            httpResponse.setStatus(401);
            httpResponse.getWriter().write(commandResult.getResultMessage());
        }

        return httpResponse;
    }
}
