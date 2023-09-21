package de.thm.holdem.model.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "userextra")
public class UserExtra {

    @Id
    private String username;
    private String avatar;
    private int bankroll;

    public UserExtra(String username) {
        this.username = username;
    }
}