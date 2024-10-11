package com.api.simplesdental.service.contato;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.simplesdental.dto.contato.ContatoDTO;
import com.api.simplesdental.dto.contato.ContatoDTOFactory;
import com.api.simplesdental.exception.ResourceNotFoundException;
import com.api.simplesdental.model.contato.Contato;
import com.api.simplesdental.model.profissional.Profissional;
import com.api.simplesdental.repository.contato.ContatoRepository;
import com.api.simplesdental.repository.profissional.ProfissionalRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

@Service
public class ContatoService {

    @Autowired
    private ContatoRepository contatoRepository;
    
    @Autowired
    private ProfissionalRepository profissionalRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private static final List<String> validFields = Arrays.asList("id", "nome", "contato", "createdDate", "profissional");

    public List<ContatoDTO> findAll(String query, List<String> fields) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        List<String> filteredFields = fields != null 
            ? fields.stream().filter(validFields::contains).collect(Collectors.toList())
            : validFields; 

        final List<String> finalFilteredFields = filteredFields.isEmpty() ? validFields : filteredFields;

        CriteriaQuery<Object[]> cqDynamic = cb.createQuery(Object[].class);
        Root<Contato> root = cqDynamic.from(Contato.class);

        List<Selection<?>> selections = finalFilteredFields.stream()
                .map(field -> root.get(field))
                .collect(Collectors.toList());
        cqDynamic.multiselect(selections);

        if (query != null && !query.isEmpty()) {
            Predicate[] predicates = validFields.stream()
                .map(field -> {
                    if (field.equals("id")) {
                        try {
                            Long idValue = Long.parseLong(query);
                            return cb.equal(root.get("id"), idValue);
                        } catch (NumberFormatException e) {
                            return null; 
                        }
                    } else {
                        return cb.like(root.get(field).as(String.class), "%" + query + "%");
                    }
                })
                .filter(predicate -> predicate != null)
                .toArray(Predicate[]::new);

            cqDynamic.where(cb.or(predicates));
        }

        TypedQuery<Object[]> dynamicQuery = entityManager.createQuery(cqDynamic);
        List<Object[]> resultList = dynamicQuery.getResultList();

        return resultList.stream()
                .map(result -> ContatoDTOFactory.createContactDTO(result, finalFilteredFields))
                .collect(Collectors.toList());
    }




    public Optional<Contato> findById(Long id) {
        return contatoRepository.findById(id);
    }

    public Contato save(Contato contact) {
        Long professionalId = contact.getProfissional().getId();
        
        String number = contact.getContato();

        Profissional professional = profissionalRepository.findById(professionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado um profissional com o id " + professionalId));
        
        Optional<Contato> existingContact = contatoRepository.findByContato(number);
        if (existingContact.isPresent()) {
            throw new ResourceNotFoundException("Já existe um contato com o número informado");
        }

        contact.setProfissional(professional);

        return contatoRepository.save(contact);
    }

    public Contato update(Long id, Contato contactDetails) {
        String newNumber = contactDetails.getContato();
        String newName = contactDetails.getNome();

        Contato contact = contatoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado com id " + id));

        if (newNumber != null && !newNumber.trim().isEmpty() && !newNumber.equals(contact.getContato())) {
            contatoRepository.findByContato(newNumber).ifPresent(existingContact -> {
                if (!existingContact.getId().equals(id)) {
                    throw new ResourceNotFoundException("Já existe um contato com o número informado");
                }
            });
            contact.setContato(newNumber);
        }

        if (newName != null && !newName.trim().isEmpty() && !newName.equals(contact.getNome())) {
            contact.setNome(newName);
        }

        return contatoRepository.save(contact);
    }

    
    public boolean existsByProfissionalId(Long profissionalId) {
        String jpql = "SELECT COUNT(c) FROM Contato c WHERE c.profissional.id = :profissionalId";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("profissionalId", profissionalId);
        Long count = query.getSingleResult();
        return count > 0; 
    }

    public void delete(Long id) {
        Contato contact = contatoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado com id " + id));

        contatoRepository.delete(contact);
    }
}
