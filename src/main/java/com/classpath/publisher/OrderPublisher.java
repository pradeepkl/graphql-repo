package com.classpath.publisher;

import com.classpath.model.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Flux;

@Component
public class OrderPublisher {

    private final Sinks.Many<Order> sink =
            Sinks.many().multicast().onBackpressureBuffer();

    public void publish(Order order) {
        sink.tryEmitNext(order);
    }

    public Flux<Order> getOrders() {
        return sink.asFlux();
    }
}
