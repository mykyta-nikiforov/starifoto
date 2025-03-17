package ua.in.photomap.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.in.photomap.user.model.AuthActionToken;

import java.util.Optional;

public interface AuthActionTokenRepository extends JpaRepository<AuthActionToken, Long> {
    Optional<AuthActionToken> findByToken(String token);
}
