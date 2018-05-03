package me.threeq.libs.log.http;


import me.threeq.libs.log.RequestIdUtil;
import junit.framework.TestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClientHttpRequestIdInterceptorTest {
    ClientHttpRequestIdInterceptor instance;
    @BeforeEach
    public void setUp() throws Exception {
        instance = new ClientHttpRequestIdInterceptor();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    public void tearDown() throws Exception {

        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void shouldSetHeader() throws IOException {

        // given
        RequestIdUtil.put("2222");

        final HttpRequest request = mock(HttpRequest.class);
        final ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        final byte[] body = new byte[0];

        when(request.getHeaders()).thenReturn(new HttpHeaders());

        // when
        instance.intercept(request, body, execution);

        // then
        assertTrue(request.getHeaders().containsKey(RequestIdUtil.httpHeaderName()));
        assertEquals("2222", request.getHeaders().getFirst(RequestIdUtil.httpHeaderName()));
        verify(execution).execute(request, body);

        TestCase.assertEquals("2222", MDC.get(RequestIdUtil.attributeName()));

        RequestIdUtil.delete();
        assertNull(RequestIdUtil.get());
        assertNull(MDC.get(RequestIdUtil.attributeName()));
    }

}
