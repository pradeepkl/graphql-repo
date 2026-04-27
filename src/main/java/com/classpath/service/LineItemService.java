package com.classpath.service;

import com.classpath.dto.ProductStats;
import com.classpath.exception.OrderNotFoundException;
import com.classpath.model.LineItem;
import com.classpath.model.Order;
import com.classpath.repository.LineItemRepository;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LineItemService {

    private final LineItemRepository repo;

    public LineItemService(LineItemRepository repo) {
        this.repo = repo;
    }

    public List<LineItem> getByOrderId(Long orderId) {
        System.out.println("DB CALL → LineItems");
        return repo.findByOrderId(orderId);
    }

    @SchemaMapping(typeName = "Order", field = "itemCount")
    public int itemCount(Order order) {
        List<LineItem> lineItems = repo.findByOrderId(order.getId());
        return lineItems.size();
    }
}