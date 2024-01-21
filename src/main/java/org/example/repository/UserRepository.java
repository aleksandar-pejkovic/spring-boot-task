package org.example.repository;

import java.util.Optional;

import org.example.model.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ListCrudRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Long countByUsernameContaining(String baseUsername);
}
