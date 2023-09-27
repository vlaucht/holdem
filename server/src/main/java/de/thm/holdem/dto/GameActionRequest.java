package de.thm.holdem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class GameActionRequest {

    @NotBlank(message = "Please provide a game id.")
    private String gameId;

    @NotBlank(message = "Please provide an action.")
    @Pattern(
            regexp = "^(fold|check|call|raise|allIn)$",
            message = "Invalid action."
    )
    private String action;

    private int amount;
}
