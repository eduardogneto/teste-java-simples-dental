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

import com.api.simplesdental.dto.contato.ContatoDTO;
import com.api.simplesdental.dto.contato.ContatoDTOFactory;
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
    @Operation(summary = "Lista de contatos com filtros opcionais por parâmetro")
    public ResponseEntity<List<ContatoDTO>> getAllContacts(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "fields", required = false) List<String> fields) {
        List<ContatoDTO> contacts = contatoService.findAll(query, fields);
        return ResponseEntity.ok(contacts);
    }



    @GetMapping("/{id}")
    @Operation(summary = "Buscar contato por ID de parâmetro")
    public ResponseEntity<ContatoDTO> getContactById(@PathVariable Long id) {
        Contato contact = contatoService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado com id " + id));
        ContatoDTO contactDTO = ContatoDTOFactory.createFromDTO(contact);
        return ResponseEntity.ok(contactDTO);
    }


    @PostMapping
    @Operation(summary = "Cadastrar novo contato")
    public ResponseEntity<String> createContact(@RequestBody Contato contact) {
        Contato save = contatoService.save(contact);
        return ResponseEntity.ok("Sucesso contato com id " + save.getId() + " cadastrado");
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar contato existente utilizando id como parametro")
    public ResponseEntity<String> updateContact(@PathVariable Long id, @RequestBody Contato contactDetails) {
        contatoService.update(id, contactDetails);
        return ResponseEntity.ok("Sucesso cadastrado alterado");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclusão de contato por id")
    public ResponseEntity<String> deleteContact(@PathVariable Long id) {
        contatoService.delete(id);
        return ResponseEntity.ok("Sucesso contato excluído");
    }
}
