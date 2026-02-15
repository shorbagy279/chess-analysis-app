package com.chessanalysis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "analyses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Analysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
    
    private BigDecimal avgCentipawnLoss;
    
    private BigDecimal accuracy;
    
    private Integer blunders;
    
    private Integer mistakes;
    
    private Integer inaccuracies;
    
    private Integer bestMoves;
    
    private String openingName;
    
    private String openingEco;
    
    private LocalDateTime analyzedAt;
}
