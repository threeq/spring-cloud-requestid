package me.threeq.libs.log;

import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

public class RequestIdUtil {
    private static InheritableThreadLocal<Map<String, String>> inheritableThreadLocal;
    static {
        inheritableThreadLocal = new InheritableThreadLocal<Map<String, String>>() {
            @Override
            protected Map<String, String> initialValue() {
                return new HashMap<>();
            }

            @Override
            protected Map<String, String> childValue(Map<String, String> parentValue) {
                return parentValue == null ? new HashMap<>() : new HashMap<>(parentValue);
            }
        };
    }

    public static void put(String requestId) {
        inheritableThreadLocal.get().put("RequestId", requestId);
        MDC.put("RequestId", requestId);
    }

    public static void delete() {
        inheritableThreadLocal.get().remove("RequestId");
        MDC.remove("RequestId");
    }

    public static String get() {
        return inheritableThreadLocal.get().get("RequestId");
    }

    public static String httpHeaderName() {
        return "x-request-id";
    }

    public static String attributeName() {
        return "RequestId";
    }
}
