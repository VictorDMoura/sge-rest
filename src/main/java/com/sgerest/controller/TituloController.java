package com.sgerest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/titulos")
public class TituloController {

    @GetMapping
    public String getMethodName() {
        return "Testando a API";
    }

}
