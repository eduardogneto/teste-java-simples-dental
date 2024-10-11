package com.api.simplesdental.controller.contato;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.api.simplesdental.enums.profissional.Cargo;
import com.api.simplesdental.model.contato.Contato;
import com.api.simplesdental.model.profissional.Profissional;
import com.api.simplesdental.service.contato.ContatoService;

public class ContatoControllerTest {

    @InjectMocks
    private ContatoController contatoController;

    @Mock
    private ContatoService contatoService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(contatoController).build();
    }

    @Test
    public void testGetContactById_Success() throws Exception {
        Contato contact = new Contato();
        contact.setId(1L);
        contact.setNome("João Silva");
        contact.setContato("joao.silva@example.com");
        contact.setCreatedDate(LocalDateTime.of(2024, 10, 10, 10, 0));

        Profissional professional = new Profissional();
        professional.setId(2L);
        professional.setNome("Maria Souza");
        professional.setCargo(Cargo.DESENVOLVEDOR);
        contact.setProfissional(professional);

        when(contatoService.findById(1L)).thenReturn(Optional.of(contact));


        mockMvc.perform(get("/contatos/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateContact_Success() throws Exception {
        Contato contact = new Contato();
        contact.setId(1L);
        contact.setNome("João Silva");

        when(contatoService.save(any(Contato.class))).thenReturn(contact);

        String jsonBody = "{ \"nome\": \"João Silva\" }";

        mockMvc.perform(post("/contatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Sucesso contato com id 1 cadastrado"));
    }

    @Test
    public void testUpdateContact_Success() throws Exception {
        Contato existContact = new Contato();
        existContact.setId(1L);
        existContact.setNome("João Silva");

        Contato updatedContact = new Contato();
        updatedContact.setNome("Carlos Eduardo");

        when(contatoService.update(any(Long.class), any(Contato.class))).thenReturn(updatedContact);

        String jsonBody = "{ \"nome\": \"Carlos Eduardo\" }";

        mockMvc.perform(put("/contatos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Sucesso cadastrado alterado"));
    }

    @Test
    public void testDeleteContact_Success() throws Exception {
        doNothing().when(contatoService).delete(1L);

        mockMvc.perform(delete("/contatos/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Sucesso contato excluído"));
    }

    @Test
    public void testDeleteContact_NotFound() throws Exception {
        doNothing().when(contatoService).delete(1L);

        mockMvc.perform(delete("/contatos/999"))
                .andExpect(status().isOk())  
                .andExpect(content().string("Sucesso contato excluído"));
    }
}

