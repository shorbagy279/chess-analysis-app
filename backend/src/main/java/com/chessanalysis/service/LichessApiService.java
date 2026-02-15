package com.chessanalysis.service;

import com.chessanalysis.model.Game;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class LichessApiService {

    @Value("${lichess.api.url}")
    private String lichessApiUrl;

    private final WebClient webClient;

    public LichessApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(lichessApiUrl)
                .build();
    }

    public List<Game> fetchUserGames(String username, int numberOfGames) {
        log.info("Fetching {} games for user {} from Lichess", numberOfGames, username);

        try {
            String endpoint = String.format("/api/games/user/%s?max=%d&pgnInJson=true&clocks=false&evals=false&opening=true",
                    username, numberOfGames);

            List<Game> games = webClient.get()
                    .uri(endpoint)
                    .header("Accept", "application/x-ndjson")
                    .retrieve()
                    .bodyToFlux(String.class)
                    .take(numberOfGames)
                    .map(this::parseNdJsonGame)
                    .filter(game -> game != null)
                    .collectList()
                    .block(Duration.ofSeconds(30));

            log.info("Successfully fetched {} games from Lichess", games != null ? games.size() : 0);
            return games != null ? games : List.of();

        } catch (Exception e) {
            log.error("Error fetching games from Lichess: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch games from Lichess: " + e.getMessage());
        }
    }

    private Game parseNdJsonGame(String jsonLine) {
        try {
            // Simple JSON parsing (you might want to use Jackson ObjectMapper for production)
            Game game = new Game();
            game.setPlatform("LICHESS");
            
            // Extract PGN from JSON
            int pgnStart = jsonLine.indexOf("\"pgn\":\"");
            if (pgnStart != -1) {
                pgnStart += 7;
                int pgnEnd = jsonLine.indexOf("\"", pgnStart);
                if (pgnEnd != -1) {
                    String pgn = jsonLine.substring(pgnStart, pgnEnd)
                            .replace("\\n", "\n")
                            .replace("\\\"", "\"");
                    game.setPgn(pgn);
                }
            }

            // Extract game ID
            int idStart = jsonLine.indexOf("\"id\":\"");
            if (idStart != -1) {
                idStart += 6;
                int idEnd = jsonLine.indexOf("\"", idStart);
                if (idEnd != -1) {
                    game.setGameId(jsonLine.substring(idStart, idEnd));
                }
            }

            // Extract result
            int resultStart = jsonLine.indexOf("\"winner\":\"");
            if (resultStart != -1) {
                resultStart += 10;
                int resultEnd = jsonLine.indexOf("\"", resultStart);
                if (resultEnd != -1) {
                    String winner = jsonLine.substring(resultStart, resultEnd);
                    game.setResult(winner.equals("white") ? "1-0" : winner.equals("black") ? "0-1" : "1/2-1/2");
                }
            } else {
                game.setResult("1/2-1/2"); // Draw if no winner
            }

            return game;

        } catch (Exception e) {
            log.error("Error parsing game JSON: {}", e.getMessage());
            return null;
        }
    }
}