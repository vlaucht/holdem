package de.thm.holdem.model.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.Objects;

@Data
@Document(collection = "userextra")
public class UserExtra {

    @Id
    private String id;
    private String username;
    private String avatar;
    private BigInteger bankroll;


    public UserExtra(String id, String username) {
        this.id = id;
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserExtra userExtra)) return false;
        return this.id.equals(userExtra.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}