package me.threeq.libs.log;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = {
                RequestIdListener.class,
                TestApp.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RequestIdListenerTest {
    @Autowired
    private MockMvc mockMvc;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void requestInitialized() throws Exception {
        MvcResult mvcResult = mockMvc.perform(// 1
                MockMvcRequestBuilders.get("/test-request-id") // 2
        )
                .andReturn();// 4

        int status = mvcResult.getResponse().getStatus(); // 5

        Assertions.assertEquals(200, status, "请求错误"); // 7

        String result = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(result);
    }

    @Test
    public void requestInitializedHeader() throws Exception {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("x-request-id", "11");
                    return execution.execute(request, body);
                }));
        String result = this.restTemplate.getForObject("http://localhost:" + port + "/test-request-id",
                String.class);

        Assertions.assertEquals("11_11", result);
    }
}
