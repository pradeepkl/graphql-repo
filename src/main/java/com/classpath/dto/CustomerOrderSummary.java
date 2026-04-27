package com.classpath.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class CustomerOrderSummary {


    private String customerName;
    private Long totalOrders;
}
