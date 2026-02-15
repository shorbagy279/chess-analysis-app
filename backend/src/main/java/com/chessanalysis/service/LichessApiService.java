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
public class LichessApiService {

    @Value("${lichess.api.url}")
    private String lichessApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LichessApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public List<Game> fetchGames(User user, int count) {
        List<Game> games = new ArrayList<>();
        
        try {
            String url = String.format("%s/games/user/%s?max=%d&pgnInJson=true&opening=true&clocks=true",
                    lichessApiUrl, user.getLichessUsername(), count);
            
            log.info("Fetching games from Lichess: {}", url);
            
            String response = restTemplate.getForObject(url, String.class);
            
            if (response == null || response.isEmpty()) {
                log.warn("No games found for user: {}", user.getLichessUsername());
                return games;
            }

            // Lichess returns NDJSON (newline-delimited JSON)
            String[] lines = response.split("\n");
            
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                
                try {
                    JsonNode gameNode = objectMapper.readTree(line);
                    Game game = parseL ichessGame(gameNode, user);
                    if (game != null) {
                        games.add(game);
                    }
                } catch (Exception e) {
                    log.error("Error parsing game: {}", e.getMessage());
                }
            }
            
            log.info("Successfully fetched {} games from Lichess", games.size());
            
        } catch (Exception e) {
            log.error("Error fetching games from Lichess: {}", e.getMessage(), e);
        }
        
        return games;
    }

    private Game parseLichessGame(JsonNode gameNode, User user) {
        try {
            Game game = new Game();
            game.setUser(user);
            game.setPlatform("lichess");
            game.setGameId(gameNode.get("id").asText());
            game.setPgn(gameNode.get("pgn").asText());
            
            // Time control
            JsonNode clock = gameNode.get("clock");
            if (clock != null) {
                int initial = clock.get("initial").asInt();
                int increment = clock.get("increment").asInt();
                game.setTimeControl(String.format("%d+%d", initial / 60, increment));
            } else {
                game.setTimeControl("correspondence");
            }
            
            // Players and ratings
            JsonNode players = gameNode.get("players");
            JsonNode white = players.get("white");
            JsonNode black = players.get("black");
            
            String username = user.getLichessUsername();
            boolean isWhite = white.get("user").get("name").asText().equalsIgnoreCase(username);
            
            game.setPlayedAs(isWhite ? "white" : "black");
            game.setWhiteRating(white.has("rating") ? white.get("rating").asInt() : null);
            game.setBlackRating(black.has("rating") ? black.get("rating").asInt() : null);
            
            // Result
            String winner = gameNode.has("winner") ? gameNode.get("winner").asText() : "draw";
            if (winner.equals("draw") || !gameNode.has("winner")) {
                game.setResult("draw");
            } else if ((isWhite && winner.equals("white")) || (!isWhite && winner.equals("black"))) {
                game.setResult("win");
            } else {
                game.setResult("loss");
            }
            
            // Played at
            long timestamp = gameNode.get("createdAt").asLong();
            game.setPlayedAt(LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));
            
            return game;
            
        } catch (Exception e) {
            log.error("Error parsing Lichess game: {}", e.getMessage());
            return null;
        }
    }
}
