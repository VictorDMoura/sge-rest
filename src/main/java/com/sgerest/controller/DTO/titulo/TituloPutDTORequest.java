package com.sgerest.controller.DTO.titulo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TituloPutDTORequest(
                @NotBlank @Size(min = 1, max = 150) String descricao) {

}
