package com.api.simplesdental.controller.contato;

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

import com.api.simplesdental.exception.ResourceNotFoundException;
import com.api.simplesdental.model.contato.Contato;
import com.api.simplesdental.service.contato.ContatoService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/contatos")
public class ContatoController {

    @Autowired
    private ContatoService contatoService;

    @GetMapping
    @Operation(summary = "Lista de contatos com filtros opcionais por parametro")
    public ResponseEntity<List<Object>> getAllContatos(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "fields", required = false) List<String> fields) {
        List<Object> contacts = contatoService.findAll(query, fields);
        return ResponseEntity.ok(contacts);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Buscar contato por ID de parametro")
    public ResponseEntity<Contato> getContatoById(@PathVariable Long id) {
        Contato contato = contatoService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado com id " + id));
        return ResponseEntity.ok(contato);
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo contato")
    public ResponseEntity<String> createContato(@RequestBody Contato contato) {
        Contato save = contatoService.save(contato);
        return ResponseEntity.ok("Sucesso contato com id " + save.getId() + " cadastrado");
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar contato existente utilizando id como parametro")
    public ResponseEntity<String> updateContato(@PathVariable Long id, @RequestBody Contato contatoDetails) {
        contatoService.update(id, contatoDetails);
        return ResponseEntity.ok("Sucesso cadastrado alterado");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclusão de contato por id")
    public ResponseEntity<String> deleteContato(@PathVariable Long id) {
        contatoService.delete(id);
        return ResponseEntity.ok("Sucesso contato excluído");
    }
}
