package com.api.simplesdental.controller.profissional;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.simplesdental.dto.profissional.ProfissionalDTO;
import com.api.simplesdental.dto.profissional.ProfissionalDTOFactory;
import com.api.simplesdental.dto.profissional.ProfissionalUpdateDTO;
import com.api.simplesdental.exception.ResourceNotFoundException;
import com.api.simplesdental.model.profissional.Profissional;
import com.api.simplesdental.service.profissional.ProfissionalService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalController {

    @Autowired
    private ProfissionalService profissionalService;
    
    @GetMapping
    @Operation(summary = "Lista de contatos com filtros opcionais passando parâmetros")
    public ResponseEntity<List<ProfissionalDTO>> getAllProfessionals(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "fields", required = false) List<String> fields) {
        List<ProfissionalDTO> professional = profissionalService.findAll(query, fields);
        return ResponseEntity.ok(professional);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Obter profissional por id de parâmetro")
    public ResponseEntity<ProfissionalDTO> getProfessionalById(@PathVariable Long id) {
        Profissional professional = profissionalService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com id " + id));
        ProfissionalDTO professionalDTO = ProfissionalDTOFactory.createProfessionalFromDTO(professional);
        return ResponseEntity.ok(professionalDTO);
    }


    @PostMapping
    @Operation(summary = "Cadastrar novo profissional")
    public ResponseEntity<String> createProfessional(@RequestBody Profissional professional) {
        Profissional save = profissionalService.save(professional);
        return ResponseEntity.ok("Sucesso! profissional com id " + save.getId() + " cadastrado");
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar profissional existente utilizando o id")
    public ResponseEntity<String> updateProfessional(@PathVariable Long id, @Valid @RequestBody ProfissionalUpdateDTO professionalDTO) {
        profissionalService.update(id, professionalDTO);
        return ResponseEntity.ok("Sucesso! cadastrado alterado");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclusão de profissional por id, com validação se o mesmo tem contatos vinculados")
    public ResponseEntity<String> deleteProfessional(@PathVariable Long id) {
        profissionalService.delete(id);
        return ResponseEntity.ok("Sucesso! Profissional excluído.");
    }

}
