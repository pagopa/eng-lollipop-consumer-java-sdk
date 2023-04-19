/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.spring;

import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommand;
import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommandBuilder;
import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.spring.converter.LollipopConsumerRequestConverter;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Instance of a Spring Http {@link HandlerInterceptor}, to be used for Lollipop Request validations
 */
@AllArgsConstructor
public class HttpVerifierHandlerInterceptor implements HandlerInterceptor {

    private final LollipopConsumerCommandBuilder consumerCommandBuilder;
    private static final Log log = LogFactory.getLog(HttpVerifierHandlerInterceptor.class);

    /**
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute, for type and/or instance evaluation
     * @return boolean to determine if the handle completes successfully
     * @throws IOException throws exception if the conversion of a http request fails
     */
    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {

        LollipopConsumerCommand lollipopConsumerCommand =
                consumerCommandBuilder.createCommand(
                        LollipopConsumerRequestConverter.convert(request));

        try {
            CommandResult commandResult = lollipopConsumerCommand.doExecute();

            if (!commandResult.getResultCode().equals("SUCCESS")) {
                response.sendError(401, commandResult.getResultMessage());
            } else {
                return true;
            }

        } catch (Exception e) {
            log.error("Error verifying request", e);
        }

        return false;
    }
}
