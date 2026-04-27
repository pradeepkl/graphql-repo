package com.classpath.resolver;

import com.classpath.model.Product;
import com.classpath.model.ProductInput;
import com.classpath.service.ProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ProductMutation {

    private final ProductService service;

    public ProductMutation(ProductService service) {
        this.service = service;
    }

    @MutationMapping
    public Product createProduct(@Argument ProductInput input) {
        return service.create(input);
    }

    @MutationMapping
    public Product updateProduct(@Argument ProductInput input) {
        return service.update(input);
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument Long id) {
        return service.delete(id);
    }
}
