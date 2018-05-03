package me.threeq.libs.log.async;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MdcAsyncConfigurerSupportTest {

    @Test
    public void testGetAsyncExecutor() throws ExecutionException, InterruptedException {
        // given
        List<String> expected = new ArrayList<>();
        MdcAsyncConfigurerSupport mdcAsyncConfigurerSupport = new MdcAsyncConfigurerSupport();
        MDC.put("test1", "test1");
        ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor)mdcAsyncConfigurerSupport.getAsyncExecutor();

        // when
        Assertions.assertNotNull(executor);
        Future<?> result = executor.submit(() -> {
            expected.add(MDC.get("test1"));
        });
        result.get();

        // then
        Assertions.assertEquals(1, expected.size());
        Assertions.assertEquals(expected.get(0), "test1");
    }

    @Test
    public void testNormalExecutor() throws ExecutionException, InterruptedException {

        // given
        MDC.put("test1", "test1");
        List<String> expected = new ArrayList<>();

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.initialize();

        // when
        Assertions.assertNotNull(executor);
        Future<?> result = executor.submit(() -> {
            expected.add(MDC.get("test1"));
        });
        result.get();

        // then
        Assertions.assertEquals(1, expected.size());
        Assertions.assertNull(expected.get(0));
    }

    @Test
    void getAsyncUncaughtExceptionHandler() {
        MdcAsyncConfigurerSupport mdcAsyncConfigurerSupport = new MdcAsyncConfigurerSupport();
        AsyncUncaughtExceptionHandler errorHandler = mdcAsyncConfigurerSupport.getAsyncUncaughtExceptionHandler();
        Method testMethod = MdcAsyncConfigurerSupport.class.getMethods()[0];
        try {
            errorHandler.handleUncaughtException(new Exception("test"), testMethod, "test1", "test2");
        } catch (Exception e) {
            // this is test fail
            Assertions.assertNull(e);
        }
        Assertions.assertTrue(true, "test success");
    }
}
