package com.classpath.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductInput {

    private Long id;     // optional → used for update

    @NotBlank(message = "Product name is required")
    private String name; // required
}
