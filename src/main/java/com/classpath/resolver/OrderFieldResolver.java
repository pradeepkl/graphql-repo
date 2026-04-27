package com.classpath.resolver;

import com.classpath.model.LineItem;
import com.classpath.model.Order;
import com.classpath.service.LineItemService;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class OrderFieldResolver {

    private final LineItemService service;

    public OrderFieldResolver(LineItemService service) {
        this.service = service;
    }

    @SchemaMapping(typeName = "Order", field = "lineItems")
    public List<LineItem> lineItems(Order order) {
        return service.getByOrderId(order.getId());
    }


}
