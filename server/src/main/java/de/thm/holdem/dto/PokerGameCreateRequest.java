package de.thm.holdem.dto;

import de.thm.holdem.model.game.poker.TableType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PokerGameCreateRequest {

    @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters long.")
    @NotBlank(message = "Name must not be empty.")
    private String name;

    @Min(value = 100, message = "Buy in must be at least $100.")
    @Max(value = 1000000, message = "Buy in cannot exceed $1,000,000.")
    private int buyIn;

    @NotNull(message = "Table type must be set.")
    private TableType tableType;

    @Min(value = 2, message = "Minimum player count must be at least 2.")
    @Max(value = 5, message = "Maximum player count cannot exceed 5.")
    private int maxPlayerCount;
}
