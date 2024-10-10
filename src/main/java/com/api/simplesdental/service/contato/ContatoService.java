package com.api.simplesdental.service.contato;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public List<Object> findAll(String query, List<String> fields) {
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

        return resultList.stream().map(result -> {
            Map<String, Object> dtoMap = new HashMap<>();
            
            if (finalFilteredFields.contains("id")) {
                dtoMap.put("id", result[finalFilteredFields.indexOf("id")]);
            }
            if (finalFilteredFields.contains("nome")) {
                dtoMap.put("nome", result[finalFilteredFields.indexOf("nome")]);
            }
            if (finalFilteredFields.contains("contato")) {
            	dtoMap.put("contato", result[finalFilteredFields.indexOf("contato")]);
            }
            if (finalFilteredFields.contains("createdDate")) {
                dtoMap.put("createdDate", result[finalFilteredFields.indexOf("createdDate")]);
            }
            if (finalFilteredFields.contains("profissional")) {
                Profissional professional = (Profissional) result[finalFilteredFields.indexOf("profissional")];
                if (professional != null) {
                    Map<String, Object> professionalMap = new HashMap<>();
                    professionalMap.put("id", professional.getId());
                    professionalMap.put("nome", professional.getNome());
                    professionalMap.put("cargo", professional.getCargo());
                    professionalMap.put("nascimento", professional.getNascimento());
                    professionalMap.put("createdDate", professional.getCreatedDate());
                    dtoMap.put("profissional", professionalMap);
                }
            }
            return dtoMap;
        }).collect(Collectors.toList());
    }



    public Optional<Contato> findById(Long id) {
        return contatoRepository.findById(id);
    }

    public Contato save(Contato contato) {
        Long professionalId = contato.getProfissional().getId();
        
        String number = contato.getContato();

        Profissional professional = profissionalRepository.findById(professionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado um profissional com o id " + professionalId));
        
        Optional<Contato> existingContato = contatoRepository.findByContato(number);
        if (existingContato.isPresent()) {
            throw new ResourceNotFoundException("Já existe um contato com o número informado");
        }

        contato.setProfissional(professional);

        return contatoRepository.save(contato);
    }

    public Contato update(Long id, Contato contatoDetails) {
        String newNumber = contatoDetails.getContato();
        String newName = contatoDetails.getNome();

        Contato contact = contatoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado com id " + id));

        if (newNumber != null && !newNumber.trim().isEmpty() && !newNumber.equals(contact.getContato())) {
            contatoRepository.findByContato(newNumber).ifPresent(existingContato -> {
                if (!existingContato.getId().equals(id)) {
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
        Contato contato = contatoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado com id " + id));

        contatoRepository.delete(contato);
    }
}
