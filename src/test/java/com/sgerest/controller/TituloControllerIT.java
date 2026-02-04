package com.sgerest.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgerest.controller.DTO.titulo.TituloDTORequest;
import com.sgerest.domain.repository.TituloRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Testes de Integração - TituloController")
class TituloControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TituloRepository tituloRepository;

    @BeforeEach
    void setUp() {
        tituloRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve cadastrar título com sucesso via POST")
    void testCadastrarTituloComSucesso() throws Exception {
        TituloDTORequest request = new TituloDTORequest("Novo Título");

        mockMvc.perform(post("/v1/titulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.descricao", equalTo("Novo Título")));
    }

    @Test
    @DisplayName("Deve retornar 400 quando descrição está vazia")
    void testCadastrarComDescricaoVazia() throws Exception {
        TituloDTORequest request = new TituloDTORequest("");

        mockMvc.perform(post("/v1/titulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", equalTo("Validation Error")));
    }

    @Test
    @DisplayName("Deve retornar 400 quando descrição é null")
    void testCadastrarComDescricaoNula() throws Exception {
        String json = "{\"descricao\": null}";

        mockMvc.perform(post("/v1/titulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 409 quando descrição duplicada")
    void testCadastrarComDescricaoDuplicada() throws Exception {
        TituloDTORequest request = new TituloDTORequest("Título Duplicado");

        mockMvc.perform(post("/v1/titulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/titulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", equalTo("Conflict")))
                .andExpect(jsonPath("$.message", containsString("registrado")));
    }

    @Test
    @DisplayName("Deve verificar que título foi salvo no banco")
    void testCadastrarComPersistencia() throws Exception {
        TituloDTORequest request = new TituloDTORequest("Título com Persistência");

        mockMvc.perform(post("/v1/titulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        var titulos = tituloRepository.findAll();
        assertEquals(1, titulos.size());
        assertEquals("Título com Persistência", titulos.get(0).getDescricao());
    }

    @Test
    @DisplayName("Deve validar Content-Type obrigatório")
    void testSemContentType() throws Exception {
        mockMvc.perform(post("/v1/titulos")
                .content("{\"descricao\": \"teste\"}"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Deve retornar Location header com ID do recurso criado")
    void testLocationHeaderComId() throws Exception {
        TituloDTORequest request = new TituloDTORequest("Teste Location");

        mockMvc.perform(post("/v1/titulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        matchesPattern(".*/v1/titulos/\\d+")));
    }

    @Test
    @DisplayName("Deve retornar ID gerado automaticamente")
    void testIdGeradoAutomaticamente() throws Exception {
        TituloDTORequest request = new TituloDTORequest("Teste ID");

        mockMvc.perform(post("/v1/titulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", greaterThan(0)));
    }
}
