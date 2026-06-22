package controller;

import dto.SanitizeRequest;
import dto.SanitizeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.SensitiveWordService;

@RestController
@RequestMapping("/api/v1/external/messages")
@Tag(name = "2. Sanitization API (External)", description = "Core business logic endpoints for external client applications to sanitize chat messages. Requires USER or ADMIN role.")
public class SanitizationController {

    private final SensitiveWordService service;

    public SanitizationController(SensitiveWordService service) {
        this.service = service;
    }

    @PostMapping("/sanitize")
    @Operation(
            summary = "Sanitize a chat message",
            description = "Receives a raw chat message, scans it against the cached sensitive word dictionary in memory, and returns the message with sensitive words replaced by asterisks (*)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message successfully processed and sanitized"),
            @ApiResponse(responseCode = "400", description = "Message payload is empty or invalid", content = @Content)
    })
    public ResponseEntity<SanitizeResponse> sanitize(@Valid @RequestBody SanitizeRequest request) {
        String sanitized = service.sanitizeMessage(request.message());
        return ResponseEntity.ok(new SanitizeResponse(request.message(), sanitized));
    }
}
