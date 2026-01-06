package com.chubb.consumer.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.chubb.consumer.config.FeignAuthInterceptor;
import com.chubb.consumer.dto.AuthUserResponse;


@FeignClient(name = "auth-service", configuration = FeignAuthInterceptor.class)
public interface AuthClient {

    @GetMapping("/auth/users/{userId}")
    AuthUserResponse getUserById(@PathVariable("userId") String userId);
}

