package com.sgerest.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "titulo")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class TituloEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_titulo")
    private Long id;

    @Column(name = "tx_descricao", length = 150, unique = true, nullable = false)
    private String descricao;

}
