package org.oauth.resource.service.inf;

import org.oauth.common.dto.ResponseDto;
import org.oauth.resource.dto.res.ResUserDto;

/**
 * packageName    : org.oauth.resource.service.inf
 * fileName       : UserService
 * author         : sinuk
 * date           : 2025-09-03
 * description    : 사용자 서비스 인터페이스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-03         sinuk       최초 생성
 **/

public interface UserService {
    ResponseDto<ResUserDto> getUserInfo(String name);
}