package com.api.simplesdental.service.profissional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.simplesdental.dto.profissional.ProfissionalDTO;
import com.api.simplesdental.dto.profissional.ProfissionalDTOFactory;
import com.api.simplesdental.dto.profissional.ProfissionalUpdateDTO;
import com.api.simplesdental.enums.profissional.Cargo;
import com.api.simplesdental.exception.ResourceNotFoundException;
import com.api.simplesdental.model.profissional.Profissional;
import com.api.simplesdental.repository.profissional.ProfissionalRepository;
import com.api.simplesdental.service.contato.ContatoService;

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
    
    @Autowired
    private ContatoService contatoService;

    @PersistenceContext
    private EntityManager entityManager;

    private static final List<String> validFields = Arrays.asList("id", "cargo", "nascimento", "createdDate", "nome");

    public List<ProfissionalDTO> findAll(String query, List<String> fields) {
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

        return resultList.stream()
                .map(result -> ProfissionalDTOFactory.createProfessionalDTO(result, finalFilteredFields))
                .collect(Collectors.toList());
    }


    public Optional<Profissional> findById(Long id) {
        return profissionalRepository.findById(id);
    }

    public Profissional save(Profissional profissional) {
        return profissionalRepository.save(profissional);
    }

    public Profissional update(Long id, ProfissionalUpdateDTO profissionalDTO) {
        Profissional profissional = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com id " + id));
        
        LocalDate newBirth = profissionalDTO.getNascimento();
        String newName = profissionalDTO.getNome();
        Cargo newRole = profissionalDTO.getCargo();

        if (newName != null && !newName.trim().isEmpty() && !newName.equals(profissional.getNome())) {
            profissional.setNome(newName);
        }
        if (newRole != null && (newRole != profissional.getCargo())) {
            profissional.setCargo(newRole);
        }
        if (newBirth != null && (newBirth != profissional.getNascimento())) {
            profissional.setNascimento(newBirth);
        }
        
        return profissionalRepository.save(profissional);
    }


    public void delete(Long id) {
        Profissional profissional = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com id " + id));
        
        if (contatoService.existsByProfissionalId(id)) {
            throw new ResourceNotFoundException("Não é possível excluir o profissional, pois ele possui contatos vinculados.");
        }

        profissionalRepository.delete(profissional);
    }
}
