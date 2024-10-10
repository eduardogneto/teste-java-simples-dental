package com.api.simplesdental.controller.profissional;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.api.simplesdental.exception.ResourceNotFoundException;
import com.api.simplesdental.model.profissional.Profissional;
import com.api.simplesdental.service.contato.ContatoService;
import com.api.simplesdental.service.profissional.ProfissionalService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalController {

    @Autowired
    private ProfissionalService profissionalService;
    
    @Autowired
    private ContatoService contatoService;

    @GetMapping
    @Operation(summary = "Lista de contatos com filtros opcionais passando parametros")
    public ResponseEntity<List<Object>> getAllProfissionais(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "fields", required = false) List<String> fields) {
        List<Object> professional = profissionalService.findAll(query, fields);
        return ResponseEntity.ok(professional);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter profissional por id de parametro")
    public ResponseEntity<Profissional> getProfissionalById(@PathVariable Long id) {
        Profissional professional = profissionalService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com id " + id));
        return ResponseEntity.ok(professional);
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo profissional")
    public ResponseEntity<String> createProfissional(@RequestBody Profissional profissional) {
        Profissional save = profissionalService.save(profissional);
        return ResponseEntity.ok("Sucesso! profissional com id " + save.getId() + " cadastrado");
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar profissional existente utilizando o id")
    public ResponseEntity<String> updateProfissional(@PathVariable Long id, @RequestBody Profissional profissionalDetails) {
        profissionalService.update(id, profissionalDetails);
        return ResponseEntity.ok("Sucesso! cadastrado alterado");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclusão de profissional por id, com validação se o mesmo tem contatos vinculados")
    public ResponseEntity<String> deleteProfissional(@PathVariable Long id) {
        if (contatoService.existsByProfissionalId(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Não é possível excluir o profissional, pois ele possui contatos vinculados.");
        }

        profissionalService.delete(id);
        return ResponseEntity.ok("Sucesso! Profissional excluído.");
    }
}
