package com.chessanalysis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "games")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String gameId;
    
    @Column(nullable = false)
    private String platform; // LICHESS or CHESS_COM
    
    @Column(columnDefinition = "TEXT")
    private String pgn;
    
    private String result; // 1-0, 0-1, 1/2-1/2
    
    private String timeControl;
    
    private LocalDateTime playedAt;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToOne(mappedBy = "game", cascade = CascadeType.ALL)
    private Analysis analysis;
}
