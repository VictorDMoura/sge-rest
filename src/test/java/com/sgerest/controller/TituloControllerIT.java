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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgerest.config.TestConfig;
import com.sgerest.controller.DTO.titulo.TituloDTORequest;
import com.sgerest.controller.DTO.titulo.TituloDTOResponse;
import com.sgerest.domain.repository.TituloRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
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
                .andExpect(jsonPath("$.message", containsString("já existe.")));
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

    @Test
    @DisplayName("Deve retornar 404 ao buscar título inexistente")
    void testGetByIdInexistente() throws Exception {
        Long inexistenteId = 999L;
        String msgErro = "Título com ID %d não encontrado.";
        String mensagemFormatada = String.format(msgErro, inexistenteId);

        mockMvc.perform(get("/v1/titulos/{id}", inexistenteId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", equalTo("Not Found")))
                .andExpect(jsonPath("$.message", equalTo(mensagemFormatada)));
    }

    @Test
    @DisplayName("Deve buscar título existente por ID com sucesso")
    void testGetByIdExistente() throws Exception {
        TituloDTORequest request = new TituloDTORequest("Título Existente");
        String responseContent = mockMvc.perform(post("/v1/titulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TituloDTOResponse response = objectMapper.readValue(responseContent, TituloDTOResponse.class);
        Long id = response.id();

        mockMvc.perform(get("/v1/titulos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(id.intValue())))
                .andExpect(jsonPath("$.descricao", equalTo("Título Existente")));
    }

    @Test
    @DisplayName("Deve listar títulos com paginação")
    void testListarTitulosComPaginacao() throws Exception {
        for (int i = 1; i <= 15; i++) {
            TituloDTORequest request = new TituloDTORequest("Título " + i);
            mockMvc.perform(post("/v1/titulos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/v1/titulos")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements", equalTo(15)))
                .andExpect(jsonPath("$.totalPages", equalTo(2)))
                .andExpect(jsonPath("$.pageNumber", equalTo(0)))
                .andExpect(jsonPath("$.pageSize", equalTo(10)))
                .andExpect(jsonPath("$.hasNext", equalTo(true)))
                .andExpect(jsonPath("$.hasPrevious", equalTo(false)));
    }
}
