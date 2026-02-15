package com.chessanalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResponse {

    private Long userId;
    private String username;
    private String platform;
    private Integer totalGamesAnalyzed;
    
    // AI Insights
    private String playingStyle;
    private String strengths;
    private String weaknesses;
    private String commonMistakes;
    private String recommendations;
    private String openingAnalysis;
    
    // Ratings
    private String tacticalRating;
    private String positionalRating;
    private String endgameRating;
    private String timeManagementRating;
    
    // Statistics
    private Map<String, Object> statistics;
    private Map<String, Object> openingStats;
    private Map<String, Object> performanceByTimeControl;
    
    private LocalDateTime generatedAt;
    
    // Progress tracking
    private String analysisId;
    private String status; // "processing", "completed", "failed"
    private Integer progress; // 0-100
}
