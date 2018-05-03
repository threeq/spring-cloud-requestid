package me.threeq.libs.log;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@SpringBootApplication
public class TestApp {

    @RestController
    public class TestApi {

        @GetMapping("/test-request-id")
        public String requestId(HttpServletRequest request) {
            return String.valueOf(request.getAttribute("RequestId")) +
                    "_" +
                    request.getServletContext().getAttribute("RequestId");
        }
    }
}
