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

        TituloEntity titulo = new TituloEntity();
        titulo.setDescricao(descricao);

        TituloEntity tituloPersistido = tituloRepository.save(titulo);

        TituloDTOResponse response = new TituloDTOResponse(tituloPersistido.getId(), tituloPersistido.getDescricao());
        log.info("Cadastrando título: {}", descricao);
        return response;
    }

}
