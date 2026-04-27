package com.classpath.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProductStats {

    private String productName;
    private Long usageCount;   // must be Long
    private List<CustomerUsage> customers;
}
