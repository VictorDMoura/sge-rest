package com.sgerest.domain.services;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import com.sgerest.controller.DTO.titulo.TituloDTOResponse;
import com.sgerest.domain.entities.TituloEntity;
import com.sgerest.domain.repository.TituloRepository;

@Service
@Log4j2
public class TituloService {

    private final TituloRepository tituloRepository;

    public TituloService(TituloRepository tituloRepository) {
        this.tituloRepository = tituloRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public TituloDTOResponse cadastrar(String descricao) {
        log.info("Cadastrando título com descrição: {}", descricao);

        if (tituloRepository.existsByDescricaoIgnoreCase(descricao)) {
            log.warn("Título com descrição '{}' já existe.", descricao);
            throw new IllegalArgumentException("Título já existe.");
        }

        TituloEntity titulo = new TituloEntity();
        titulo.setDescricao(descricao);

        TituloEntity tituloPersistido = tituloRepository.save(titulo);

        TituloDTOResponse response = new TituloDTOResponse(tituloPersistido.getId(), tituloPersistido.getDescricao());
        log.info("Título cadastrado com sucesso. ID: {}", response.id());
        return response;
    }

}
