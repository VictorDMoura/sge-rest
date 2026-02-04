package com.sgerest.domain.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sgerest.controller.DTO.titulo.TituloDTOResponse;
import com.sgerest.domain.entities.TituloEntity;
import com.sgerest.domain.repository.TituloRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do TituloService")
class TituloServiceTest {

    @Mock
    private TituloRepository tituloRepository;

    @InjectMocks
    private TituloService tituloService;

    private TituloEntity tituloEntity;

    @BeforeEach
    void setUp() {
        tituloEntity = new TituloEntity();
        tituloEntity.setId(1L);
        tituloEntity.setDescricao("Título Teste");
    }

    @Test
    @DisplayName("Deve cadastrar um título com sucesso")
    void testCadastrarComSucesso() {
        String descricao = "Novo Título";
        TituloEntity entidadeSalva = new TituloEntity();
        entidadeSalva.setId(1L);
        entidadeSalva.setDescricao(descricao);
        when(tituloRepository.save(any(TituloEntity.class))).thenReturn(entidadeSalva);

        TituloDTOResponse response = tituloService.cadastrar(descricao);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(descricao, response.descricao());
        verify(tituloRepository).save(any(TituloEntity.class));
    }

    @Test
    @DisplayName("Deve retornar TituloDTOResponse com id correto")
    void testRetornoComIdCorreto() {
        when(tituloRepository.save(any(TituloEntity.class))).thenReturn(tituloEntity);

        TituloDTOResponse response = tituloService.cadastrar("Teste");

        assertEquals(1L, response.id());
    }
}
