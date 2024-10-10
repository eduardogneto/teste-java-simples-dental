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

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.api.simplesdental.model.contato.Contato;
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
    public void testGetContatoById_Success() throws Exception {
        Contato contato = new Contato();
        contato.setId(1L);
        contato.setNome("João Silva");

        when(contatoService.findById(1L)).thenReturn(Optional.of(contato));

        mockMvc.perform(get("/contatos/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{'id':1,'nome':'João Silva'}"));
    }

    @Test
    public void testGetContatoById_NotFound() throws Exception {
        when(contatoService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/contatos/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateContato_Success() throws Exception {
        Contato contato = new Contato();
        contato.setId(1L);
        contato.setNome("João Silva");

        when(contatoService.save(any(Contato.class))).thenReturn(contato);

        String jsonBody = "{ \"nome\": \"João Silva\" }";

        mockMvc.perform(post("/contatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Sucesso contato com id 1 cadastrado"));
    }

    @Test
    public void testUpdateContato_Success() throws Exception {
        Contato contatoExistente = new Contato();
        contatoExistente.setId(1L);
        contatoExistente.setNome("João Silva");

        Contato contatoAtualizado = new Contato();
        contatoAtualizado.setNome("Carlos Eduardo");

        when(contatoService.update(any(Long.class), any(Contato.class))).thenReturn(contatoExistente);

        String jsonBody = "{ \"nome\": \"Carlos Eduardo\" }";

        mockMvc.perform(put("/contatos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Sucesso cadastrado alterado"));
    }

    @Test
    public void testDeleteContato_Success() throws Exception {
        doNothing().when(contatoService).delete(1L);

        mockMvc.perform(delete("/contatos/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Sucesso contato excluído"));
    }

    @Test
    public void testDeleteContato_NotFound() throws Exception {
        doNothing().when(contatoService).delete(1L);

        mockMvc.perform(delete("/contatos/999"))
                .andExpect(status().isOk())  
                .andExpect(content().string("Sucesso contato excluído"));
    }
}

