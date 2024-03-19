package com.usco.demo.stock.web.errors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface IExceptionHandler {
    Mono<ResponseEntity<Object>> handleAnyException(Throwable var1, ServerWebExchange var2);
}
