package com.sanju.apigateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilterFactory.Config> {

    private final JwtAuthenticationGatewayFilter jwtAuthenticationGatewayFilter;

    public JwtAuthenticationGatewayFilterFactory(
            JwtAuthenticationGatewayFilter jwtAuthenticationGatewayFilter) {
        super(Config.class);
        this.jwtAuthenticationGatewayFilter = jwtAuthenticationGatewayFilter;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return jwtAuthenticationGatewayFilter.apply(new JwtAuthenticationGatewayFilter.Config());
    }

    @Override
    public String name() {
        return "JwtAuthentication";
    }

    public static class Config {
    }
}
