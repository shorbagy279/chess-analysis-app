package com.chessanalysis.service;

import com.chessanalysis.model.AIInsight;
import com.chessanalysis.model.Analysis;
import com.chessanalysis.model.Game;
import com.chessanalysis.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GroqAIService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.api.model}")
    private String model;

    @Value("${groq.api.max-tokens}")
    private int maxTokens;

    @Value("${groq.api.temperature}")
    private double temperature;

    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient;

    public GroqAIService() {
        this.objectMapper = new ObjectMapper();
        this.httpClient = new OkHttpClient();
    }

    public AIInsight generateInsights(User user, List<Game> games, List<Analysis> analyses) {
        AIInsight insight = new AIInsight();
        insight.setUser(user);

        try {
            String prompt = buildAnalysisPrompt(games, analyses);
            String response = callGroqAPI(prompt);
            parseResponse(response, insight);
            
            log.info("Successfully generated AI insights for user: {}", user.getId());
            
        } catch (Exception e) {
            log.error("Error generating AI insights: {}", e.getMessage(), e);
            insight.setPlayingStyle("Analysis could not be completed");
            insight.setStrengths("Unable to determine at this time");
            insight.setWeaknesses("Unable to determine at this time");
            insight.setRecommendations("Please try again later");
        }

        return insight;
    }

    public String analyzePosition(String fen, int evaluation) {
        try {
            String prompt = String.format(
                "As a chess coach, analyze this position (FEN: %s) with evaluation %d centipawns. " +
                "Provide a brief 2-3 sentence analysis of the key features and who is better.",
                fen, evaluation
            );
            
            return callGroqAPI(prompt);
        } catch (Exception e) {
            log.error("Error analyzing position: {}", e.getMessage());
            return "Position analysis unavailable";
        }
    }

    private String buildAnalysisPrompt(List<Game> games, List<Analysis> analyses) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are a professional chess coach. Analyze this player based on ")
              .append(games.size()).append(" games.\n\n");
        
        // Add statistics
        long wins = games.stream().filter(g -> "win".equals(g.getResult())).count();
        long losses = games.stream().filter(g -> "loss".equals(g.getResult())).count();
        long draws = games.stream().filter(g -> "draw".equals(g.getResult())).count();
        
        prompt.append("### Performance:\n");
        prompt.append(String.format("- Record: %dW-%dL-%dD (%.1f%% win rate)\n", 
            wins, losses, draws, wins * 100.0 / games.size()));
        
        double avgAccuracy = analyses.stream()
                .map(Analysis::getAccuracy)
                .filter(a -> a != null)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0.0);
        
        int totalBlunders = analyses.stream().mapToInt(Analysis::getBlunders).sum();
        int totalMistakes = analyses.stream().mapToInt(Analysis::getMistakes).sum();
        
        prompt.append(String.format("- Average accuracy: %.1f%%\n", avgAccuracy));
        prompt.append(String.format("- Blunders per game: %.1f\n", totalBlunders * 1.0 / games.size()));
        prompt.append(String.format("- Mistakes per game: %.1f\n", totalMistakes * 1.0 / games.size()));
        
        prompt.append("\n### Provide:\n");
        prompt.append("1. Playing Style (2-3 sentences)\n");
        prompt.append("2. Strengths (3-4 bullet points)\n");
        prompt.append("3. Weaknesses (3-4 bullet points)\n");
        prompt.append("4. Common Mistakes (2-3 patterns)\n");
        prompt.append("5. Recommendations (4-5 actionable tips)\n");
        prompt.append("6. Opening Analysis (brief overview)\n");
        prompt.append("7. Ratings (Excellent/Good/Average/Needs Work) for: Tactical, Positional, Endgame, Time Management\n");
        
        return prompt.toString();
    }

    private String callGroqAPI(String prompt) throws Exception {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", model);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("temperature", temperature);
        
        ArrayNode messages = requestBody.putArray("messages");
        ObjectNode message = messages.addObject();
        message.put("role", "user");
        message.put("content", prompt);

        RequestBody body = RequestBody.create(
            objectMapper.writeValueAsString(requestBody),
            MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Groq API call failed: " + response.code());
            }
            
            String responseBody = response.body().string();
            JsonNode responseNode = objectMapper.readTree(responseBody);
            JsonNode content = responseNode.get("choices").get(0).get("message").get("content");
            
            return content.asText();
        }
    }

    private void parseResponse(String response, AIInsight insight) {
        // Simple parsing - extract sections
        String[] lines = response.split("\n");
        StringBuilder currentSection = new StringBuilder();
        String sectionName = "";
        
        for (String line : lines) {
            if (line.toLowerCase().contains("playing style")) {
                sectionName = "style";
                currentSection = new StringBuilder();
            } else if (line.toLowerCase().contains("strengths")) {
                if (!currentSection.toString().isEmpty()) {
                    insight.setPlayingStyle(currentSection.toString().trim());
                }
                sectionName = "strengths";
                currentSection = new StringBuilder();
            } else if (line.toLowerCase().contains("weaknesses")) {
                if (!currentSection.toString().isEmpty()) {
                    insight.setStrengths(currentSection.toString().trim());
                }
                sectionName = "weaknesses";
                currentSection = new StringBuilder();
            } else if (line.toLowerCase().contains("common mistakes")) {
                if (!currentSection.toString().isEmpty()) {
                    insight.setWeaknesses(currentSection.toString().trim());
                }
                sectionName = "mistakes";
                currentSection = new StringBuilder();
            } else if (line.toLowerCase().contains("recommendations")) {
                if (!currentSection.toString().isEmpty()) {
                    insight.setCommonMistakes(currentSection.toString().trim());
                }
                sectionName = "recommendations";
                currentSection = new StringBuilder();
            } else if (line.toLowerCase().contains("opening")) {
                if (!currentSection.toString().isEmpty()) {
                    insight.setRecommendations(currentSection.toString().trim());
                }
                sectionName = "opening";
                currentSection = new StringBuilder();
            } else {
                currentSection.append(line).append("\n");
            }
        }
        
        // Set final section
        if (!currentSection.toString().isEmpty()) {
            if ("opening".equals(sectionName)) {
                insight.setOpeningAnalysis(currentSection.toString().trim());
            }
        }
        
        // Extract ratings
        insight.setTacticalRating(extractRating(response, "tactical"));
        insight.setPositionalRating(extractRating(response, "positional"));
        insight.setEndgameRating(extractRating(response, "endgame"));
        insight.setTimeManagementRating(extractRating(response, "time"));
    }

    private String extractRating(String text, String category) {
        String lowerText = text.toLowerCase();
        int index = lowerText.indexOf(category);
        
        if (index == -1) return "Average";
        
        String substring = lowerText.substring(index, Math.min(index + 100, lowerText.length()));
        
        if (substring.contains("excellent")) return "Excellent";
        if (substring.contains("good")) return "Good";
        if (substring.contains("needs work")) return "Needs Work";
        
        return "Average";
    }
}