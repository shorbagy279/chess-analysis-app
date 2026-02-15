package com.chessanalysis.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Platform is required")
    @Pattern(regexp = "lichess|chesscom", message = "Platform must be either 'lichess' or 'chesscom'")
    private String platform;

    @Min(value = 10, message = "Game count must be at least 10")
    @Max(value = 200, message = "Game count cannot exceed 200")
    private Integer gameCount = 50;
}
