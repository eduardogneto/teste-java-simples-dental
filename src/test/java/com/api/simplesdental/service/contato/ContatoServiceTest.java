package com.api.simplesdental.service.contato;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import com.api.simplesdental.model.contato.Contato;
import com.api.simplesdental.repository.contato.ContatoRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class ContatoServiceTest {

    @InjectMocks
    private ContatoService contatoService;

    @Mock
    private ContatoRepository contatoRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Long> typedQuery;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindById_Success() {
        Contato contato = new Contato();
        contato.setId(1L);
        contato.setNome("João Silva");

        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contato));

        Optional<Contato> result = contatoService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("João Silva", result.get().getNome());
    }

    @Test
    public void testFindById_NotFound() {
        when(contatoRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Contato> result = contatoService.findById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    public void testSave_Success() {
        Contato contato = new Contato();
        contato.setId(1L);
        contato.setNome("João Silva");

        when(contatoRepository.save(any(Contato.class))).thenReturn(contato);

        Contato savedContato = contatoService.save(contato);
        assertNotNull(savedContato);
        assertEquals("João Silva", savedContato.getNome());
    }

    @Test
    public void testUpdate_Success() {
        Contato contatoExistente = new Contato();
        contatoExistente.setId(1L);
        contatoExistente.setNome("João Silva");

        Contato contatoAtualizado = new Contato();
        contatoAtualizado.setNome("Carlos Eduardo");

        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contatoExistente));
        when(contatoRepository.save(any(Contato.class))).thenReturn(contatoExistente);

        Contato result = contatoService.update(1L, contatoAtualizado);
        assertNotNull(result);
        assertEquals("Carlos Eduardo", result.getNome());
    }

    @Test
    public void testUpdate_NotFound() {
        Contato contatoAtualizado = new Contato();
        contatoAtualizado.setNome("Carlos Eduardo");

        when(contatoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            contatoService.update(1L, contatoAtualizado);
        });
    }

    @Test
    public void testExistsByProfissionalId_Success() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), anyLong())).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(2L);  

        boolean exists = contatoService.existsByProfissionalId(1L);
        assertTrue(exists);
    }

    @Test
    public void testExistsByProfissionalId_NotFound() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), anyLong())).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(0L);  

        boolean exists = contatoService.existsByProfissionalId(1L);
        assertFalse(exists);
    }

    @Test
    public void testDelete_Success() {
        Contato contato = new Contato();
        contato.setId(1L);
        contato.setNome("João Silva");

        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contato));
        doNothing().when(contatoRepository).delete(contato);

        contatoService.delete(1L);
        verify(contatoRepository, times(1)).delete(contato);
    }

    @Test
    public void testDelete_NotFound() {
        when(contatoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            contatoService.delete(1L);
        });

        verify(contatoRepository, never()).delete(any(Contato.class));
    }
}

