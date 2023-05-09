/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.servlet;

import static it.pagopa.tech.lollipop.consumer.command.impl.LollipopConsumerCommandImpl.VERIFICATION_SUCCESS_CODE;

import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommand;
import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommandBuilder;
import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.utils.LollipopConsumerConverter;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Instance of Servlet filter, to be used for Lollipop Request validations */
@Slf4j
@AllArgsConstructor
public class HttpVerifierServletFilter implements Filter {

    private final LollipopConsumerCommandBuilder consumerCommandBuilder;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.error("Servlet filter initialized");
    }

    /**
     * @param servletRequest current request
     * @param servletResponse current request
     * @param filterChain the filter chain
     * @throws IOException throws exception if the conversion of a http request fails
     * @throws ServletException throws in case of request validation failure
     */
    @Override
    public void doFilter(
            ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        LollipopConsumerCommand lollipopConsumerCommand =
                consumerCommandBuilder.createCommand(
                        LollipopConsumerConverter.convertToLollipopRequest(
                                (HttpServletRequest) servletRequest));

        try {
            CommandResult commandResult = lollipopConsumerCommand.doExecute();
            LollipopConsumerConverter.interceptResult(
                    commandResult, (HttpServletResponse) servletResponse);

            if (commandResult.getResultCode().equals(VERIFICATION_SUCCESS_CODE)) {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } catch (Exception e) {
            log.error("Error verifying request", e);
        }
    }

    @Override
    public void destroy() {
        log.error("Servlet filter destroyed");
    }
}
