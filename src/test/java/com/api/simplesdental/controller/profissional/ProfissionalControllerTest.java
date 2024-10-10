package com.api.simplesdental.controller.profissional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.api.simplesdental.enums.profissional.Cargo;
import com.api.simplesdental.exception.ResourceNotFoundException;
import com.api.simplesdental.model.profissional.Profissional;
import com.api.simplesdental.service.contato.ContatoService;
import com.api.simplesdental.service.profissional.ProfissionalService;

@WebMvcTest(ProfissionalController.class)
public class ProfissionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfissionalService profissionalService;

    @MockBean
    private ContatoService contatoService;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testGetProfissionalById_Success() throws Exception {
        Profissional profissional = new Profissional();
        profissional.setId(1L);
        profissional.setNome("Eduardo Neto");
        profissional.setCargo(Cargo.DESIGNER);
        profissional.setNascimento(LocalDate.of(2003, 4, 10));
        profissional.setCreatedDate(LocalDateTime.of(2024, 10, 8, 20, 31, 51));

        when(profissionalService.findById(1L)).thenReturn(Optional.of(profissional));

        mockMvc.perform(get("/profissionais/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"nome\":\"Eduardo Neto\",\"cargo\":\"DESIGNER\",\"nascimento\":\"2003-04-10\",\"createdDate\":\"2024-10-08T20:31:51\"}"));
    }
    
    @Test
    public void testGetProfissionalById_NotFound() throws Exception {
        when(profissionalService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/profissionais/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteProfissional_Success() throws Exception {
        when(contatoService.existsByProfissionalId(1L)).thenReturn(false);
        doNothing().when(profissionalService).delete(1L);

        mockMvc.perform(delete("/profissionais/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Sucesso! Profissional excluído."));

        verify(profissionalService, Mockito.times(1)).delete(1L);
    }

    @Test
    public void testDeleteProfissional_ContatosVinculados() throws Exception {
        when(contatoService.existsByProfissionalId(1L)).thenReturn(true);

        mockMvc.perform(delete("/profissionais/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Não é possível excluir o profissional, pois ele possui contatos vinculados."));

        verify(profissionalService, Mockito.never()).delete(1L);
    }

    @Test
    public void testCreateProfissional_Success() throws Exception {
        Profissional profissional = new Profissional();
        profissional.setId(1L);
        profissional.setNome("Eduardo Neto");
        profissional.setCargo(Cargo.DESIGNER);
        profissional.setNascimento(LocalDate.of(2003, 4, 10));
        profissional.setCreatedDate(LocalDateTime.of(2024, 10, 8, 20, 31, 51));

        when(profissionalService.save(any(Profissional.class))).thenReturn(profissional);

        String jsonBody = "{\"nome\":\"Eduardo Neto\",\"cargo\":\"DESIGNER\",\"nascimento\":\"2003-04-10\"}";

        mockMvc.perform(post("/profissionais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Sucesso! profissional com id 1 cadastrado"));
    }

    @Test
    public void testUpdateProfissional_Success() throws Exception {
        Profissional profissionalAtualizado = new Profissional();
        profissionalAtualizado.setId(1L);
        profissionalAtualizado.setNome("Carlos Silva");
        profissionalAtualizado.setCargo(Cargo.TESTER);
        profissionalAtualizado.setNascimento(LocalDate.of(2002, 5, 15));
        profissionalAtualizado.setCreatedDate(LocalDateTime.of(2024, 10, 8, 20, 31, 51));

        when(profissionalService.update(eq(1L), any(Profissional.class))).thenReturn(profissionalAtualizado);

        String jsonBody = "{\"nome\":\"Carlos Silva\",\"cargo\":\"TESTER\",\"nascimento\":\"2002-05-15\"}";

        mockMvc.perform(put("/profissionais/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Sucesso! cadastrado alterado"));
    }

    @Test
    public void testUpdateProfissional_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Profissional não encontrado com id 1"))
                .when(profissionalService).update(eq(1L), any(Profissional.class));

        String jsonBody = "{\"nome\":\"Carlos Silva\"}";

        mockMvc.perform(put("/profissionais/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllProfissionais_Success() throws Exception {
        List<Object> profissionais = Arrays.asList(
                new HashMap<String, Object>() {{
                    put("id", 1L);
                    put("nome", "Eduardo Neto");
                    put("cargo", "DESENVOLVEDOR");
                }},
                new HashMap<String, Object>() {{
                    put("id", 2L);
                    put("nome", "Maria Souza");
                    put("cargo", "SUPORTE");
                }}
        );

        when(profissionalService.findAll(null, null)).thenReturn(profissionais);

        mockMvc.perform(get("/profissionais"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"nome\":\"Eduardo Neto\",\"cargo\":\"DESENVOLVEDOR\"},{\"id\":2,\"nome\":\"Maria Souza\",\"cargo\":\"SUPORTE\"}]"));
    }

    @Test
    public void testGetAllProfissionais_WithFilters() throws Exception {
        List<Object> profissionais = Arrays.asList(
                new HashMap<String, Object>() {{
                    put("id", 1L);
                    put("nome", "Eduardo Neto");
                }}
        );

        when(profissionalService.findAll("Eduardo", Arrays.asList("id", "nome"))).thenReturn(profissionais);

        mockMvc.perform(get("/profissionais")
                        .param("q", "Eduardo")
                        .param("fields", "id")
                        .param("fields", "nome"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"nome\":\"Eduardo Neto\"}]"));
    }
}
