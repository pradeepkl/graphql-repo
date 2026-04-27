package com.classpath.service;

import com.classpath.dto.CustomerOrderSummary;
import com.classpath.dto.CustomerUsage;
import com.classpath.dto.OrderPage;
import com.classpath.dto.ProductStats;
import com.classpath.exception.OrderNotFoundException;
import com.classpath.exception.ProductNotFoundException;
import com.classpath.model.*;
import com.classpath.publisher.OrderPublisher;
import com.classpath.repository.LineItemRepository;
import com.classpath.repository.OrderJpaRepository;
import com.classpath.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderJpaRepository orderRepository;
    private final LineItemRepository lineItemRepository;
    private final OrderPublisher orderPublisher;
    private final ProductRepository productRepository;

    public Order getOrder(Long id) {
        System.out.println("DB CALL → Order");
        return this.orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    public List<Order> getAllOrders() {
        System.out.println("DB CALL → getAllOrders");
        return orderRepository.findAll();
    }

    public List<Order> findByCustomerName(String name) {
        System.out.println("DB CALL → findByCustomerName");

        return orderRepository.findByCustomerName(name);
    }

    public List<Order> findByDateRange(String start, String end) {

        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        System.out.println("DB CALL → ordersByDateRange");

        return orderRepository.findByCreatedAtBetween(startDate, endDate);
    }

    public List<CustomerOrderSummary> getTopCustomers(int limit) {

        System.out.println("DB CALL → topCustomers");

        return orderRepository.findAll().stream()

                // group by customer
                .collect(Collectors.groupingBy(Order::getCustomerName))

                // convert to DTO
                .entrySet()
                .stream()
                .map(entry -> new CustomerOrderSummary(
                        entry.getKey(),
                       Long.valueOf(entry.getValue().size())
                ))
                // sort descending
                .sorted(comparing(CustomerOrderSummary::getTotalOrders).reversed())                // limit
                .limit(limit)
                .toList();
    }

    public List<CustomerOrderSummary> getTopCustomersWIthJpql(int limit) {

        System.out.println("DB CALL → topCustomers (JPQL)");

        return orderRepository.findTopCustomers()
                .stream()
                .limit(limit)
                .toList();
    }

    public List<Order> findOrdersByProduct(Long productId) {

        List<LineItem> lineItems = lineItemRepository.findByProductId(productId);
        if(lineItems.isEmpty()){
            throw new ProductNotFoundException(productId);
        }

        System.out.println("Came here !!!!");
        List<Long> orderIds = lineItems.stream()
                .map(LineItem::getOrderId)
                .distinct()
                .toList();

        return orderRepository.findByIdIn(orderIds);
    }

    public List<ProductStats> getProductStatsWithCustomerCounts() {

        List<Object[]> rows = lineItemRepository.getProductCustomerStats();

        Map<String, List<CustomerUsage>> customerMap = new HashMap<>();
        Map<String, Long> totalCountMap = new HashMap<>();

        for (Object[] row : rows) {

            String product = (String) row[0];
            String customer = (String) row[1];
            Long count = (Long) row[2];

            // Add customer usage
            customerMap
                    .computeIfAbsent(product, k -> new ArrayList<>())
                    .add(new CustomerUsage(customer, count));

            // Aggregate total usage
            totalCountMap.merge(product, count, Long::sum);
        }

        return customerMap.entrySet()
                .stream()
                .map(entry -> {

                    List<CustomerUsage> sortedCustomers = entry.getValue()
                            .stream()
                            .sorted(Comparator.comparing(CustomerUsage::getOrderCount).reversed().thenComparing(CustomerUsage::getName))
                            .toList();

                    return new ProductStats(
                            entry.getKey(),
                            totalCountMap.get(entry.getKey()),
                            sortedCustomers
                    );
                })
                .sorted(Comparator.comparing(ProductStats::getUsageCount).reversed())
                .toList();
    }

    public OrderPage getOrdersPagedSorted(int offset, int limit, OrderSortField sortBy) {

        System.out.println("DB CALL → ordersPagedSorted");

        int page = offset / limit;

        Pageable pageable = PageRequest.of(
                page,
                limit,
                Sort.by(sortBy.name()).ascending()
        );

        Page<Order> result = orderRepository.findAll(pageable);

        return new OrderPage(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    public List<Order> searchOrders(String keyword) {

        System.out.println("DB CALL → searchOrders");

        return orderRepository.searchOrders(keyword);
    }

    public List<Order> findOrdersByProductName(String name) {

        System.out.println("DB CALL → ordersByProductName");

        return orderRepository.findOrdersByProductName(name);
    }

    @Transactional
    public Order create(OrderInput input) {

        Order order = Order.builder()
                .customerName(input.getCustomerName())
                .email(input.getEmail())
                .createdAt(LocalDate.now())
                .build();

        Order saved = orderRepository.save(order);

        if (input.getItems() != null) {

            List<LineItem> items = input.getItems().stream()
                    .map(i -> LineItem.builder()
                            .id(i.getId())
                            .orderId(saved.getId())
                            .productId(i.getProductId())
                            .qty(i.getQty())
                            .build())
                    .toList();

            lineItemRepository.saveAll(items);
        }

        orderPublisher.publish(saved);

        return saved;
    }

    @Transactional
    public Order update(OrderInput input) {

        Order order = orderRepository.findById(Long.valueOf(input.getId()))
                .orElseThrow();

        order.setCustomerName(input.getCustomerName());
        order.setEmail(input.getEmail());

        // delete old items
        lineItemRepository.deleteAll(
                lineItemRepository.findByOrderId(order.getId()));

        // add new items
        if (input.getItems() != null) {
            List<LineItem> items = input.getItems().stream()
                    .map(i -> LineItem.builder()
                            .id(i.getId())
                            .orderId(order.getId())
                            .productId(i.getProductId())
                            .qty(i.getQty())
                            .build())
                    .toList();

            lineItemRepository.saveAll(items);
        }

        return orderRepository.save(order);
    }

    public boolean delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException("Order not found: " + id);
        }
        List<LineItem> lineItems = lineItemRepository.findByOrderId(id);
        lineItemRepository.deleteAll(lineItems);
        orderRepository.deleteById(id);
        return true;
    }
}
