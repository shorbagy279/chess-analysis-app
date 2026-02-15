package com.chessanalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisRequest {
    private String username;
    private String platform; // LICHESS or CHESS_COM
    private Integer numberOfGames;
    
    // Alternative name for backward compatibility
    public Integer getGameCount() {
        return numberOfGames;
    }
    
    public void setGameCount(Integer gameCount) {
        this.numberOfGames = gameCount;
    }
}
