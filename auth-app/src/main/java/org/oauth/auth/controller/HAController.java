package org.oauth.auth.controller;

import org.oauth.common.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName    : org.oauth.auth.controller
 * fileName       : HAController
 * author         : sinuk
 * date           : 2025-09-02
 * description    : health check 컨트롤러
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@RestController
public class HAController {

    @GetMapping("/health")
    public ResponseEntity<ResponseDto<Void>> health() {
        return ResponseEntity.ok(ResponseDto.ofSuccess());
    }
}