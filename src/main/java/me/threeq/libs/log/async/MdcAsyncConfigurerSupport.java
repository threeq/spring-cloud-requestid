package me.threeq.libs.log.async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author three
 *
 * @Async 注解异步方法日志 requestId 支持
 */
@Slf4j
@Configuration
@EnableAsync
public class MdcAsyncConfigurerSupport extends AsyncConfigurerSupport
{
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.initialize();
        return executor;
    }
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) -> {
            ObjectMapper om = new ObjectMapper();
            String p = "";
            try {
                p = om.writeValueAsString(params);
            } catch (JsonProcessingException ignored) { }

            log.error("async task exception => method: {} params: {}", method.getName(), p);
            log.error(throwable.getMessage(), throwable);
        };
    }
}
