package org.oauth.jpa.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * packageName    : org.oauth.jpamodule.config
 * fileName       : JpaConfig
 * author         : sinuk
 * date           : 2025-09-02
 * description    : JPA 설정 클래스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@Configuration
@EnableJpaRepositories(basePackages = "org.oauth.jpa.repository")
@EntityScan(basePackages = "org.oauth.jpa.entity")
@EnableJpaAuditing
public class JpaConfig {
}