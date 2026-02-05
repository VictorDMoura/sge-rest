package com.sgerest.domain.services;

import lombok.extern.log4j.Log4j2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sgerest.controller.DTO.PageResponse;
import com.sgerest.controller.DTO.titulo.TituloDTOResponse;
import com.sgerest.domain.entities.TituloEntity;
import com.sgerest.domain.repository.TituloRepository;
import com.sgerest.exception.TituloAlreadyExistsException;
import com.sgerest.exception.ArgumentNotFoundException;

@Service
@Log4j2
public class TituloService {

    private final TituloRepository tituloRepository;

    public TituloService(TituloRepository tituloRepository) {
        this.tituloRepository = tituloRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public TituloDTOResponse cadastrar(String descricao) {
        log.info("Cadastrando título com descrição: {}", descricao);

        descricao = descricao.trim();

        if (tituloRepository.existsByDescricaoIgnoreCase(descricao)) {
            log.warn("Título com descrição '{}' já existe.", descricao);
            throw new TituloAlreadyExistsException(descricao);
        }

        TituloEntity titulo = new TituloEntity();
        titulo.setDescricao(descricao);

        TituloEntity tituloPersistido = tituloRepository.save(titulo);

        TituloDTOResponse response = mapToDTO(tituloPersistido);
        log.info("Título cadastrado com sucesso. ID: {}", response.id());
        return response;
    }

    @Transactional(readOnly = true)
    public TituloDTOResponse getById(Long id) {
        log.info("Buscando título com ID: {}", id);
        TituloEntity titulo = tituloRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Título com ID {} não encontrado.", id);
                    return new ArgumentNotFoundException("Título com ID " + id + " não encontrado.");
                });

        TituloDTOResponse response = mapToDTO(titulo);
        log.info("Título encontrado: {}", response);
        return response;

    }

    @Transactional(readOnly = true)
    public PageResponse<TituloDTOResponse> listarTodos(Pageable pageable) {
        log.info("Listando todos os títulos com paginação: {}", pageable);
        Page<TituloEntity> titulosPage = tituloRepository.findAll(pageable);

        Page<TituloDTOResponse> responsePage = titulosPage.map(this::mapToDTO);

        log.info("Total de títulos encontrados: {}", responsePage.getTotalElements());
        return PageResponse.of(responsePage);
    }

    @Transactional(rollbackFor = Exception.class)
    public TituloDTOResponse atualizar(Long id, String descricao) {
        log.info("Atualizando título com ID: {}", id);
        TituloEntity titulo = tituloRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Título com ID {} não encontrado para atualização.", id);
                    return new ArgumentNotFoundException("Título com ID " + id + " não encontrado.");
                });

        descricao = descricao.trim();

        if (tituloRepository.existsByDescricaoIgnoreCase(descricao) &&
                !titulo.getDescricao().equalsIgnoreCase(descricao)) {
            log.warn("Título com descrição '{}' já existe.", descricao);
            throw new TituloAlreadyExistsException(descricao);
        }

        titulo.setDescricao(descricao);
        TituloEntity tituloAtualizado = tituloRepository.save(titulo);

        TituloDTOResponse response = mapToDTO(tituloAtualizado);
        log.info("Título atualizado com sucesso. ID: {}", response.id());
        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletar(Long id) {
        log.info("Deletando título com ID: {}", id);
        TituloEntity titulo = tituloRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Título com ID {} não encontrado para deleção.", id);
                    return new ArgumentNotFoundException("Título com ID " + id + " não encontrado.");
                });

        tituloRepository.delete(titulo);
        log.info("Título com ID {} deletado com sucesso.", id);
    }

    private TituloDTOResponse mapToDTO(TituloEntity titulo) {
        return new TituloDTOResponse(titulo.getId(), titulo.getDescricao());
    }

}
