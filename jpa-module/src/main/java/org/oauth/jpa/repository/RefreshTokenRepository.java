package org.oauth.jpa.repository;

import org.oauth.jpa.entity.Client;
import org.oauth.jpa.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

/**
 * packageName    : org.oauth.jpamodule.repository
 * fileName       : RefreshTokenRepository
 * author         : sinuk
 * date           : 2025-09-02
 * description    : RefreshTokenRepository
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByClientAndRevokedAndExpireTimeAfter(Client client, boolean b, Instant now);
}