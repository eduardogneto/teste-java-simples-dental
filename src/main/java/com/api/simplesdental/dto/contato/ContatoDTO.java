package com.api.simplesdental.dto.contato;

import java.time.LocalDateTime;

import com.api.simplesdental.dto.profissional.ProfissionalDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContatoDTO {
    private Long id;
    private String nome; 
    private String contato;
    private LocalDateTime createdDate;
    private ProfissionalDTO profissional;
}
