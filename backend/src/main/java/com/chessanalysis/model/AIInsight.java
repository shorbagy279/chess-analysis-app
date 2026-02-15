package com.chessanalysis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_insights")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIInsight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "playing_style", columnDefinition = "TEXT")
    private String playingStyle;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @Column(name = "common_mistakes", columnDefinition = "TEXT")
    private String commonMistakes;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "opening_analysis", columnDefinition = "TEXT")
    private String openingAnalysis;

    @Column(name = "tactical_rating")
    private String tacticalRating; // "Excellent", "Good", "Average", "Needs Work"

    @Column(name = "positional_rating")
    private String positionalRating;

    @Column(name = "endgame_rating")
    private String endgameRating;

    @Column(name = "time_management_rating")
    private String timeManagementRating;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
}
