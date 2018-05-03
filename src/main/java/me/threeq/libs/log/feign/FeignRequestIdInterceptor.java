package me.threeq.libs.log.feign;

import me.threeq.libs.log.RequestIdUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class FeignRequestIdInterceptor implements RequestInterceptor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(RequestTemplate template) {

        final String requestId = RequestIdUtil.get();
        if ( requestId != null ) {
            template.header(RequestIdUtil.httpHeaderName(), requestId);
        }
    }
}
