package org.oauth.auth.service;

import lombok.RequiredArgsConstructor;
import org.oauth.common.data.SERVICE_RESPONSE;
import org.oauth.common.exception.ServiceException;
import org.oauth.jpa.entity.User;
import org.oauth.jpa.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.oauth.common.data.ConstParam.ROLE_USER;

/**
 * packageName    : org.oauth.auth.service
 * fileName       : CustomUserDetailService
 * author         : sinuk
 * date           : 2025-09-02
 * description    : UserDetailService 커스텀 클래스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User userEntity = userRepository.findByUserId(username)
                .orElseThrow(() -> new ServiceException(SERVICE_RESPONSE.USER_NOT_FOUND));

        boolean enabled = Boolean.TRUE.equals(userEntity.getEnabled());

        return org.springframework.security.core.userdetails.User
                .withUsername(userEntity.getUserId())
                .password(userEntity.getPassword())
                .authorities(ROLE_USER)
                .accountLocked(!enabled)
                .accountExpired(false)
                .credentialsExpired(false)
                .disabled(!enabled)
                .build();
    }
}