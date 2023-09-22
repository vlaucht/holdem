package de.thm.holdem.repository;

import de.thm.holdem.model.user.UserExtra;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for UserExtra.
 *
 * @see de.thm.holdem.model.user.UserExtra
 *
 * @author Valentin Laucht
 * @version 1.0
 */
@Repository
public interface UserRepository extends MongoRepository<UserExtra, String> {
}
