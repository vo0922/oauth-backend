package org.oauth.auth.config;

import org.oauth.auth.service.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Optional;

/**
 * packageName    : org.oauth.auth.config
 * fileName       : SecurityConfig
 * author         : sinuk
 * date           : 2025-09-02
 * description    : Security 설정 클래스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@Configuration
public class SecurityConfig {

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            CustomUserDetailService userDetailService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider provider) {
        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(reg -> reg
                .requestMatchers("/auth/login", "/health", "/oauth2/token").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()
        );

        // 미인증 접근 시 React 로그인 페이지로 리다이렉트 (returnTo 포함)
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    String full = req.getRequestURL().toString();
                    String qs = req.getQueryString();
                    if (qs != null && !qs.isBlank()) full += "?" + qs;
                    String loginPage = "http://localhost:3000/login";
                    String location = loginPage + "?returnTo=" + java.net.URLEncoder.encode(full, java.nio.charset.StandardCharsets.UTF_8);
                    res.setStatus(302);
                    res.setHeader("Location", location);
                })
        );

        http.headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
        );

        http.formLogin(form -> form
                .loginProcessingUrl("/auth/login")
                .successHandler((req, res, auth) -> {
                    String returnTo = req.getParameter("returnTo");
                    String location = (returnTo != null && !returnTo.isBlank())
                            ? returnTo
                            : req.getContextPath() + "/oauth2/authorize"; // 필요 시 다른 기본 경로
                    res.setStatus(302);
                    res.setHeader("Location", location);
                })
                .failureHandler((req, res, ex) -> {
                    // React 로그인으로 되돌리되 에러 플래그 포함
                    String loginPage = "http://localhost:3000/login";
                    String returnTo = Optional.ofNullable(req.getParameter("returnTo")).orElse("");
                    String loc = loginPage + "?error=1&returnTo=" + java.net.URLEncoder.encode(returnTo, java.nio.charset.StandardCharsets.UTF_8);
                    res.setStatus(302);
                    res.setHeader("Location", loc);
                })
                .permitAll()
        );

        http.logout(l -> l.logoutUrl("/auth/logout"));

        http.cors(c -> c.configurationSource(req -> {
            CorsConfiguration conf = new CorsConfiguration();
            conf.setAllowedOrigins(java.util.List.of("http://localhost:3000"));
            conf.setAllowedMethods(java.util.List.of("GET","POST","PUT","DELETE","OPTIONS"));
            conf.setAllowedHeaders(java.util.List.of("*"));
            conf.setAllowCredentials(true);
            conf.setMaxAge(3600L);
            return conf;
        }));

        return http.build();
    }
}