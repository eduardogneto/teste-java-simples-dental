package com.api.simplesdental.dto.contato;


import java.time.LocalDateTime;
import java.util.List;

import com.api.simplesdental.dto.profissional.ProfissionalDTO;
import com.api.simplesdental.dto.profissional.ProfissionalDTOFactory;
import com.api.simplesdental.model.contato.Contato;
import com.api.simplesdental.model.profissional.Profissional;

public class ContatoDTOFactory {
    public static ContatoDTO createContactDTO(Object[] result, List<String> fields) {
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
            Profissional professional = (Profissional) result[fields.indexOf("profissional")];
            if (professional != null) {
                ProfissionalDTO professionalDTO = ProfissionalDTOFactory.createProfessionalDTO(
                		professional, List.of("id", "nome", "cargo", "nascimento", "createdDate")
                );
                builder.profissional(professionalDTO);
            }
        }

        return builder.build();
    }
    
    public static ContatoDTO createFromDTO(Contato contact) {
        if (contact == null) {
            return null;
        }

        ContatoDTO.ContatoDTOBuilder builder = ContatoDTO.builder();

        builder.id(contact.getId());
        builder.nome(contact.getNome());
        builder.contato(contact.getContato());
        builder.createdDate(contact.getCreatedDate());

        if (contact.getProfissional() != null) {
            ProfissionalDTO professionalDTO = ProfissionalDTOFactory.createContactFromDTO(contact.getProfissional());
            builder.profissional(professionalDTO);
        }

        return builder.build();
    }
    
    
}
