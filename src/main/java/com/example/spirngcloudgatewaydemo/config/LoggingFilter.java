package com.example.spirngcloudgatewaydemo.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {
    // 기본 constructor는 있어야함
     public LoggingFilter() {
         super(LoggingFilter.Config.class);
     }

     @Override
     public GatewayFilter apply(LoggingFilter.Config config) {
         return ((exchange, chain) -> {
             ServerHttpRequest request = exchange.getRequest();
             // reactive를 import받아야한다 webflux를 지원한다 rxjava
             ServerHttpResponse response = exchange.getResponse();

             log.info("Logging PRE filter: basemessage id -> {}", config.getBaseMessage());

             if (config.isPreLogger()) {
                 log.info("Logging Filter Start: request id -> {}", request.getId());
             }

             // Custom Post Filter
             return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                 if (config.isPostLogger()) {
                     log.info("Logging Post filter: response code -> {}", response.getStatusCode());
                 }
             }));
         });
     }

     @Data
     public static class Config {
         // put the config properties
         private String baseMessage;
         private boolean preLogger;
         private boolean postLogger;
     }
}
