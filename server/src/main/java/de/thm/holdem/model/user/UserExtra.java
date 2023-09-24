package de.thm.holdem.model.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Class to store additional information about a user.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@Data
@Document(collection = "userextra")
public class UserExtra {

    /** The id of the user from the JWT "sub" claim. */
    @Id
    private String id;

    /** The username of the user. */
    private String username;

    /** The avatar of the user. */
    private String avatar;

    /** The bankroll of the user. */
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