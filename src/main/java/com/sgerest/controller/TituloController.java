package com.sgerest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import com.sgerest.controller.DTO.PageResponse;
import com.sgerest.controller.DTO.titulo.TituloDTORequest;
import com.sgerest.controller.DTO.titulo.TituloDTOResponse;
import com.sgerest.domain.services.TituloService;

@RestController
@RequestMapping("v1/titulos")
@Tag(name = "Título", description = "Operações relacionadas a títulos")
public class TituloController {

    private final TituloService tituloService;

    public TituloController(TituloService tituloService) {
        this.tituloService = tituloService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Cadastrar um novo título")
    public ResponseEntity<TituloDTOResponse> save(@Valid @RequestBody TituloDTORequest request) {
        TituloDTOResponse response = tituloService.cadastrar(request.descricao());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter título por ID")
    public ResponseEntity<TituloDTOResponse> getById(@PathVariable Long id) {
        var response = tituloService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Listar todos os títulos com paginação")
    public ResponseEntity<PageResponse<TituloDTOResponse>> getAll(Pageable pageable) {
        var response = tituloService.listarTodos(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Atualizar um título existente")
    public ResponseEntity<TituloDTOResponse> update(@PathVariable Long id,
            @Valid @RequestBody TituloDTORequest request) {
        var response = tituloService.atualizar(id, request.descricao());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um título por ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tituloService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
