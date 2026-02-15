package com.chessanalysis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "games")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String platform; // "lichess" or "chesscom"

    @Column(name = "game_id", nullable = false)
    private String gameId; // Platform's game ID

    @Column(columnDefinition = "TEXT")
    private String pgn;

    @Column(name = "time_control")
    private String timeControl;

    @Column(nullable = false)
    private String result; // "win", "loss", "draw"

    @Column(name = "white_rating")
    private Integer whiteRating;

    @Column(name = "black_rating")
    private Integer blackRating;

    @Column(name = "played_as")
    private String playedAs; // "white" or "black"

    @Column(name = "played_at")
    private LocalDateTime playedAt;

    @OneToOne(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private Analysis analysis;
}
