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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClaudeAIService {

    @Value("${claude.api.key}")
    private String apiKey;

    @Value("${claude.api.url}")
    private String apiUrl;

    @Value("${claude.api.model}")
    private String model;

    @Value("${claude.api.version}")
    private String apiVersion;

    @Value("${claude.api.max-tokens}")
    private int maxTokens;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ClaudeAIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public AIInsight generateInsights(User user, List<Game> games, List<Analysis> analyses) {
        AIInsight insight = new AIInsight();
        insight.setUser(user);

        try {
            // Prepare aggregated data for Claude
            String prompt = buildAnalysisPrompt(games, analyses);
            
            // Call Claude API
            String response = callClaudeAPI(prompt);
            
            // Parse response
            parseClaudeResponse(response, insight);
            
            log.info("Successfully generated AI insights for user: {}", user.getId());
            
        } catch (Exception e) {
            log.error("Error generating AI insights: {}", e.getMessage(), e);
            // Set default values on error
            insight.setPlayingStyle("Analysis could not be completed");
            insight.setStrengths("Unable to determine at this time");
            insight.setWeaknesses("Unable to determine at this time");
            insight.setRecommendations("Please try again later");
        }

        return insight;
    }

    private String buildAnalysisPrompt(List<Game> games, List<Analysis> analyses) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are a professional chess coach analyzing a player's games. ");
        prompt.append("Based on the following data from ").append(games.size()).append(" games, ");
        prompt.append("provide comprehensive insights about this chess player.\n\n");
        
        // Game results summary
        long wins = games.stream().filter(g -> "win".equals(g.getResult())).count();
        long losses = games.stream().filter(g -> "loss".equals(g.getResult())).count();
        long draws = games.stream().filter(g -> "draw".equals(g.getResult())).count();
        
        prompt.append("### Overall Performance:\n");
        prompt.append("- Total games: ").append(games.size()).append("\n");
        prompt.append("- Wins: ").append(wins).append(" (")
              .append(String.format("%.1f", wins * 100.0 / games.size())).append("%)\n");
        prompt.append("- Losses: ").append(losses).append(" (")
              .append(String.format("%.1f", losses * 100.0 / games.size())).append("%)\n");
        prompt.append("- Draws: ").append(draws).append(" (")
              .append(String.format("%.1f", draws * 100.0 / games.size())).append("%)\n\n");
        
        // Analysis statistics
        double avgAccuracy = analyses.stream()
                .map(Analysis::getAccuracy)
                .filter(a -> a != null)
                .mapToDouble(BigDecimal::doubleValue)
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
        
        prompt.append("### Move Quality:\n");
        prompt.append("- Average accuracy: ").append(String.format("%.1f", avgAccuracy)).append("%\n");
        prompt.append("- Total blunders: ").append(totalBlunders)
              .append(" (avg ").append(String.format("%.1f", totalBlunders * 1.0 / games.size()))
              .append(" per game)\n");
        prompt.append("- Total mistakes: ").append(totalMistakes)
              .append(" (avg ").append(String.format("%.1f", totalMistakes * 1.0 / games.size()))
              .append(" per game)\n");
        prompt.append("- Total inaccuracies: ").append(totalInaccuracies)
              .append(" (avg ").append(String.format("%.1f", totalInaccuracies * 1.0 / games.size()))
              .append(" per game)\n\n");
        
        // Opening repertoire
        Map<String, Long> openings = analyses.stream()
                .filter(a -> a.getOpeningName() != null)
                .collect(Collectors.groupingBy(Analysis::getOpeningName, Collectors.counting()));
        
        prompt.append("### Opening Repertoire:\n");
        openings.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> prompt.append("- ").append(entry.getKey())
                        .append(": ").append(entry.getValue()).append(" games\n"));
        
        prompt.append("\n### Please provide:\n\n");
        prompt.append("1. **Playing Style**: Describe the player's overall chess style in 2-3 sentences.\n\n");
        prompt.append("2. **Strengths**: List 3-4 key strengths with specific examples.\n\n");
        prompt.append("3. **Weaknesses**: List 3-4 areas that need improvement with specific examples.\n\n");
        prompt.append("4. **Common Mistakes**: Identify patterns in errors and blunders.\n\n");
        prompt.append("5. **Recommendations**: Provide 4-5 actionable recommendations for improvement.\n\n");
        prompt.append("6. **Opening Analysis**: Analyze their opening repertoire and suggest improvements.\n\n");
        prompt.append("7. **Ratings**: Rate the following on a scale (Excellent/Good/Average/Needs Work):\n");
        prompt.append("   - Tactical ability\n");
        prompt.append("   - Positional understanding\n");
        prompt.append("   - Endgame technique\n");
        prompt.append("   - Time management\n\n");
        
        prompt.append("Format your response clearly with markdown headers.");
        
        return prompt.toString();
    }

    private String callClaudeAPI(String prompt) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", apiVersion);

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", model);
        requestBody.put("max_tokens", maxTokens);
        
        ArrayNode messages = requestBody.putArray("messages");
        ObjectNode message = messages.addObject();
        message.put("role", "user");
        message.put("content", prompt);

        HttpEntity<String> request = new HttpEntity<>(
                objectMapper.writeValueAsString(requestBody), headers);

        log.info("Calling Claude API...");
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
        
        JsonNode responseNode = objectMapper.readTree(response.getBody());
        JsonNode content = responseNode.get("content").get(0);
        
        return content.get("text").asText();
    }

    private void parseClaudeResponse(String response, AIInsight insight) {
        // Parse the Claude response and extract sections
        // This is a simplified parser - in production, you might want more robust parsing
        
        String[] sections = response.split("##");
        
        for (String section : sections) {
            String sectionLower = section.toLowerCase();
            
            if (sectionLower.contains("playing style")) {
                insight.setPlayingStyle(extractContent(section));
            } else if (sectionLower.contains("strengths")) {
                insight.setStrengths(extractContent(section));
            } else if (sectionLower.contains("weaknesses")) {
                insight.setWeaknesses(extractContent(section));
            } else if (sectionLower.contains("common mistakes")) {
                insight.setCommonMistakes(extractContent(section));
            } else if (sectionLower.contains("recommendations")) {
                insight.setRecommendations(extractContent(section));
            } else if (sectionLower.contains("opening analysis")) {
                insight.setOpeningAnalysis(extractContent(section));
            }
        }
        
        // Extract ratings
        if (response.toLowerCase().contains("tactical")) {
            insight.setTacticalRating(extractRating(response, "tactical"));
        }
        if (response.toLowerCase().contains("positional")) {
            insight.setPositionalRating(extractRating(response, "positional"));
        }
        if (response.toLowerCase().contains("endgame")) {
            insight.setEndgameRating(extractRating(response, "endgame"));
        }
        if (response.toLowerCase().contains("time management")) {
            insight.setTimeManagementRating(extractRating(response, "time management"));
        }
    }

    private String extractContent(String section) {
        // Remove the header and trim
        int firstNewline = section.indexOf('\n');
        if (firstNewline > 0) {
            return section.substring(firstNewline).trim();
        }
        return section.trim();
    }

    private String extractRating(String text, String category) {
        String lowerText = text.toLowerCase();
        int index = lowerText.indexOf(category.toLowerCase());
        
        if (index == -1) return "Average";
        
        String substring = lowerText.substring(index, Math.min(index + 100, lowerText.length()));
        
        if (substring.contains("excellent")) return "Excellent";
        if (substring.contains("good")) return "Good";
        if (substring.contains("needs work")) return "Needs Work";
        
        return "Average";
    }
}
