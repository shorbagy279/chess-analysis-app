package com.chessanalysis.service;

import com.chessanalysis.model.Analysis;
import com.chessanalysis.model.Game;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StockfishService {

    @Value("${stockfish.path}")
    private String stockfishPath;

    @Value("${stockfish.depth}")
    private int depth;

    @Value("${stockfish.threads}")
    private int threads;

    public Analysis analyzeGame(Game game) {
        Analysis analysis = new Analysis();
        analysis.setGame(game);

        try {
            // Parse PGN
            Board board = new Board();
            MoveList moves = new MoveList();
            moves.loadFromSan(game.getPgn());

            List<Integer> centipawnLosses = new ArrayList<>();
            int blunders = 0;
            int mistakes = 0;
            int inaccuracies = 0;
            int bestMoves = 0;

            // Analyze key positions (every 3 moves to save time)
            for (int i = 0; i < moves.size(); i += 3) {
                try {
                    board.doMove(moves.get(i));
                    
                    // Skip opening moves (first 10 moves)
                    if (i < 10) continue;

                    // Get evaluation before the move
                    int evalBefore = getEvaluation(board.getFen());
                    
                    // Make the move
                    if (i + 1 < moves.size()) {
                        board.doMove(moves.get(i + 1));
                        int evalAfter = getEvaluation(board.getFen());
                        
                        // Calculate centipawn loss
                        int cpLoss = Math.abs(evalAfter - evalBefore);
                        centipawnLosses.add(cpLoss);
                        
                        // Classify move quality
                        if (cpLoss < 20) {
                            bestMoves++;
                        } else if (cpLoss < 50) {
                            inaccuracies++;
                        } else if (cpLoss < 100) {
                            mistakes++;
                        } else {
                            blunders++;
                        }
                    }
                } catch (Exception e) {
                    log.error("Error analyzing position: {}", e.getMessage());
                }
            }

            // Calculate average centipawn loss
            if (!centipawnLosses.isEmpty()) {
                double avgCpLoss = centipawnLosses.stream()
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0.0);
                analysis.setAvgCentipawnLoss(
                        BigDecimal.valueOf(avgCpLoss).setScale(2, RoundingMode.HALF_UP));
                
                // Calculate accuracy (simplified formula)
                double accuracy = Math.max(0, 100 - (avgCpLoss / 2));
                analysis.setAccuracy(
                        BigDecimal.valueOf(accuracy).setScale(2, RoundingMode.HALF_UP));
            }

            analysis.setBlunders(blunders);
            analysis.setMistakes(mistakes);
            analysis.setInaccuracies(inaccuracies);
            analysis.setBestMoves(bestMoves);

            // Extract opening name from PGN
            String pgn = game.getPgn();
            if (pgn.contains("[Opening")) {
                int start = pgn.indexOf("[Opening \"") + 10;
                int end = pgn.indexOf("\"]", start);
                if (end > start) {
                    analysis.setOpeningName(pgn.substring(start, end));
                }
            }

            log.info("Game analysis complete: {} blunders, {} mistakes, {} inaccuracies", 
                    blunders, mistakes, inaccuracies);

        } catch (Exception e) {
            log.error("Error analyzing game: {}", e.getMessage(), e);
        }

        return analysis;
    }

    private int getEvaluation(String fen) {
        // Simplified evaluation - in production, use actual Stockfish process
        // This is a placeholder that returns a random evaluation
        // Real implementation would communicate with Stockfish engine
        
        try {
            Process process = Runtime.getRuntime().exec(stockfishPath);
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(process.getOutputStream()));
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            writer.write("uci\n");
            writer.flush();
            
            writer.write(String.format("position fen %s\n", fen));
            writer.write(String.format("go depth %d\n", depth));
            writer.flush();

            String line;
            int score = 0;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("info") && line.contains("score cp")) {
                    String[] parts = line.split(" ");
                    for (int i = 0; i < parts.length - 1; i++) {
                        if (parts[i].equals("cp")) {
                            score = Integer.parseInt(parts[i + 1]);
                            break;
                        }
                    }
                }
                if (line.startsWith("bestmove")) {
                    break;
                }
            }

            writer.write("quit\n");
            writer.flush();
            process.destroy();

            return score;
            
        } catch (Exception e) {
            log.error("Error getting Stockfish evaluation: {}", e.getMessage());
            return 0;
        }
    }
}
