package com.api.simplesdental.model.contato;

import java.time.LocalDateTime;

import com.api.simplesdental.model.profissional.Profissional;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contatos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome; 

    @Column(nullable = false)
    private String contato;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profissional_id", nullable = false)
    private Profissional profissional;

    @PrePersist
    public void prePersist() {
        createdDate = LocalDateTime.now();
    }
}
