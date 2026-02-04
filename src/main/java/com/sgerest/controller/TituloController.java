package com.sgerest.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import com.sgerest.controller.DTO.titulo.TituloDTORequest;
import com.sgerest.controller.DTO.titulo.TituloDTOResponse;
import com.sgerest.domain.services.TituloService;

@RestController
@RequestMapping("v1/titulos")
public class TituloController {

    private final TituloService tituloService;

    public TituloController(TituloService tituloService) {
        this.tituloService = tituloService;
    }

    @PostMapping
    public ResponseEntity<TituloDTOResponse> save(@Valid @RequestBody TituloDTORequest request) {
        TituloDTOResponse response = tituloService.cadastrar(request.descricao());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

}
