package org.oauth.auth.config;

import lombok.RequiredArgsConstructor;
import org.oauth.jpa.entity.Client;
import org.oauth.jpa.entity.User;
import org.oauth.jpa.repository.ClientRepository;
import org.oauth.jpa.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * packageName    : org.oauth.auth.config
 * fileName       : DevDataInitializer
 * author         : sinuk
 * date           : 2025-09-02
 * description    : 초기 데이터 1개 Insert
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-08-20         sinuk       최초 생성
 **/

@Configuration
@RequiredArgsConstructor
public class DevDataInitializer {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedDevData() {
        return args -> {
            // User
            userRepository.findByUserId("test")
                    .orElseGet(() -> userRepository.save(User.builder()
                            .userId("test")
                            .password(passwordEncoder.encode("1234"))
                            .userName("홍길동")
                            .email("test@local.dev")
                            .enabled(true)
                            .defaultScopes("read write")
                            .build()));

            // Client
            clientRepository.findByClientId("react-client")
                    .orElseGet(() -> clientRepository.save(Client.builder()
                            .clientId("react-client")
                            .clientSecret(passwordEncoder.encode("react-secret"))
                            .redirectUri("http://localhost:3000/callback")
                            .scope("read write")
                            .grantTypes("authorization_code refresh_token")
                            .publicClient(true) // SPA이면 true(= secret 검증 스킵, PKCE 권장)
                            .build()));
        };
    }
}