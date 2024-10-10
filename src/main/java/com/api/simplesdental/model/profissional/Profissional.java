package com.api.simplesdental.model.profissional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.api.simplesdental.enums.profissional.Cargo;
import com.api.simplesdental.model.contato.Contato;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profissionais")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profissional {
	
	private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cargo cargo;
    
    @Column(nullable = false)
    private LocalDate nascimento;
    
    @OneToMany(mappedBy = "profissional", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Contato> contatos;
    
    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }
    
    public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
}
