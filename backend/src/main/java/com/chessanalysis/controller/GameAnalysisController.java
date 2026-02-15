package com.chessanalysis.controller;

import com.chessanalysis.dto.GameAnalysisRequest;
import com.chessanalysis.dto.GameAnalysisResponse;
import com.chessanalysis.service.GameAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analysis/game")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class GameAnalysisController {

    private final GameAnalysisService gameAnalysisService;

    @PostMapping
    public ResponseEntity<?> analyzeGame(@Valid @RequestBody GameAnalysisRequest request) {
        try {
            log.info("Received game analysis request");
            
            GameAnalysisResponse response = gameAnalysisService.analyzeGame(request.getPgn());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error during game analysis: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}