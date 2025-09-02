package org.oauth.jpa.repository;

import org.oauth.jpa.entity.AuthorizationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * packageName    : org.oauth.jpamodule.repository
 * fileName       : AuthorizationCodeRepository
 * author         : sinuk
 * date           : 2025-09-02
 * description    : AuthorizationCodeRepository
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

public interface AuthorizationCodeRepository extends JpaRepository<AuthorizationCode, Long> {

    List<AuthorizationCode> findTop50ByClient_ClientIdAndUsedFalseOrderByIdxDesc(String clientId);
}