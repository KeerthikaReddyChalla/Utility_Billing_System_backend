package com.chubb.gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthHeaderForwardingFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        var request = exchange.getRequest();

        if (request.getHeaders().containsKey("Authorization")) {
            return chain.filter(
                exchange.mutate()
                    .request(
                        request.mutate()
                            .header(
                                "Authorization",
                                request.getHeaders().getFirst("Authorization")
                            )
                            .build()
                    )
                    .build()
            );
        }

        return chain.filter(exchange);
    }
}
