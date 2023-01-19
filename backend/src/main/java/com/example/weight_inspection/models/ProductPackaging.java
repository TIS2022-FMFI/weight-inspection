package com.example.weight_inspection.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProductPackaging {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Float tolerance;

    private Integer quantity;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private Packaging packaging;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private Product product;
}