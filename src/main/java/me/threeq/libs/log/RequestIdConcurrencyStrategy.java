package me.threeq.libs.log;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Log4j2
public class RequestIdConcurrencyStrategy extends HystrixConcurrencyStrategy {
    private HystrixConcurrencyStrategy delegate;
    public RequestIdConcurrencyStrategy(HystrixConcurrencyStrategy strategy) {
        this.delegate = strategy;
    }

    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
                                            HystrixProperty<Integer> corePoolSize,
                                            HystrixProperty<Integer> maximumPoolSize,
                                            HystrixProperty<Integer> keepAliveTime, TimeUnit unit,
                                            BlockingQueue<Runnable> workQueue) {
        return this.delegate.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize,
                keepAliveTime, unit, workQueue);
    }
    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
                                            HystrixThreadPoolProperties threadPoolProperties) {
        return this.delegate.getThreadPool(threadPoolKey, threadPoolProperties);
    }
    @Override
    public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
        return this.delegate.getBlockingQueue(maxQueueSize);
    }
    @Override
    public <T> HystrixRequestVariable<T> getRequestVariable(
            HystrixRequestVariableLifecycle<T> rv) {
        return this.delegate.getRequestVariable(rv);
    }

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {

        if (callable instanceof RequestIdCallable) {
            return callable;
        }

        Callable<T> wrappedCallable = this.delegate != null
                ? this.delegate.wrapCallable(callable) : callable;

        if (wrappedCallable instanceof RequestIdCallable) {
            return wrappedCallable;
        }

        return new RequestIdCallable<>(callable, MDC.get("RequestId"));
    }

    /**
     * 包装传递
     * @param <T>
     */
    static class RequestIdCallable<T> implements Callable<T> {
        private final Callable<T> target;
        private final String requestId;
        public RequestIdCallable(Callable<T> target, String requestId) {
            this.target = target;
            this.requestId = requestId;
        }
        @Override
        public T call() throws Exception {
            try {
                RequestIdUtil.put(requestId);
                return target.call();
            }
            finally {
                RequestIdUtil.delete();
            }
        }
    }
}
