package org.example.repository;

import org.example.model.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ListCrudRepository<User, Long> {

    User findByUsername(String username);
}
