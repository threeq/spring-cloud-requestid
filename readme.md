# spring-cloud-requestid

 spring-cloud 请求全局统一日志 id，通过 x-request-id 请求头部传输
  

# TODO

- [ ] 支持 okhttp 客户端
- [ ] 支持 @Scheduled 计划任务

# v1.0.0

* 支持 @Async 异步方法日志
* 使用 log4j2 支持 json 格式输出

# v0.8.0

* 全局请求 id 支持，使用 http 请求头部 `x-request-id` 传递
* http client 支持：`RestTemplate`
* 支持 feign 客户端
* 支持 Hystrix 中 线程熔断 策略