package com.chessanalysis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "avg_centipawn_loss", precision = 5, scale = 2)
    private BigDecimal avgCentipawnLoss;

    @Column(precision = 5, scale = 2)
    private BigDecimal accuracy;

    @Column(nullable = false)
    private Integer blunders = 0;

    @Column(nullable = false)
    private Integer mistakes = 0;

    @Column(nullable = false)
    private Integer inaccuracies = 0;

    @Column(name = "best_moves", nullable = false)
    private Integer bestMoves = 0;

    @Column(name = "opening_name", length = 200)
    private String openingName;

    @Column(name = "opening_eco", length = 10)
    private String openingEco; // ECO code like "E00"

    @Column(name = "avg_move_time", precision = 5, scale = 2)
    private BigDecimal avgMoveTime; // in seconds
}
