package com.sgerest.config;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.BeforeEach;
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
@DisplayName("Testes de Integração - GlobalExceptionHandler")
class GlobalExceptionHandlerIT {

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
    @DisplayName("Deve tratar erro 404 para endpoint inexistente")
    void testEndpointNaoEncontrado() throws Exception {
        mockMvc.perform(get("/v1/inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar timestamp em erro de validação")
    void testErroComTimestamp() throws Exception {
        mockMvc.perform(post("/v1/titulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"descricao\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Deve retornar path no erro")
    void testErroComPath() throws Exception {
        mockMvc.perform(post("/v1/titulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"descricao\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path", endsWith("/v1/titulos")));
    }

    @Test
    @DisplayName("Deve retornar status code em erro")
    void testErroComStatus() throws Exception {
        mockMvc.perform(post("/v1/titulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"descricao\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", equalTo(400)));
    }

    @Test
    @DisplayName("Deve retornar mensagem de erro legível")
    void testErroComMensagem() throws Exception {
        mockMvc.perform(post("/v1/titulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"descricao\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Deve retornar 400 para JSON inválido")
    void testJsonInvalido() throws Exception {
        mockMvc.perform(post("/v1/titulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"descricao\": invalid}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 409 para violação de constraint única")
    void testViolacaoConstraintUnica() throws Exception {
        // Criar primeiro título
        TituloDTORequest request = new TituloDTORequest("Descrição Unica");
        mockMvc.perform(post("/v1/titulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Tentar criar com a mesma descrição
        mockMvc.perform(post("/v1/titulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", equalTo(409)))
                .andExpect(jsonPath("$.error", equalTo("Conflict")));
    }

    @Test
    @DisplayName("Deve incluir todos os campos na resposta de erro")
    void testRespostaErroCompleta() throws Exception {
        mockMvc.perform(post("/v1/titulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"descricao\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    @DisplayName("Deve retornar erro descritivo para validação")
    void testMensagemValidacaoDescritiva() throws Exception {
        mockMvc.perform(post("/v1/titulos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"descricao\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", equalTo("Validation Error")));
    }
}
