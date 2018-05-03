package me.threeq.libs.log.http;

import me.threeq.libs.log.RequestIdUtil;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class ClientHttpRequestIdInterceptor implements ClientHttpRequestInterceptor {

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        // sets the request id
        final String reqeustId = RequestIdUtil.get();
        if(reqeustId != null) {
            request.getHeaders().add(RequestIdUtil.httpHeaderName(), reqeustId);
        }

        // proceeds with execution
        return execution.execute(request, body);
    }
}
