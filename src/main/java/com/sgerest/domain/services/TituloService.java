package com.sgerest.domain.services;

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

    public TituloDTOResponse cadastrar(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("A descrição não pode ser nula ou vazia");
        }

        TituloEntity titulo = new TituloEntity();
        titulo.setDescricao(descricao);

        tituloRepository.save(titulo);

        TituloDTOResponse response = new TituloDTOResponse(titulo.getId(), titulo.getDescricao());
        return response;
    }

}
