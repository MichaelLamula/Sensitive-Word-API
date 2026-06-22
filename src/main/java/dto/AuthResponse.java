package dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload containing the JWT token after successful login or registration")
public record AuthResponse(
        @Schema(description = "JWT Bearer token. Prepend 'Bearer ' when passing it in the Authorization header.",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI...")
        String token
) {}
