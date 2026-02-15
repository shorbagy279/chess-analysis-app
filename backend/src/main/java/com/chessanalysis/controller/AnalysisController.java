package com.chessanalysis.controller;

import com.chessanalysis.dto.AnalysisRequest;
import com.chessanalysis.dto.AnalysisResponse;
import com.chessanalysis.service.AnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping("/start")
    public ResponseEntity<?> startAnalysis(@Valid @RequestBody AnalysisRequest request) {
        try {
            log.info("Received analysis request for user: {} on platform: {}", 
                    request.getUsername(), request.getPlatform());
            
            AnalysisResponse response = analysisService.analyzePlayer(request);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error during analysis: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getLatestAnalysis(
            @PathVariable String username,
            @RequestParam String platform) {
        try {
            AnalysisResponse response = analysisService.getLatestAnalysis(username, platform);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error retrieving analysis: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Analysis not found for user: " + username));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Chess Analysis API",
                "timestamp", System.currentTimeMillis()
        ));
    }
}
