package com.classpath.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LineItemInput {


    private Long id;        // optional
    private Long productId; // required
    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer qty;    // required
}
