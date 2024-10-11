package com.api.simplesdental.dto.profissional;

import java.time.LocalDate;

import com.api.simplesdental.enums.profissional.Cargo;

import lombok.Data;

@Data
public class ProfissionalUpdateDTO {
    private String nome;
    private Cargo cargo;
    private LocalDate nascimento;
}
