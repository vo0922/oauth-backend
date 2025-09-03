package org.oauth.resource.service;

import lombok.RequiredArgsConstructor;
import org.oauth.common.data.SERVICE_RESPONSE;
import org.oauth.common.dto.ResponseDto;
import org.oauth.common.exception.ServiceException;
import org.oauth.jpa.entity.User;
import org.oauth.jpa.repository.UserRepository;
import org.oauth.resource.dto.res.ResUserDto;
import org.oauth.resource.service.inf.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * packageName    : org.oauth.resource.service.inf
 * fileName       : UserServiceImpl
 * author         : sinuk
 * date           : 2025-09-03
 * description    : 사용자 서비스 구현체
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-03         sinuk       최초 생성
 **/

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public ResponseDto<ResUserDto> getUserInfo(String name) {

        User user = userRepository.findByUserId(name)
                .orElseThrow(() -> new ServiceException(SERVICE_RESPONSE.USER_NOT_FOUND));

        return ResponseDto.ofSuccess(
            ResUserDto.builder()
                    .userName(user.getUserId())
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .gender(user.getGender())
                    .createDateTime(user.getCreatedDateTime())
                    .build()
        );
    }
}