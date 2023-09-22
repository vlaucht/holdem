package de.thm.holdem.model.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Data
@Document(collection = "userextra")
public class UserExtra {

    @Id
    private String username;
    private String avatar;
    private BigInteger bankroll;

    public UserExtra(String username) {
        this.username = username;
    }
}