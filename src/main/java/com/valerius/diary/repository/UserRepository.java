package com.valerius.diary.repository;

import com.valerius.diary.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Persistence access for application accounts.
 */
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
