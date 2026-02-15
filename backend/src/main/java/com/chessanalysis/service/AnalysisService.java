package com.chessanalysis.service;

import com.chessanalysis.dto.AnalysisRequest;
import com.chessanalysis.dto.AnalysisResponse;
import com.chessanalysis.model.*;
import com.chessanalysis.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final AnalysisRepository analysisRepository;
    private final AIInsightRepository aiInsightRepository;
    private final LichessApiService lichessApiService;
    private final ChessComApiService chessComApiService;
    private final StockfishService stockfishService;
    private final GroqAIService groqAIService;

    @Transactional
    public AnalysisResponse analyzeUser(AnalysisRequest request) {
        log.info("Starting analysis for user: {} on platform: {}", 
                request.getUsername(), request.getPlatform());

        // Find or create user
        User user = findOrCreateUser(request);

        // Fetch games from the appropriate platform
        List<Game> games = fetchGames(request);
        
        // Save games
        games.forEach(game -> {
            game.setUser(user);
            gameRepository.save(game);
        });

        // Analyze each game with Stockfish
        List<Analysis> analyses = games.stream()
                .map(stockfishService::analyzeGame)
                .peek(analysisRepository::save)
                .collect(Collectors.toList());

        // Generate AI insights
        AIInsight aiInsight = groqAIService.generateInsights(user, games, analyses);
        aiInsight.setUser(user);
        aiInsight.setGeneratedAt(LocalDateTime.now());
        aiInsightRepository.save(aiInsight);

        // Update user's last analyzed time
        user.setLastAnalyzedAt(LocalDateTime.now());
        userRepository.save(user);

        // Build response
        return buildResponse(user, games, analyses, aiInsight);
    }

    private User findOrCreateUser(AnalysisRequest request) {
        String platform = request.getPlatform();
        String username = request.getUsername();

        if ("LICHESS".equalsIgnoreCase(platform)) {
            return userRepository.findByLichessUsername(username)
                    .orElseGet(() -> {
                        User newUser = User.builder()
                                .lichessUsername(username)
                                .createdAt(LocalDateTime.now())
                                .build();
                        return userRepository.save(newUser);
                    });
        } else {
            return userRepository.findByChesscomUsername(username)
                    .orElseGet(() -> {
                        User newUser = User.builder()
                                .chesscomUsername(username)
                                .createdAt(LocalDateTime.now())
                                .build();
                        return userRepository.save(newUser);
                    });
        }
    }

    private List<Game> fetchGames(AnalysisRequest request) {
        String platform = request.getPlatform();
        int gameCount = request.getNumberOfGames() != null ? request.getNumberOfGames() : 10;

        if ("LICHESS".equalsIgnoreCase(platform)) {
            return lichessApiService.fetchUserGames(request.getUsername(), gameCount);
        } else {
            return chessComApiService.fetchUserGames(request.getUsername(), gameCount);
        }
    }

    private AnalysisResponse buildResponse(User user, List<Game> games, 
                                          List<Analysis> analyses, AIInsight aiInsight) {
        // Calculate statistics
        int wins = (int) games.stream()
                .filter(g -> "1-0".equals(g.getResult()) || "0-1".equals(g.getResult()))
                .count();
        int losses = (int) games.stream()
                .filter(g -> "0-1".equals(g.getResult()))
                .count();
        int draws = (int) games.stream()
                .filter(g -> "1/2-1/2".equals(g.getResult()))
                .count();

        double avgAccuracy = analyses.stream()
                .filter(a -> a.getAccuracy() != null)
                .mapToDouble(a -> a.getAccuracy().doubleValue())
                .average()
                .orElse(0.0);

        int totalBlunders = analyses.stream()
                .mapToInt(Analysis::getBlunders)
                .sum();
        int totalMistakes = analyses.stream()
                .mapToInt(Analysis::getMistakes)
                .sum();
        int totalInaccuracies = analyses.stream()
                .mapToInt(Analysis::getInaccuracies)
                .sum();

        // Get top openings
        List<String> topOpenings = analyses.stream()
                .map(Analysis::getOpeningName)
                .filter(name -> name != null && !name.isEmpty())
                .collect(Collectors.groupingBy(name -> name, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Get time control distribution
        Map<String, Long> timeControlDist = games.stream()
                .map(Game::getTimeControl)
                .filter(tc -> tc != null && !tc.isEmpty())
                .collect(Collectors.groupingBy(tc -> tc, Collectors.counting()));

        return AnalysisResponse.builder()
                .userId(user.getId())
                .username(user.getLichessUsername() != null ? 
                         user.getLichessUsername() : user.getChesscomUsername())
                .platform(games.isEmpty() ? "UNKNOWN" : games.get(0).getPlatform())
                .totalGames(games.size())
                .wins(wins)
                .losses(losses)
                .draws(draws)
                .avgAccuracy(avgAccuracy)
                .totalBlunders(totalBlunders)
                .totalMistakes(totalMistakes)
                .totalInaccuracies(totalInaccuracies)
                .topOpenings(topOpenings)
                .timeControlDistribution(timeControlDist)
                .playingStyle(aiInsight.getPlayingStyle())
                .strengths(parseList(aiInsight.getStrengths()))
                .weaknesses(parseList(aiInsight.getWeaknesses()))
                .commonMistakes(parseList(aiInsight.getCommonMistakes()))
                .recommendations(parseList(aiInsight.getRecommendations()))
                .openingAnalysis(aiInsight.getOpeningAnalysis())
                .build();
    }

    private List<String> parseList(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return List.of(text.split("\\n"));
    }
}
