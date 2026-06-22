package dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload containing the sensitive word details")
public record WordResponse(
        @Schema(description = "The unique database ID of the word", example = "1")
        Long id,

        @Schema(description = "The sensitive word", example = "SELECT")
        String word
) {}
