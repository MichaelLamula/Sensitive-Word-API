package dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload containing both the original and censored message")
public record SanitizeResponse(
        @Schema(description = "The original raw message", example = "Please SELECT * FROM users;")
        String original,

        @Schema(description = "The message with sensitive words replaced by asterisks", example = "Please ****** * FROM users;")
        String sanitized
) {}
