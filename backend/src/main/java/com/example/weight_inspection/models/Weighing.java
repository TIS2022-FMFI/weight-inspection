package com.example.weight_inspection.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Weighing {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private  String IDP;

    @NotNull
    private float weight;

    @NotNull
    private float calculated_weight;

    @NotNull
    private  int quantity;

    @NotNull
    private Timestamp weighed_on;

    @NotNull
    private boolean is_exported;

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

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private  Palette palette;








}
