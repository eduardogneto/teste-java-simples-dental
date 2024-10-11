package com.api.simplesdental.dto.profissional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.api.simplesdental.enums.profissional.Cargo;

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
}

