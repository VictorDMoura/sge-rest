package com.sgerest.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.sgerest.domain.entities.TituloEntity;

public interface TituloRepository extends JpaRepository<TituloEntity, Long> {

    Optional<TituloEntity> findByDescricao(String descricao);

    boolean existsByDescricaoIgnoreCase(String descricao);

    Page<TituloEntity> findAll(Pageable pageable);

}
