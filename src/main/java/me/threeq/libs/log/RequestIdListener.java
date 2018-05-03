package me.threeq.libs.log;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @author three
 */
@WebListener
public class RequestIdListener implements ServletRequestListener {

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        ServletRequest request = sre.getServletRequest();
        String requestId = UUID.randomUUID().toString();
        if(request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String xRequestId = httpRequest.getHeader(RequestIdUtil.httpHeaderName());
            if(xRequestId != null && !xRequestId.equals("")) {
                requestId = xRequestId;
            }
        }
        request.setAttribute(RequestIdUtil.attributeName(), requestId);
        sre.getServletContext().setAttribute(RequestIdUtil.attributeName(), requestId);

        RequestIdUtil.put(requestId);

    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        RequestIdUtil.delete();
    }
}
