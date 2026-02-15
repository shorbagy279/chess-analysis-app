package com.chessanalysis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String lichessUsername;
    
    private String chesscomUsername;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Game> games;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime lastAnalyzedAt;
}
