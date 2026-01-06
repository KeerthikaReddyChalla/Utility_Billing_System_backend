package com.chubb.gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayAuthDebugFilter {

    @Bean
    public GlobalFilter authHeaderLogger() {
        return (exchange, chain) -> {

            String authHeader =
                    exchange.getRequest().getHeaders().getFirst("Authorization");

            System.out.println(">>> GATEWAY received request");
            System.out.println(">>> Authorization header = " + authHeader);
            System.out.println(">>> Path = " + exchange.getRequest().getPath());

            return chain.filter(exchange);
        };
    }
}
