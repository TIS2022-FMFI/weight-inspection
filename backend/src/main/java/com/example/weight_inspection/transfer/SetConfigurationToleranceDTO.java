package com.example.weight_inspection.transfer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SetConfigurationToleranceDTO {
    @NotNull
    private Float tolerance;
}
