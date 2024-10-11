package com.api.simplesdental.service.profissional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.api.simplesdental.dto.profissional.ProfissionalUpdateDTO;
import com.api.simplesdental.enums.profissional.Cargo;
import com.api.simplesdental.exception.ResourceNotFoundException;
import com.api.simplesdental.model.profissional.Profissional;
import com.api.simplesdental.repository.profissional.ProfissionalRepository;
import com.api.simplesdental.service.contato.ContatoService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class ProfissionalServiceTest {

    @InjectMocks
    private ProfissionalService profissionalService;

    @Mock
    private ProfissionalRepository profissionalRepository;
    
    @Mock
    private ContatoService contatoService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Object[]> typedQuery;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindById_Success() {
        Profissional profissional = new Profissional();
        profissional.setId(1L);
        profissional.setNome("Eduardo Neto");

        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));

        Optional<Profissional> result = profissionalService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Eduardo Neto", result.get().getNome());
    }

    @Test
    public void testFindById_NotFound() {
        when(profissionalRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Profissional> result = profissionalService.findById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    public void testSave_Success() {
        Profissional profissional = new Profissional();
        profissional.setId(1L);
        profissional.setNome("Eduardo Neto");

        when(profissionalRepository.save(any(Profissional.class))).thenReturn(profissional);

        Profissional savedProfissional = profissionalService.save(profissional);
        assertNotNull(savedProfissional);
        assertEquals("Eduardo Neto", savedProfissional.getNome());
    }

    @Test
    public void testUpdate_Success() {
        Profissional profissionalExistente = new Profissional();
        profissionalExistente.setId(1L);
        profissionalExistente.setNome("Eduardo Neto");
        profissionalExistente.setCargo(Cargo.DESENVOLVEDOR);
        profissionalExistente.setNascimento(LocalDate.of(2000, 1, 1));

        ProfissionalUpdateDTO profissionalDTO = new ProfissionalUpdateDTO();
        profissionalDTO.setNome("Carlos Silva");
        profissionalDTO.setCargo(Cargo.TESTER);
        profissionalDTO.setNascimento(LocalDate.of(1995, 5, 15));

        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissionalExistente));
        when(profissionalRepository.save(any(Profissional.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Profissional result = profissionalService.update(1L, profissionalDTO);

        assertNotNull(result);
        assertEquals("Carlos Silva", result.getNome());
        assertEquals(Cargo.TESTER, result.getCargo());
        assertEquals(LocalDate.of(1995, 5, 15), result.getNascimento());
    }

    @Test
    public void testUpdate_NotFound() {
        ProfissionalUpdateDTO profissionalDTO = new ProfissionalUpdateDTO();
        profissionalDTO.setNome("Carlos Silva");

        when(profissionalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            profissionalService.update(1L, profissionalDTO);
        });
    }

    @Test
    public void testDelete_Success() {
        Profissional profissional = new Profissional();
        profissional.setId(1L);
        profissional.setNome("Eduardo Neto");

        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(contatoService.existsByProfissionalId(1L)).thenReturn(false);
        doNothing().when(profissionalRepository).delete(profissional);

        profissionalService.delete(1L);

        verify(profissionalRepository, times(1)).delete(profissional);
        verify(contatoService, times(1)).existsByProfissionalId(1L);
    }


    @Test
    public void testDelete_NotFound() {
        when(profissionalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            profissionalService.delete(1L);
        });

        verify(profissionalRepository, never()).delete(any(Profissional.class));
    }
}

