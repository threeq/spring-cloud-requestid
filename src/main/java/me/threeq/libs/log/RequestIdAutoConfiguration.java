package me.threeq.libs.log;

import me.threeq.libs.log.feign.FeignRequestIdInterceptor;
import me.threeq.libs.log.http.ClientHttpRequestIdInterceptor;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.InterceptingHttpAccessor;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Log4j2
@Import(RequestIdListener.class)
public class RequestIdAutoConfiguration {

    @Autowired(required = false)
    private HystrixConcurrencyStrategy hystrixConcurrencyStrategy;

    @Bean
    @ConditionalOnClass(name = "com.netflix.hystrix.Hystrix")
    public HystrixConcurrencyStrategy ignore() {
        HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins
                .getInstance().getCommandExecutionHook();
        HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance()
                .getEventNotifier();
        HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance()
                .getMetricsPublisher();
        HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance()
                .getPropertiesStrategy();

        this.logCurrentStateOfHystrixPlugins(eventNotifier, metricsPublisher,
                propertiesStrategy);

        HystrixPlugins.reset();

        HystrixPlugins.getInstance().registerConcurrencyStrategy(
                new RequestIdConcurrencyStrategy(hystrixConcurrencyStrategy));
        HystrixPlugins.getInstance()
                .registerCommandExecutionHook(commandExecutionHook);
        HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
        HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
        HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);

        return HystrixPlugins.getInstance().getConcurrencyStrategy();
    }

    private void logCurrentStateOfHystrixPlugins(HystrixEventNotifier eventNotifier,
                                                 HystrixMetricsPublisher metricsPublisher,
                                                 HystrixPropertiesStrategy propertiesStrategy) {
        if (log.isDebugEnabled()) {
            log.debug("Current Hystrix plugins configuration is ["
                    + "concurrencyStrategy [" + this.hystrixConcurrencyStrategy + "]," + "eventNotifier ["
                    + eventNotifier + "]," + "metricPublisher [" + metricsPublisher + "],"
                    + "propertiesStrategy [" + propertiesStrategy + "]," + "]");
            log.debug("Registering Sleuth Hystrix Concurrency Strategy.");
        }
    }

    @Autowired(required = false)
    private List<InterceptingHttpAccessor> clients = new ArrayList<>();

    @Bean
    @ConditionalOnClass(InterceptingHttpAccessor.class)
    public InitializingBean clientsHttpRequestIdInitializer() {

        return () -> {
            if(clients != null) {
                for(InterceptingHttpAccessor client : clients) {
                    final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(client.getInterceptors());
                    interceptors.add(new ClientHttpRequestIdInterceptor());
                    client.setInterceptors(interceptors);
                }
            }
        };
    }

    @Bean
    @ConditionalOnClass(name = "feign.Feign")
    public feign.RequestInterceptor feignRequestIdInterceptor() {
        return new FeignRequestIdInterceptor();
    }
}
