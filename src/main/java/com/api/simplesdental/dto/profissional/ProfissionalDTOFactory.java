package com.api.simplesdental.dto.profissional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.api.simplesdental.enums.profissional.Cargo;
import com.api.simplesdental.model.profissional.Profissional;

public class ProfissionalDTOFactory {
    public static ProfissionalDTO createProfissionalDTO(Object[] result, List<String> fields) {
        ProfissionalDTO.ProfissionalDTOBuilder builder = ProfissionalDTO.builder();

        int index = 0;
        if (fields.contains("id")) {
            builder.id((Long) result[fields.indexOf("id")]);
        }
        if (fields.contains("nome")) {
            builder.nome((String) result[fields.indexOf("nome")]);
        }
        if (fields.contains("cargo")) {
            builder.cargo((Cargo) result[fields.indexOf("cargo")]);
        }
        if (fields.contains("nascimento")) {
            builder.nascimento((LocalDate) result[fields.indexOf("nascimento")]);
        }
        if (fields.contains("createdDate")) {
            builder.createdDate((LocalDateTime) result[fields.indexOf("createdDate")]);
        }

        return builder.build();
    }
    
    public static ProfissionalDTO createProfissionalDTO(Profissional profissional, List<String> fields) {
        ProfissionalDTO.ProfissionalDTOBuilder builder = ProfissionalDTO.builder();

        if (fields.contains("id")) {
            builder.id(profissional.getId());
        }
        if (fields.contains("nome")) {
            builder.nome(profissional.getNome());
        }
        if (fields.contains("cargo")) {
            builder.cargo(profissional.getCargo());
        }
        if (fields.contains("nascimento")) {
            builder.nascimento(profissional.getNascimento());
        }
        if (fields.contains("createdDate")) {
            builder.createdDate(profissional.getCreatedDate());
        }

        return builder.build();
    }
    
    public static ProfissionalDTO createFromDTO(Profissional profissional) {
        if (profissional == null) {
            return null;
        }

        ProfissionalDTO.ProfissionalDTOBuilder builder = ProfissionalDTO.builder();

        builder.id(profissional.getId());
        builder.nome(profissional.getNome());
        builder.cargo(profissional.getCargo());
        builder.nascimento(profissional.getNascimento());
        builder.createdDate(profissional.getCreatedDate());

        return builder.build();
    }
}

