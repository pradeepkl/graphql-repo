package com.classpath.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerUsage {

    private String name;
    private Long orderCount;
}
