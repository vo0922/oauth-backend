package org.oauth.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.oauth.auth.dto.req.ReqAuthorizeDto;
import org.oauth.auth.service.inf.AuthorizeService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.oauth.common.data.ConstParam.*;

/**
 * packageName    : org.oauth.auth.controller
 * fileName       : AuthorizeController
 * author         : sinuk
 * date           : 2025-09-02
 * description    : Authorize 컨트롤러
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@RestController
@RequiredArgsConstructor
public class AuthorizeController {

    private final AuthorizeService authorizeService;

    @GetMapping("/oauth2/authorize")
    public ResponseEntity<Void> authorize(@Valid @ModelAttribute ReqAuthorizeDto reqAuthorizeDto,
                                      Authentication authentication,
                                      HttpServletResponse response) {

        if (!CODE.equals(reqAuthorizeDto.getResponseType())) {
            return ResponseEntity.badRequest().build();
        }
        String userName = authentication.getName();

        String code = authorizeService.issueCode(
                userName, reqAuthorizeDto
        );

        String location = reqAuthorizeDto.getRedirectUri() + "?code=" + URLEncoder.encode(code, StandardCharsets.UTF_8);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(location))
                .build();
    }
}