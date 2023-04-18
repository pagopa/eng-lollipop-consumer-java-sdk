/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.spring;

import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommand;
import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommandBuilder;
import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.spring.converter.LollipopConsumerRequestConverter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerInterceptor;

@AllArgsConstructor
public class HttpVerifierHandlerInterceptor implements HandlerInterceptor {

    private final LollipopConsumerCommandBuilder consumerCommandBuilder;
    private static final Log log = LogFactory.getLog(HttpVerifierHandlerInterceptor.class);

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

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
