package com.classpath.resolver;

import com.classpath.dto.CustomerOrderSummary;
import com.classpath.dto.OrderPage;
import com.classpath.dto.ProductStats;
import com.classpath.model.Order;
import com.classpath.model.OrderInput;
import com.classpath.model.OrderSortField;
import com.classpath.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderResolver {

    private final OrderService orderService;

    @QueryMapping
    public Order order(@Argument Long id) {
        return orderService.getOrder(id);
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> orders() {
        return orderService.getAllOrders();
    }
    @QueryMapping
    public List<Order> ordersByCustomer(@Argument String name) {
        return orderService.findByCustomerName(name);
    }

    @QueryMapping
    public List<Order> ordersByDateRange(@Argument String start,
                                         @Argument String end) {
        return orderService.findByDateRange(start, end);
    }

    @QueryMapping
    public List<CustomerOrderSummary> topCustomers(@Argument int limit) {
        return orderService.getTopCustomersWIthJpql(limit);
    }

    @QueryMapping
    public List<Order> ordersByProduct(@Argument Long productId) {
        return orderService.findOrdersByProduct(productId);
    }

    @QueryMapping
    public List<ProductStats> productStats() {
        return orderService.getProductStatsWithCustomerCounts();
    }

    @QueryMapping
    public OrderPage ordersPagedSorted(@Argument int offset,
                                       @Argument int limit,
                                       @Argument OrderSortField sortBy) {
        return orderService.getOrdersPagedSorted(offset, limit, sortBy);
    }

    @QueryMapping
    public List<Order> searchOrders(@Argument String keyword) {
        return orderService.searchOrders(keyword);
    }

    @QueryMapping
    public List<Order> ordersByProductName(@Argument String name) {
        return orderService.findOrdersByProductName(name);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Order createOrder(@Valid @Argument OrderInput input) {
        return orderService.create(input);
    }

    @MutationMapping
    public Order updateOrder(@Valid @Argument OrderInput input) {
        return orderService.update(input);
    }

    @MutationMapping
    public Boolean deleteOrder(@Argument Long id) {
        return orderService.delete(id);
    }
}
