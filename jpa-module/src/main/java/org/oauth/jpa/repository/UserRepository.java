package org.oauth.jpa.repository;

import org.oauth.jpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * packageName    : org.oauth.jpamodule.repository
 * fileName       : UserRepository
 * author         : sinuk
 * date           : 2025-09-02
 * description    : UserRepository
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String id);
}