package org.oauth.auth.config;

import lombok.RequiredArgsConstructor;
import org.oauth.common.properties.JwtProps;
import org.oauth.common.util.JwtUtil;
import org.oauth.common.util.PemKeyUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * packageName    : org.oauth.auth.config
 * fileName       : JwtConfig
 * author         : sinuk
 * date           : 2025-09-02
 * description    : Auth 모듈 Jwt 설정 클래스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProps.class)
public class JwtConfig {

    private final JwtProps jwtProps;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(jwtProps, PemKeyUtil.loadPublic(jwtProps.getPublicKeyPath()), PemKeyUtil.loadPrivate(jwtProps.getPrivateKeyPath()));
    }
}