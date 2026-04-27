package com.classpath.service;

import com.classpath.exception.ProductNotFoundException;
import com.classpath.model.Product;
import com.classpath.model.ProductInput;
import com.classpath.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public Product getProduct(Long id) {
        System.out.println("DB CALL → Product");
        return repo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Product create(ProductInput input) {
        Product p = Product.builder()
                .name(input.getName())
                .build();
        return repo.save(p);
    }

    public Product update(ProductInput input) {
        Product p = repo.findById(Long.valueOf(input.getId()))
                .orElseThrow();

        p.setName(input.getName());
        return repo.save(p);
    }

    public boolean delete(Long id) {
        repo.deleteById(id);
        return true;
    }


    public List<Product> getAllProducts() {
        System.out.println("DB CALL → getAllProducts");
        return repo.findAll();
    }


}
