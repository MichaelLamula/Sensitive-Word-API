package dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roles.Role;

@Schema(description = "Request payload for registering a new user")
public record RegisterRequest(
        @NotBlank(message = "Username cannot be blank")
        @Schema(description = "Desired username", example = "new_client")
        String username,

        @NotBlank(message = "Password cannot be blank")
        @Schema(description = "Desired password", example = "SecurePass123!")
        String password,

        @NotNull(message = "Role must be specified")
        @Schema(description = "System role granting specific API permissions", example = "USER")
        Role role
) {}
