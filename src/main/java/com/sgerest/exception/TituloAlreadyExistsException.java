package com.sgerest.exception;

public class TituloAlreadyExistsException extends RuntimeException {

    public TituloAlreadyExistsException(String descricao) {
        super("Título com descrição '" + descricao + "' já existe.");
    }
}
