package com.classpath.resolver;

import com.classpath.model.Order;
import com.classpath.publisher.OrderPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class OrderSubscription {

    private final OrderPublisher publisher;

    @SubscriptionMapping
    public Flux<Order> orderCreated() {
        return publisher.getOrders();
    }
}
