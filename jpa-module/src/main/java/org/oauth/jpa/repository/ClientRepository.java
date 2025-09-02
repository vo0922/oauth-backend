package org.oauth.jpa.repository;

import org.oauth.jpa.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * packageName    : org.oauth.jpamodule.repository
 * fileName       : ClientRepository
 * author         : sinuk
 * date           : 2025-09-02
 * description    : ClientRepository
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByClientId(String id);
}