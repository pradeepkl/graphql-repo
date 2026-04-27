package com.classpath.util;

import com.classpath.model.LineItem;
import com.classpath.model.Order;
import com.classpath.model.Product;
import com.classpath.repository.LineItemRepository;
import com.classpath.repository.OrderJpaRepository;
import com.classpath.repository.ProductRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class BootstrapData {

    private final OrderJpaRepository orderRepo;
    private final LineItemRepository lineItemRepo;
    private final ProductRepository productRepo;



    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {

        if (orderRepo.count() > 0) return;

        Faker faker = new Faker();
        Random random = new Random();

        // 🔹 Create Products
        List<Product> products = IntStream.range(0, 5)
                .mapToObj(i -> Product.builder()
                        .name(faker.commerce().productName())
                        .build())
                .map(productRepo::save)
                .toList();

        // 🔹 Create Orders + LineItems
        IntStream.range(0, 30).forEach(i -> {

            Order order = Order.builder()
                    .customerName(faker.name().fullName())
                    .email(faker.name().firstName()+ "@"+ faker.internet().domainName())
                    .createdAt(LocalDate.now().minusDays(random.nextInt(10)))
                    .build();

            Order savedOrder = orderRepo.save(order);

            // Nested stream for line items
            IntStream.range(0, 3)
                    .mapToObj(j -> {
                        Product randomProduct = products.get(random.nextInt(products.size()));

                        return LineItem.builder()
                                .qty(random.nextInt(5) + 1)
                                .orderId(savedOrder.getId())
                                .productId(randomProduct.getId())
                                .build();
                    })
                    .forEach(lineItemRepo::save);
        });

        System.out.println("🔥 Fake data loaded successfully!");
    }
}
