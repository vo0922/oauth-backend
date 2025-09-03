package org.oauth.resource.filter;

import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.oauth.common.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.oauth.common.data.ConstParam.BEARER;
import static org.oauth.common.data.ConstParam.SCOPE;

/**
 * packageName    : org.oauth.resource.filter
 * fileName       : JwtAuthFilter
 * author         : sinuk
 * date           : 2025-09-03
 * description    : Jwt 인증 필터
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-03         sinuk       최초 생성
 **/

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(auth) && auth.startsWith(BEARER + " ")) {
            try {
                String token = auth.substring(7);
                JWTClaimsSet claimsSet = jwtUtil.verifyAndParse(token);
                String scope = claimsSet.getStringClaim(SCOPE);
                List<SimpleGrantedAuthority> authorities = Arrays.stream(scope == null ? new String[0] : scope.split("\\s+"))
                        .filter(s -> !s.isBlank())
                        .map(s -> new SimpleGrantedAuthority(SCOPE + "_" + s))
                        .collect(Collectors.toList());

                AbstractAuthenticationToken authentication = new AbstractAuthenticationToken(authorities) {
                    @Override public Object getCredentials() { return token; }
                    @Override public Object getPrincipal() { return claimsSet.getSubject(); }
                };
                authentication.setAuthenticated(true);
                org.springframework.security.core.context.SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            } catch (Exception ignore) {
            }
        }

        filterChain.doFilter(request, response);
    }
}