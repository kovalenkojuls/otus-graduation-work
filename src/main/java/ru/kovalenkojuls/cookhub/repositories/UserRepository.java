package ru.kovalenkojuls.cookhub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kovalenkojuls.cookhub.domains.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByActivationCode(String activationCode);
}

