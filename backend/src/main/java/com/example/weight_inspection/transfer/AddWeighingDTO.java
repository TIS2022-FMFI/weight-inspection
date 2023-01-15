package com.example.weight_inspection.transfer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AddWeighingDTO {

    @NotNull
    private String IDP;

    @NotNull
    private String reference;

    @NotNull
    private float weight;

    @NotNull
    private int quantity;
    private Long paletteId;

    private Long packagingId;
}
