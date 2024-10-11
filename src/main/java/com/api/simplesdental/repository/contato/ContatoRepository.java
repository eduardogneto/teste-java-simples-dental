package com.api.simplesdental.repository.contato;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.simplesdental.model.contato.Contato;

public interface ContatoRepository extends JpaRepository<Contato, Long> {

	Optional<Contato> findByContato(String number);
}

