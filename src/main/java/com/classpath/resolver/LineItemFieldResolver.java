package com.classpath.resolver;

import com.classpath.model.LineItem;
import com.classpath.model.Product;
import com.classpath.service.ProductService;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class LineItemFieldResolver {

    private final ProductService productService;

    public LineItemFieldResolver(ProductService productService) {
        this.productService = productService;
    }

    @SchemaMapping(typeName = "LineItem", field = "product")
    public Product product(LineItem item) {
        return productService.getProduct(item.getProductId());
    }


}
