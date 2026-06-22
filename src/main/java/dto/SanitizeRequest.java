package dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload containing the message to be sanitized")
public record SanitizeRequest(
        @NotBlank
        @Schema(description = "The raw message inputted by the user", example = "Please SELECT * FROM users;")
        String message
) {}
