package controller;

import dto.WordRequest;
import dto.WordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.SensitiveWordService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/internal/words")
@Tag(name = "1. Admin API (Internal)", description = "CRUD operations for managing the sensitive word dictionary. Requires ADMIN role.")
public class AdminController {

    private final SensitiveWordService service;

    public AdminController(SensitiveWordService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Add a new sensitive word", description = "Adds a new word to the database and refreshes the regex cache.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Word successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "409", description = "Word already exists in the database", content = @Content)
    })
    public ResponseEntity<WordResponse> addWord(@Valid @RequestBody WordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addWord(request));
    }

    @GetMapping
    @Operation(summary = "Get all sensitive words", description = "Retrieves the complete list of sensitive words currently being blocked.")
    @ApiResponse(responseCode = "200", description = "Successful retrieval")
    public ResponseEntity<List<WordResponse>> getWords() {
        return ResponseEntity.ok(service.getAllWords());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific word by ID", description = "Retrieves a single sensitive word using its database ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Word found"),
            @ApiResponse(responseCode = "404", description = "Word not found", content = @Content)
    })
    public ResponseEntity<WordResponse> getWordById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getWordById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing sensitive word", description = "Modifies an existing word and immediately updates the live regex cache.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Word successfully updated"),
            @ApiResponse(responseCode = "404", description = "Word not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict: The requested replacement word already exists", content = @Content)
    })
    public ResponseEntity<WordResponse> updateWord(
            @PathVariable Long id,
            @Valid @RequestBody WordRequest request) {
        return ResponseEntity.ok(service.updateWord(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a sensitive word", description = "Removes a word from the database and updates the live regex cache.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Word successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Word not found", content = @Content)
    })
    public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
        service.deleteWord(id);
        return ResponseEntity.noContent().build();
    }
}
