package com.classpath.resolver;

import com.classpath.model.Product;
import com.classpath.repository.ProductRepository;
import com.classpath.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class ProductResolver {

    private final ProductService productService;

    @QueryMapping
    public List<Product> products() {
        return productService.getAllProducts();
    }
}
