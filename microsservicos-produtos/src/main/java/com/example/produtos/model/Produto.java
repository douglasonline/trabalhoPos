package com.example.produtos.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Table(name = "produto")
@Entity
public class Produto {

    @Id
    @GeneratedValue
    private Long id;

    private String nome;

    private BigDecimal valor;
}
