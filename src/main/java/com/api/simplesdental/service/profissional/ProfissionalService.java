package com.api.simplesdental.service.profissional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.simplesdental.exception.ResourceNotFoundException;
import com.api.simplesdental.model.profissional.Profissional;
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
public class ProfissionalService {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private static final List<String> validFields = Arrays.asList("id", "cargo", "nascimento", "createdDate", "nome");

    public List<Object> findAll(String query, List<String> fields) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        List<String> filteredFields = fields != null 
            ? fields.stream().filter(validFields::contains).collect(Collectors.toList())
            : validFields; 

        final List<String> finalFilteredFields = filteredFields.isEmpty() ? validFields : filteredFields;

        CriteriaQuery<Object[]> cqDynamic = cb.createQuery(Object[].class);
        Root<Profissional> root = cqDynamic.from(Profissional.class);

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
            if (finalFilteredFields.contains("nascimento")) {
            	dtoMap.put("nascimento", result[finalFilteredFields.indexOf("nascimento")]);
            }
            if (finalFilteredFields.contains("createdDate")) {
                dtoMap.put("createdDate", result[finalFilteredFields.indexOf("createdDate")]);
            }
            if (finalFilteredFields.contains("cargo")) {
                dtoMap.put("cargo", result[finalFilteredFields.indexOf("cargo")]);
            }
            return dtoMap;
        }).collect(Collectors.toList());
    }

    public Optional<Profissional> findById(Long id) {
        return profissionalRepository.findById(id);
    }

    public Profissional save(Profissional profissional) {
        return profissionalRepository.save(profissional);
    }

    public Profissional update(Long id, Profissional profissionalDetails) {
        Profissional profissional = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com id " + id));

        profissional.setNome(profissionalDetails.getNome());
        profissional.setCargo(profissionalDetails.getCargo());
        profissional.setNascimento(profissionalDetails.getNascimento());

        return profissionalRepository.save(profissional);
    }

    public void delete(Long id) {
        Profissional profissional = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com id " + id));

        profissionalRepository.delete(profissional);
    }
}
