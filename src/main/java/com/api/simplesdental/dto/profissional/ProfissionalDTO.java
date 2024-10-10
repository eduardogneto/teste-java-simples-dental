package com.api.simplesdental.dto.profissional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.api.simplesdental.enums.profissional.Cargo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfissionalDTO {
    private Long id;
    private String nome;
    private Cargo cargo;
    private LocalDate nascimento;
    private LocalDateTime createdDate;
}
