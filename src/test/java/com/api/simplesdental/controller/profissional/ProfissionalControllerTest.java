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

import com.api.simplesdental.dto.profissional.ProfissionalDTO;
import com.api.simplesdental.dto.profissional.ProfissionalUpdateDTO;
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
    public void testGetProfessionalById_Success() throws Exception {
        Profissional professional = new Profissional();
        professional.setId(1L);
        professional.setNome("Eduardo Neto");
        professional.setCargo(Cargo.DESIGNER);
        professional.setNascimento(LocalDate.of(2003, 4, 10));
        professional.setCreatedDate(LocalDateTime.of(2024, 10, 8, 20, 31, 51));

        when(profissionalService.findById(1L)).thenReturn(Optional.of(professional));

        mockMvc.perform(get("/profissionais/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"nome\":\"Eduardo Neto\",\"cargo\":\"DESIGNER\",\"nascimento\":\"2003-04-10\",\"createdDate\":\"2024-10-08T20:31:51\"}"));
    }
    
    @Test
    public void testGetProfessionalById_NotFound() throws Exception {
        when(profissionalService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/profissionais/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteProfessional_Success() throws Exception {
        when(contatoService.existsByProfessionalId(1L)).thenReturn(false);
        doNothing().when(profissionalService).delete(1L);

        mockMvc.perform(delete("/profissionais/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Sucesso! Profissional excluído."));

        verify(profissionalService, Mockito.times(1)).delete(1L);
    }

    @Test
    public void testCreateProfissional_Success() throws Exception {
        Profissional professional = new Profissional();
        professional.setId(1L);
        professional.setNome("Eduardo Neto");
        professional.setCargo(Cargo.DESIGNER);
        professional.setNascimento(LocalDate.of(2003, 4, 10));
        professional.setCreatedDate(LocalDateTime.of(2024, 10, 8, 20, 31, 51));

        when(profissionalService.save(any(Profissional.class))).thenReturn(professional);

        String jsonBody = "{\"nome\":\"Eduardo Neto\",\"cargo\":\"DESIGNER\",\"nascimento\":\"2003-04-10\"}";

        mockMvc.perform(post("/profissionais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Sucesso! profissional com id 1 cadastrado"));
    }

    @Test
    public void testUpdateProfissional_Success() throws Exception {
        Profissional updateProfessional = new Profissional();
        updateProfessional.setId(1L);
        updateProfessional.setNome("Carlos Silva");
        updateProfessional.setCargo(Cargo.TESTER);
        updateProfessional.setNascimento(LocalDate.of(2002, 5, 15));
        updateProfessional.setCreatedDate(LocalDateTime.of(2024, 10, 8, 20, 31, 51));

        when(profissionalService.update(eq(1L), any(ProfissionalUpdateDTO.class))).thenReturn(updateProfessional);

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
                .when(profissionalService).update(eq(1L), any(ProfissionalUpdateDTO.class));

        String jsonBody = "{\"nome\":\"Carlos Silva\"}";

        mockMvc.perform(put("/profissionais/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllProfissionais_Success() throws Exception {
        List<ProfissionalDTO> professional = Arrays.asList(
                ProfissionalDTO.builder()
                        .id(1L)
                        .nome("Eduardo Neto")
                        .cargo(Cargo.DESENVOLVEDOR)
                        .build(),
                ProfissionalDTO.builder()
                        .id(2L)
                        .nome("Maria Souza")
                        .cargo(Cargo.SUPORTE)
                        .build()
        );

        when(profissionalService.findAll(null, null)).thenReturn(professional);

        mockMvc.perform(get("/profissionais"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"nome\":\"Eduardo Neto\",\"cargo\":\"DESENVOLVEDOR\"},{\"id\":2,\"nome\":\"Maria Souza\",\"cargo\":\"SUPORTE\"}]"));
    }


    @Test
    public void testGetAllProfissionais_WithFilters() throws Exception {
        List<ProfissionalDTO> professional = Arrays.asList(
            ProfissionalDTO.builder()
                .id(1L)
                .nome("Eduardo Neto")
                .build()
        );

        when(profissionalService.findAll("Eduardo", Arrays.asList("id", "nome"))).thenReturn(professional);

        mockMvc.perform(get("/profissionais")
                .param("q", "Eduardo")
                .param("fields", "id")
                .param("fields", "nome"))
            .andExpect(status().isOk())
            .andExpect(content().json("[{\"id\":1,\"nome\":\"Eduardo Neto\"}]"));
    }

}
