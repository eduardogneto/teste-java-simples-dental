package com.api.simplesdental.repository.contato;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.api.simplesdental.model.contato.Contato;

public interface ContatoRepository extends JpaRepository<Contato, Long> {
}

