package com.chessanalysis.service;

import com.chessanalysis.model.Game;
import com.chessanalysis.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ChesscomApiService {

    @Value("${chesscom.api.url}")
    private String chesscomApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ChesscomApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public List<Game> fetchGames(User user, int count) {
        List<Game> games = new ArrayList<>();
        
        try {
            // First, get list of monthly archives
            String archivesUrl = String.format("%s/player/%s/games/archives", 
                    chesscomApiUrl, user.getChesscomUsername());
            
            log.info("Fetching archives from Chess.com: {}", archivesUrl);
            
            String archivesResponse = restTemplate.getForObject(archivesUrl, String.class);
            JsonNode archivesNode = objectMapper.readTree(archivesResponse);
            JsonNode archives = archivesNode.get("archives");
            
            if (archives == null || archives.size() == 0) {
                log.warn("No archives found for user: {}", user.getChesscomUsername());
                return games;
            }

            // Get games from most recent archives first
            for (int i = archives.size() - 1; i >= 0 && games.size() < count; i--) {
                String archiveUrl = archives.get(i).asText();
                List<Game> monthGames = fetchGamesFromArchive(archiveUrl, user);
                games.addAll(monthGames);
            }
            
            // Limit to requested count
            if (games.size() > count) {
                games = games.subList(0, count);
            }
            
            log.info("Successfully fetched {} games from Chess.com", games.size());
            
        } catch (Exception e) {
            log.error("Error fetching games from Chess.com: {}", e.getMessage(), e);
        }
        
        return games;
    }

    private List<Game> fetchGamesFromArchive(String archiveUrl, User user) {
        List<Game> games = new ArrayList<>();
        
        try {
            String response = restTemplate.getForObject(archiveUrl, String.class);
            JsonNode archiveNode = objectMapper.readTree(response);
            JsonNode gamesArray = archiveNode.get("games");
            
            if (gamesArray == null) return games;
            
            for (JsonNode gameNode : gamesArray) {
                Game game = parseChesscomGame(gameNode, user);
                if (game != null) {
                    games.add(game);
                }
            }
            
        } catch (Exception e) {
            log.error("Error fetching games from archive {}: {}", archiveUrl, e.getMessage());
        }
        
        return games;
    }

    private Game parseChesscomGame(JsonNode gameNode, User user) {
        try {
            Game game = new Game();
            game.setUser(user);
            game.setPlatform("chesscom");
            game.setGameId(gameNode.get("url").asText().substring(
                    gameNode.get("url").asText().lastIndexOf("/") + 1));
            game.setPgn(gameNode.get("pgn").asText());
            
            // Time control
            String timeClass = gameNode.get("time_class").asText();
            game.setTimeControl(timeClass);
            
            // Players and ratings
            JsonNode white = gameNode.get("white");
            JsonNode black = gameNode.get("black");
            
            String username = user.getChesscomUsername();
            boolean isWhite = white.get("username").asText().equalsIgnoreCase(username);
            
            game.setPlayedAs(isWhite ? "white" : "black");
            game.setWhiteRating(white.has("rating") ? white.get("rating").asInt() : null);
            game.setBlackRating(black.has("rating") ? black.get("rating").asInt() : null);
            
            // Result
            String whiteResult = white.get("result").asText();
            String blackResult = black.get("result").asText();
            
            if (whiteResult.equals("win") && isWhite) {
                game.setResult("win");
            } else if (blackResult.equals("win") && !isWhite) {
                game.setResult("win");
            } else if (whiteResult.contains("draw") || blackResult.contains("draw")) {
                game.setResult("draw");
            } else {
                game.setResult("loss");
            }
            
            // Played at
            long timestamp = gameNode.get("end_time").asLong();
            game.setPlayedAt(LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(timestamp), ZoneId.systemDefault()));
            
            return game;
            
        } catch (Exception e) {
            log.error("Error parsing Chess.com game: {}", e.getMessage());
            return null;
        }
    }
}
