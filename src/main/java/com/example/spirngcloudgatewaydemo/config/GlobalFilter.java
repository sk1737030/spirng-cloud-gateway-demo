package com.example.spirngcloudgatewaydemo.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


/**
 * Filter LifeCycle global -> custom -> logging 순서대로 된다.
 */
@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    // 기본 constructor는 있어야함
    public GlobalFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            // reactive를 import받아야한다 webflux를 지원한다 rxjava
            ServerHttpResponse response = exchange.getResponse();

            log.info("Global PRE filter: basemessage id -> {}", config.getBaseMessage());

            if (config.isPreLogger()) {
                log.info("Global Filter Start: request id -> {}", request.getId());
            }

            // Custom Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable((Runnable) () -> {
                if (config.isPostLogger()) {
                    log.info("Custom Post filter: response code -> {}",
                        response.getStatusCode());
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
