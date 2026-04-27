package com.classpath.config;

import com.classpath.exception.OrderNotFoundException;
import com.classpath.exception.ProductNotFoundException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.ErrorType;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class GraphQLExceptionConfig {

    @Bean
    public DataFetcherExceptionResolver exceptionResolver() {
        return (ex, env) -> {

            if (ex instanceof OrderNotFoundException) {
                return Mono.just(List.of(buildError("Order not found", "NOT_FOUND", env)));
            }
            if (ex instanceof ProductNotFoundException) {
                return Mono.just(List.of(buildError("Product not found", "NOT_FOUND", env)));
            }


            if (ex instanceof IllegalArgumentException) {
                return Mono.just(List.of(buildError(ex.getMessage(), "BAD_REQUEST", env)));
            }

            return Mono.just(List.of(buildError("Internal server error", "INTERNAL_ERROR", env)));
        };
    }

    private GraphQLError buildError(String message, String code, DataFetchingEnvironment env) {

        Map<String, Object> extensions = new HashMap<>();
        extensions.put("code", code);
        extensions.put("timestamp", Instant.now().toString());

        return GraphqlErrorBuilder.newError(env)
                .message(message)
                .errorType(ErrorType.BAD_REQUEST)
                .extensions(Map.of(
                        "code", code,
                        "timestamp", Instant.now().toString()
                ))
                .build();
    }
}
