package com.chubb.billing.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	    @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        return http
	                .csrf(csrf -> csrf.disable())
	                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
	                .oauth2ResourceServer(oauth ->
	                        oauth.jwt(jwt ->
	                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
	                        )
	                )
	                .build();
	    }

	    @Bean
	    public JwtAuthenticationConverter jwtAuthenticationConverter() {

	        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

	        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
	            List<String> authorities = jwt.getClaimAsStringList("authorities");

	            if (authorities == null) {
	                return List.of();
	            }

	            return authorities.stream()
	                    .map(SimpleGrantedAuthority::new)
	                    .collect(Collectors.toList());
	        });

	        return converter;
	    }
	}


