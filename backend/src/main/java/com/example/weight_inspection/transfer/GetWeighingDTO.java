package com.example.weight_inspection.transfer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class GetWeighingDTO {
    private Long id;

    private  String IDP;

    private float weight;

    private float calculatedWeight;

    private  int quantity;

    private Timestamp weighedOn;

    private boolean isExported;

    private boolean isCorrect;

    private String packagingName;

    private String paletteName;

    private String productReference;
}
