package com.example.weight_inspection.transfer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class GetProductOfPackagingDTO {
    private Long id;

    private String reference;

    private Float weight;

    private float tolerance;

    private int quantity;
}