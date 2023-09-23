package de.thm.holdem.model.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

@Data
@Document(collection = "userextra")
public class UserExtra {

    @Id
    private String username;
    private String avatar;
    private BigInteger bankroll;
    private Boolean isOnline;
    private Boolean isPlaying;
    private List<String> sessions;
    private List<String> channels;

    public UserExtra(String username) {
        this.username = username;
        this.isPlaying = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserExtra userExtra)) return false;
        return this.username.equals(userExtra.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.username);
    }
}