package com.chessanalysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ChessAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChessAnalysisApplication.class, args);
    }
}
