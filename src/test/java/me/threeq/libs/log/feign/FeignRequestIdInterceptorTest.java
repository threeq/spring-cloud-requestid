package me.threeq.libs.log.feign;

import me.threeq.libs.log.RequestIdUtil;
import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static junit.framework.TestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FeignRequestIdInterceptorTest {
    FeignRequestIdInterceptor instance;
    
    @BeforeEach
    public void setUp() throws Exception {
        instance = new FeignRequestIdInterceptor();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    public void tearDown() throws Exception {

        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void shouldSetHeader() {

        // given
        RequestIdUtil.put("1111");
        final RequestTemplate request = new RequestTemplate();

        // when
        instance.apply(request);

        // then
        assertTrue(request.headers().containsKey(RequestIdUtil.httpHeaderName()));
        assertEquals(1, request.headers().get(RequestIdUtil.httpHeaderName()).size());
        assertEquals("1111", request.headers().get(RequestIdUtil.httpHeaderName()).iterator().next());
        assertEquals("1111", MDC.get(RequestIdUtil.attributeName()));

        RequestIdUtil.delete();
        assertNull(RequestIdUtil.get());
        assertNull(MDC.get(RequestIdUtil.attributeName()));

    }
}
