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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import com.sgerest.controller.DTO.titulo.TituloDTOResponse;
import com.sgerest.domain.entities.TituloEntity;
import com.sgerest.domain.repository.TituloRepository;
import com.sgerest.exception.ArgumentNotFoundException;
import com.sgerest.exception.TituloAlreadyExistsException;

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

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar título já existente")
    void testCadastrarTituloExistente() {
        String descricaoExistente = "Título Existente";
        when(tituloRepository.existsByDescricaoIgnoreCase(descricaoExistente)).thenReturn(true);
        TituloAlreadyExistsException exception = assertThrows(TituloAlreadyExistsException.class, () -> {
            tituloService.cadastrar(descricaoExistente);
        });
        assertEquals("Título com descrição 'Título Existente' já existe.", exception.getMessage());
        verify(tituloRepository, never()).save(any(TituloEntity.class));
    }

    @Test
    @DisplayName("Deve buscar título por ID com sucesso")
    void testGetByIdComSucesso() {
        Long id = 1L;
        when(tituloRepository.findById(id)).thenReturn(Optional.of(tituloEntity));
        TituloDTOResponse response = tituloService.getById(id);
        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Título Teste", response.descricao());
        verify(tituloRepository).findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar título por ID inexistente")
    void testGetByIdInexistente() {
        Long idInexistente = 99L;
        when(tituloRepository.findById(idInexistente)).thenReturn(Optional.empty());
        var exception = assertThrows(ArgumentNotFoundException.class, () -> {
            tituloService.getById(idInexistente);
        });
        assertEquals("Título com ID 99 não encontrado.", exception.getMessage());
        verify(tituloRepository).findById(idInexistente);
    }

    @Test
    @DisplayName("Deve listar títulos com paginação")
    void testListarTodosComPaginacao() {
        Pageable pageable = PageRequest.of(0, 10);
        List<TituloEntity> titulos = List.of(tituloEntity);
        Page<TituloEntity> pagina = new PageImpl<>(
                titulos, pageable, titulos.size());
        when(tituloRepository.findAll(pageable)).thenReturn(pagina);
        var response = tituloService.listarTodos(pageable);
        assertNotNull(response);
        assertEquals(1, response.totalElements());
        assertEquals(1, response.content().size());
        assertEquals(tituloEntity.getId(), response.content().get(0).id());
        verify(tituloRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver títulos")
    void testListarTodosSemTítulos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TituloEntity> paginaVazia = new PageImpl<>(
                List.of(), pageable, 0);
        when(tituloRepository.findAll(pageable)).thenReturn(paginaVazia);
        var response = tituloService.listarTodos(pageable);
        assertNotNull(response);
        assertEquals(0, response.totalElements());
        assertTrue(response.content().isEmpty());
        verify(tituloRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Deve atualizar título com sucesso")
    void testAtualizarComSucesso() {
        Long id = 1L;
        String novaDescricao = "Título Atualizado";
        TituloEntity tituloExistente = new TituloEntity();
        tituloExistente.setId(id);
        tituloExistente.setDescricao("Título Antigo");
        when(tituloRepository.findById(id)).thenReturn(Optional.of(tituloExistente));
        when(tituloRepository.existsByDescricaoIgnoreCase(novaDescricao)).thenReturn(false);
        TituloEntity tituloAtualizado = new TituloEntity();
        tituloAtualizado.setId(id);
        tituloAtualizado.setDescricao(novaDescricao);
        when(tituloRepository.save(any(TituloEntity.class))).thenReturn(tituloAtualizado);
        TituloDTOResponse response = tituloService.atualizar(id, novaDescricao);
        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals(novaDescricao, response.descricao());
        verify(tituloRepository).findById(id);
        verify(tituloRepository).existsByDescricaoIgnoreCase(novaDescricao);
        verify(tituloRepository).save(any(TituloEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar título para descrição já existente")
    void testAtualizarParaDescricaoExistente() {
        Long id = 1L;
        String descricaoExistente = "Título Existente";
        TituloEntity tituloExistente = new TituloEntity();
        tituloExistente.setId(id);
        tituloExistente.setDescricao("Título Antigo");
        when(tituloRepository.findById(id)).thenReturn(Optional.of(tituloExistente));
        when(tituloRepository.existsByDescricaoIgnoreCase(descricaoExistente)).thenReturn(true);
        TituloAlreadyExistsException exception = assertThrows(TituloAlreadyExistsException.class, () -> {
            tituloService.atualizar(id, descricaoExistente);
        });
        assertEquals("Título com descrição 'Título Existente' já existe.", exception.getMessage());
        verify(tituloRepository).findById(id);
        verify(tituloRepository).existsByDescricaoIgnoreCase(descricaoExistente);
        verify(tituloRepository, never()).save(any(TituloEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar título inexistente")
    void testAtualizarTituloInexistente() {
        Long idInexistente = 99L;
        String novaDescricao = "Título Atualizado";
        when(tituloRepository.findById(idInexistente)).thenReturn(Optional.empty());
        var exception = assertThrows(ArgumentNotFoundException.class,
                () -> tituloService.atualizar(idInexistente, novaDescricao));
        assertEquals("Título com ID 99 não encontrado.", exception.getMessage());
        verify(tituloRepository).findById(idInexistente);
        verify(tituloRepository, never()).existsByDescricaoIgnoreCase(anyString());
        verify(tituloRepository, never()).save(any(TituloEntity.class));
    }

    @Test
    @DisplayName("Deve deletar título com sucesso")
    void testDeletarComSucesso() {
        Long id = 1L;
        TituloEntity tituloExistente = new TituloEntity();
        tituloExistente.setId(id);
        tituloExistente.setDescricao("Título a ser deletado");
        when(tituloRepository.findById(id)).thenReturn(Optional.of(tituloExistente));
        assertDoesNotThrow(() -> tituloService.deletar(id));
        verify(tituloRepository).findById(id);
        verify(tituloRepository).delete(tituloExistente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar título inexistente")
    void testDeletarTituloInexistente() {
        Long idInexistente = 99L;
        when(tituloRepository.findById(idInexistente)).thenReturn(Optional.empty());
        var exception = assertThrows(ArgumentNotFoundException.class, () -> {
            tituloService.deletar(idInexistente);
        });
        assertEquals("Título com ID 99 não encontrado.", exception.getMessage());
        verify(tituloRepository).findById(idInexistente);
        verify(tituloRepository, never()).delete(any(TituloEntity.class));
    }
}
