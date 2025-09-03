package org.oauth.resource.controller;

import lombok.RequiredArgsConstructor;
import org.oauth.common.dto.ResponseDto;
import org.oauth.resource.dto.res.ResUserDto;
import org.oauth.resource.service.inf.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName    : org.oauth.resource.controller
 * fileName       : ResourceController
 * author         : sinuk
 * date           : 2025-09-03
 * description    : 리소스 컨트롤러
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-03         sinuk       최초 생성
 **/

@RestController
@RequiredArgsConstructor
public class ResourceController {

    private final UserService userService;

    @GetMapping("/info")
    public ResponseEntity<ResponseDto<ResUserDto>> info(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserInfo(authentication.getName()));
    }
}