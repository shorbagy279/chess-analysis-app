package com.chessanalysis.service;

import com.chessanalysis.model.Game;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ChessComApiService {

    @Value("${chesscom.api.url}")
    private String chessComApiUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ChessComApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(chessComApiUrl)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public List<Game> fetchUserGames(String username, int numberOfGames) {
        log.info("Fetching {} games for user {} from Chess.com", numberOfGames, username);

        try {
            // Get current month's games (you can expand this to fetch from multiple months)
            String currentMonth = java.time.YearMonth.now().toString().replace("-", "/");
            String endpoint = String.format("/player/%s/games/%s", username, currentMonth);

            String response = webClient.get()
                    .uri(endpoint)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(30));

            if (response == null || response.isEmpty()) {
                log.warn("No games found for user {} in current month", username);
                return List.of();
            }

            return parseGamesFromJson(response, numberOfGames);

        } catch (Exception e) {
            log.error("Error fetching games from Chess.com: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch games from Chess.com: " + e.getMessage());
        }
    }

    private List<Game> parseGamesFromJson(String json, int maxGames) {
        List<Game> games = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode gamesNode = root.get("games");

            if (gamesNode != null && gamesNode.isArray()) {
                int count = 0;
                for (JsonNode gameNode : gamesNode) {
                    if (count >= maxGames) break;

                    Game game = new Game();
                    game.setPlatform("CHESS_COM");

                    // Extract PGN
                    if (gameNode.has("pgn")) {
                        game.setPgn(gameNode.get("pgn").asText());
                    }

                    // Extract game URL as ID
                    if (gameNode.has("url")) {
                        String url = gameNode.get("url").asText();
                        game.setGameId(url.substring(url.lastIndexOf("/") + 1));
                    }

                    // Extract result
                    if (gameNode.has("white") && gameNode.get("white").has("result")) {
                        String whiteResult = gameNode.get("white").get("result").asText();
                        if (whiteResult.equals("win")) {
                            game.setResult("1-0");
                        } else if (whiteResult.equals("checkmated") || whiteResult.equals("resigned") || 
                                   whiteResult.equals("timeout") || whiteResult.equals("abandoned")) {
                            game.setResult("0-1");
                        } else {
                            game.setResult("1/2-1/2");
                        }
                    }

                    if (game.getPgn() != null && !game.getPgn().isEmpty()) {
                        games.add(game);
                        count++;
                    }
                }
            }

            log.info("Successfully parsed {} games from Chess.com", games.size());

        } catch (Exception e) {
            log.error("Error parsing Chess.com games JSON: {}", e.getMessage(), e);
        }

        return games;
    }
}