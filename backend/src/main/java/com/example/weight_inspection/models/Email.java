package com.example.weight_inspection.models;

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
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique=true)
    @NotNull
    private String email;

    @NotNull
    private boolean sendExports;

    @OneToOne(cascade = CascadeType.PERSIST)
    private Admin admin;
}
