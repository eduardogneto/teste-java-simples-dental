package com.api.simplesdental.dto.contato;


import java.time.LocalDateTime;
import java.util.List;

import com.api.simplesdental.dto.profissional.ProfissionalDTO;
import com.api.simplesdental.dto.profissional.ProfissionalDTOFactory;
import com.api.simplesdental.model.profissional.Profissional;

public class ContatoDTOFactory {
    public static ContatoDTO createContatoDTO(Object[] result, List<String> fields) {
        ContatoDTO.ContatoDTOBuilder builder = ContatoDTO.builder();

        if (fields.contains("id")) {
            builder.id((Long) result[fields.indexOf("id")]);
        }
        if (fields.contains("nome")) {
            builder.nome((String) result[fields.indexOf("nome")]);
        }
        if (fields.contains("contato")) {
            builder.contato((String) result[fields.indexOf("contato")]);
        }
        if (fields.contains("createdDate")) {
            builder.createdDate((LocalDateTime) result[fields.indexOf("createdDate")]);
        }
        if (fields.contains("profissional")) {
            Profissional profissional = (Profissional) result[fields.indexOf("profissional")];
            if (profissional != null) {
                ProfissionalDTO profissionalDTO = ProfissionalDTOFactory.createProfissionalDTO(
                    profissional, List.of("id", "nome", "cargo", "nascimento", "createdDate")
                );
                builder.profissional(profissionalDTO);
            }
        }

        return builder.build();
    }
    
    
}
