package com.chessanalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResponse {
    private Long userId;
    private String username;
    private String platform;
    private Integer totalGames;
    private Integer wins;
    private Integer losses;
    private Integer draws;
    private Double avgAccuracy;
    private Integer totalBlunders;
    private Integer totalMistakes;
    private Integer totalInaccuracies;
    private List<String> topOpenings;
    private Map<String, Long> timeControlDistribution;
    private String playingStyle;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> commonMistakes;
    private List<String> recommendations;
    private String openingAnalysis;
}
