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
import com.api.simplesdental.model.profissional.Profissional;
import com.api.simplesdental.repository.contato.ContatoRepository;
import com.api.simplesdental.repository.profissional.ProfissionalRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class ContatoServiceTest {

    @InjectMocks
    private ContatoService contatoService;

    @Mock
    private ContatoRepository contatoRepository;
    
    @Mock
    private ProfissionalRepository profissionalRepository;

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
        Contato contact = new Contato();
        contact.setId(1L);
        contact.setNome("João Silva");

        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contact));

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
        Contato contact = new Contato();
        contact.setId(1L);
        contact.setNome("João Silva");
        contact.setContato("123456789"); 

        Profissional professional = new Profissional();
        professional.setId(2L);
        professional.setNome("Maria Souza");

        contact.setProfissional(professional);

        when(profissionalRepository.findById(2L)).thenReturn(Optional.of(professional));
        when(contatoRepository.findByContato("123456789")).thenReturn(Optional.empty());
        when(contatoRepository.save(any(Contato.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Contato savedContato = contatoService.save(contact);

        assertNotNull(savedContato);
        assertEquals("João Silva", savedContato.getNome());
        assertEquals("123456789", savedContato.getContato());
        assertNotNull(savedContato.getProfissional());
        assertEquals(2L, savedContato.getProfissional().getId());
    }

    @Test
    public void testUpdate_Success() {
        Contato existContact = new Contato();
        existContact.setId(1L);
        existContact.setNome("João Silva");

        Contato updateContact = new Contato();
        updateContact.setNome("Carlos Eduardo");

        when(contatoRepository.findById(1L)).thenReturn(Optional.of(existContact));
        when(contatoRepository.save(any(Contato.class))).thenReturn(existContact);

        Contato result = contatoService.update(1L, updateContact);
        assertNotNull(result);
        assertEquals("Carlos Eduardo", result.getNome());
    }

    @Test
    public void testUpdate_NotFound() {
        Contato updateContact = new Contato();
        updateContact.setNome("Carlos Eduardo");

        when(contatoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            contatoService.update(1L, updateContact);
        });
    }

    @Test
    public void testExistsByProfessionalId_Success() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), anyLong())).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(2L);  

        boolean exists = contatoService.existsByProfessionalId(1L);
        assertTrue(exists);
    }

    @Test
    public void testExistsByProfessionalId_NotFound() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), anyLong())).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(0L);  

        boolean exists = contatoService.existsByProfessionalId(1L);
        assertFalse(exists);
    }

    @Test
    public void testDelete_Success() {
        Contato contact = new Contato();
        contact.setId(1L);
        contact.setNome("João Silva");

        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contact));
        doNothing().when(contatoRepository).delete(contact);

        contatoService.delete(1L);
        verify(contatoRepository, times(1)).delete(contact);
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

