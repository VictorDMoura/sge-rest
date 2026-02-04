package com.sgerest.controller.DTO.titulo;

import jakarta.validation.constraints.NotBlank;

public record TituloDTORequest(
        @NotBlank String descricao) {

}
