package com.chessanalysis.service;

import com.chessanalysis.dto.AnalysisRequest;
import com.chessanalysis.dto.AnalysisResponse;
import com.chessanalysis.model.*;
import com.chessanalysis.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final AnalysisRepository analysisRepository;
    private final AIInsightRepository aiInsightRepository;
    
    private final LichessApiService lichessApiService;
    private final ChesscomApiService chesscomApiService;
    private final StockfishService stockfishService;
    private final ClaudeAIService claudeAIService;

    @Transactional
    public AnalysisResponse analyzePlayer(AnalysisRequest request) {
        log.info("Starting analysis for user: {} on platform: {}", 
                request.getUsername(), request.getPlatform());

        // Get or create user
        User user = getOrCreateUser(request);

        // Fetch games
        List<Game> games = fetchGames(user, request);
        
        if (games.isEmpty()) {
            throw new RuntimeException("No games found for user: " + request.getUsername());
        }

        // Analyze games with Stockfish
        List<Analysis> analyses = analyzeGames(games);

        // Generate AI insights with Claude
        AIInsight aiInsight = claudeAIService.generateInsights(user, games, analyses);
        aiInsightRepository.save(aiInsight);

        // Update user
        user.setLastAnalyzed(LocalDateTime.now());
        userRepository.save(user);

        // Build response
        return buildAnalysisResponse(user, games, analyses, aiInsight);
    }

    private User getOrCreateUser(AnalysisRequest request) {
        if ("lichess".equals(request.getPlatform())) {
            return userRepository.findByLichessUsername(request.getUsername())
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setLichessUsername(request.getUsername());
                        return userRepository.save(newUser);
                    });
        } else {
            return userRepository.findByChesscomUsername(request.getUsername())
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setChesscomUsername(request.getUsername());
                        return userRepository.save(newUser);
                    });
        }
    }

    private List<Game> fetchGames(User user, AnalysisRequest request) {
        List<Game> games;
        
        if ("lichess".equals(request.getPlatform())) {
            games = lichessApiService.fetchGames(user, request.getGameCount());
        } else {
            games = chesscomApiService.fetchGames(user, request.getGameCount());
        }

        // Save games to database
        for (Game game : games) {
            if (!gameRepository.existsByUserAndPlatformAndGameId(
                    user, game.getPlatform(), game.getGameId())) {
                gameRepository.save(game);
            }
        }

        return games;
    }

    private List<Analysis> analyzeGames(List<Game> games) {
        List<Analysis> analyses = new ArrayList<>();
        
        int count = 0;
        for (Game game : games) {
            try {
                log.info("Analyzing game {}/{}", ++count, games.size());
                
                Analysis analysis = stockfishService.analyzeGame(game);
                analysisRepository.save(analysis);
                analyses.add(analysis);
                
            } catch (Exception e) {
                log.error("Error analyzing game {}: {}", game.getGameId(), e.getMessage());
            }
        }
        
        return analyses;
    }

    private AnalysisResponse buildAnalysisResponse(User user, List<Game> games, 
                                                    List<Analysis> analyses, AIInsight aiInsight) {
        
        // Calculate statistics
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalGames", games.size());
        statistics.put("wins", games.stream().filter(g -> "win".equals(g.getResult())).count());
        statistics.put("losses", games.stream().filter(g -> "loss".equals(g.getResult())).count());
        statistics.put("draws", games.stream().filter(g -> "draw".equals(g.getResult())).count());
        
        double avgAccuracy = analyses.stream()
                .map(Analysis::getAccuracy)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0.0);
        statistics.put("averageAccuracy", avgAccuracy);
        
        int totalBlunders = analyses.stream().mapToInt(Analysis::getBlunders).sum();
        int totalMistakes = analyses.stream().mapToInt(Analysis::getMistakes).sum();
        int totalInaccuracies = analyses.stream().mapToInt(Analysis::getInaccuracies).sum();
        
        statistics.put("totalBlunders", totalBlunders);
        statistics.put("totalMistakes", totalMistakes);
        statistics.put("totalInaccuracies", totalInaccuracies);
        statistics.put("avgBlundersPerGame", totalBlunders * 1.0 / games.size());
        statistics.put("avgMistakesPerGame", totalMistakes * 1.0 / games.size());

        // Opening statistics
        Map<String, Long> openingStats = analyses.stream()
                .filter(a -> a.getOpeningName() != null)
                .collect(Collectors.groupingBy(Analysis::getOpeningName, Collectors.counting()));
        
        Map<String, Object> openingStatsMap = new HashMap<>();
        openingStats.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> openingStatsMap.put(entry.getKey(), entry.getValue()));

        // Performance by time control
        Map<String, Long> timeControlPerf = games.stream()
                .collect(Collectors.groupingBy(Game::getTimeControl, Collectors.counting()));

        return AnalysisResponse.builder()
                .userId(user.getId())
                .username(user.getLichessUsername() != null ? 
                        user.getLichessUsername() : user.getChesscomUsername())
                .platform(games.get(0).getPlatform())
                .totalGamesAnalyzed(games.size())
                .playingStyle(aiInsight.getPlayingStyle())
                .strengths(aiInsight.getStrengths())
                .weaknesses(aiInsight.getWeaknesses())
                .commonMistakes(aiInsight.getCommonMistakes())
                .recommendations(aiInsight.getRecommendations())
                .openingAnalysis(aiInsight.getOpeningAnalysis())
                .tacticalRating(aiInsight.getTacticalRating())
                .positionalRating(aiInsight.getPositionalRating())
                .endgameRating(aiInsight.getEndgameRating())
                .timeManagementRating(aiInsight.getTimeManagementRating())
                .statistics(statistics)
                .openingStats(openingStatsMap)
                .performanceByTimeControl(new HashMap<>(timeControlPerf))
                .generatedAt(aiInsight.getGeneratedAt())
                .status("completed")
                .progress(100)
                .build();
    }

    public AnalysisResponse getLatestAnalysis(String username, String platform) {
        User user;
        
        if ("lichess".equals(platform)) {
            user = userRepository.findByLichessUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            user = userRepository.findByChesscomUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        AIInsight latestInsight = aiInsightRepository
                .findFirstByUserOrderByGeneratedAtDesc(user)
                .orElseThrow(() -> new RuntimeException("No analysis found for user"));

        List<Game> games = gameRepository.findByUserOrderByPlayedAtDesc(user);
        List<Analysis> analyses = games.stream()
                .map(game -> analysisRepository.findByGame(game).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return buildAnalysisResponse(user, games, analyses, latestInsight);
    }
}
