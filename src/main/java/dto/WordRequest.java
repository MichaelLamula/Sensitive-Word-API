package dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for adding or updating a sensitive word")
public record WordRequest(
        @NotBlank
        @Schema(description = "The sensitive word to be censored", example = "SELECT")
        String word
) {}
