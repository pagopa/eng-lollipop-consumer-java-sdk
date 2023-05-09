/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.servlet;

import static it.pagopa.tech.lollipop.consumer.command.impl.LollipopConsumerCommandImpl.VERIFICATION_SUCCESS_CODE;
import static org.mockito.Mockito.*;

import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommand;
import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommandBuilder;
import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Enumeration;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpVerifierServletFilterTest {

    private HttpServletRequest requestMock;
    private HttpServletResponse responseMock;
    private FilterChain filterChainMock;
    private LollipopConsumerCommandBuilder commandBuilderMock;
    private LollipopConsumerCommand commandMock;

    private HttpVerifierServletFilter sut;

    @BeforeEach
    void setUp() {
        requestMock = mock(HttpServletRequest.class);
        responseMock = mock(HttpServletResponse.class);
        filterChainMock = mock(FilterChain.class);
        commandBuilderMock = mock(LollipopConsumerCommandBuilder.class);
        commandMock = mock(LollipopConsumerCommand.class);

        sut = new HttpVerifierServletFilter(commandBuilderMock);

        String header1 = "Header1";
        Enumeration<String> headers = Collections.enumeration(Collections.singletonList(header1));
        doReturn(headers).when(requestMock).getHeaderNames();
        doReturn("value").when(requestMock).getHeader(header1);
    }

    @Test
    void testDoFilterSuccess() throws IOException, ServletException {
        CommandResult commandResult =
                new CommandResult(VERIFICATION_SUCCESS_CODE, "request validation success");

        doReturn(commandMock).when(commandBuilderMock).createCommand(any());
        doReturn(commandResult).when(commandMock).doExecute();
        doNothing().when(filterChainMock).doFilter(requestMock, responseMock);

        sut.doFilter(requestMock, responseMock, filterChainMock);

        verify(responseMock, never()).setStatus(401);
        verify(responseMock, never()).getWriter();
        verify(filterChainMock).doFilter(requestMock, responseMock);
    }

    @Test
    void testDoFilterFailForValidationError() throws IOException, ServletException {
        PrintWriter printWriter = mock(PrintWriter.class);
        CommandResult commandResult = new CommandResult("FAIL", "request validation failure");

        doReturn(commandMock).when(commandBuilderMock).createCommand(any());
        doReturn(commandResult).when(commandMock).doExecute();
        doReturn(printWriter).when(responseMock).getWriter();

        sut.doFilter(requestMock, responseMock, filterChainMock);

        verify(responseMock).setStatus(401);
        verify(responseMock).getWriter();
        verify(printWriter).write(commandResult.getResultMessage());
        verify(filterChainMock, never()).doFilter(requestMock, responseMock);
    }

    @Test
    void testDoFilterFailForConversionException() throws IOException, ServletException {
        CommandResult commandResult = new CommandResult("FAIL", "request validation failure");

        doReturn(commandMock).when(commandBuilderMock).createCommand(any());
        doReturn(commandResult).when(commandMock).doExecute();
        doThrow(IOException.class).when(responseMock).getWriter();

        sut.doFilter(requestMock, responseMock, filterChainMock);

        verify(responseMock).setStatus(401);
        verify(responseMock).getWriter();
        verify(filterChainMock, never()).doFilter(requestMock, responseMock);
    }
}
