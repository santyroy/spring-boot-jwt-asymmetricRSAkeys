package com.unknownkoder.repository;

import com.unknownkoder.model.ApplicationUser;
import com.unknownkoder.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByUser(ApplicationUser user);
    Optional<RefreshToken> findByToken(String token);
}
