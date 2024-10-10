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

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.api.simplesdental.exception.ResourceNotFoundException;
import com.api.simplesdental.model.profissional.Profissional;
import com.api.simplesdental.repository.profissional.ProfissionalRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class ProfissionalServiceTest {

    @InjectMocks
    private ProfissionalService profissionalService;

    @Mock
    private ProfissionalRepository profissionalRepository;

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

        Profissional profissionalAtualizado = new Profissional();
        profissionalAtualizado.setNome("Carlos Silva");

        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissionalExistente));
        when(profissionalRepository.save(any(Profissional.class))).thenReturn(profissionalExistente);

        Profissional result = profissionalService.update(1L, profissionalAtualizado);
        assertNotNull(result);
        assertEquals("Carlos Silva", result.getNome());
    }

    @Test
    public void testUpdate_NotFound() {
        Profissional profissionalAtualizado = new Profissional();
        profissionalAtualizado.setNome("Carlos Silva");

        when(profissionalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            profissionalService.update(1L, profissionalAtualizado);
        });
    }

    @Test
    public void testDelete_Success() {
        Profissional profissional = new Profissional();
        profissional.setId(1L);
        profissional.setNome("Eduardo Neto");

        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        doNothing().when(profissionalRepository).delete(profissional);

        profissionalService.delete(1L);
        verify(profissionalRepository, times(1)).delete(profissional);
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

