package dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for authenticating an existing user")
public record AuthRequest(
        @NotBlank(message = "Username cannot be blank")
        @Schema(description = "The registered username", example = "admin_user")
        String username,

        @NotBlank(message = "Password cannot be blank")
        @Schema(description = "The user's raw password", example = "SecurePass123!")
        String password
) {}