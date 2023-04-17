/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.spring.converter;

import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.util.StreamUtils;

public class LollipopConsumerRequestConverter {

    public static LollipopConsumerRequest convert(HttpServletRequest httpServletRequest)
            throws IOException {

        byte[] requestBody;

        InputStream requestInputStream = httpServletRequest.getInputStream();
        requestBody = StreamUtils.copyToByteArray(requestInputStream);

        return LollipopConsumerRequest.builder()
                .requestBody(new String(requestBody))
                .headerParams(
                        Collections.list(httpServletRequest.getHeaderNames()).stream()
                                .collect(Collectors.toMap(h -> h, httpServletRequest::getHeader)))
                .build();
    }
}
