/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.spring.converter;

import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.util.StreamUtils;

/** Class to be used for conversion of the http requests to be validated */
public class LollipopConsumerRequestConverter {

    /**
     * Utility method to be used to generate a LollipopConsumerRequest
     *
     * @param httpServletRequest http request to be converted into a lollipop request
     * @return instance of {@link LollipopConsumerRequest} produced from the httpServletRequest
     * @throws IOException exception return if body extraction fails
     */
    public static LollipopConsumerRequest convert(HttpServletRequest httpServletRequest)
            throws IOException {

        byte[] requestBody = null;

        String method = httpServletRequest.getMethod();

        if (method != null && (!method.equals("GET") && !method.equals("DELETE"))) {
            InputStream requestInputStream = httpServletRequest.getInputStream();
            requestBody = StreamUtils.copyToByteArray(requestInputStream);
        }

        return LollipopConsumerRequest.builder()
                .requestBody(requestBody != null ? new String(requestBody) : null)
                .headerParams(
                        Collections.list(httpServletRequest.getHeaderNames()).stream()
                                .collect(Collectors.toMap(h -> h, httpServletRequest::getHeader)))
                .build();
    }
}
