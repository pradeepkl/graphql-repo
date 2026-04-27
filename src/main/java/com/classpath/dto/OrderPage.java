package com.classpath.dto;

import com.classpath.model.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderPage {

    private List<Order> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
