package com.example.weight_inspection.transfer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class GetPackagingOfProductDTO {
    private Long id;

    private String name;

    private Float weight;

    private String type;

    private float tolerance;

    private int quantity;
}