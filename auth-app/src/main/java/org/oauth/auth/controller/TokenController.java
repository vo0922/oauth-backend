package org.oauth.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.oauth.auth.dto.req.ReqTokenDto;
import org.oauth.auth.dto.res.ResTokenDto;
import org.oauth.auth.service.inf.TokenService;
import org.oauth.common.data.SERVICE_RESPONSE;
import org.oauth.common.dto.ResponseDto;
import org.oauth.common.exception.ServiceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.oauth.common.data.ConstParam.CODE;
import static org.oauth.common.data.ConstParam.REFRESH_TOKEN;

/**
 * packageName    : org.oauth.auth.controller
 * fileName       : TokenController
 * author         : sinuk
 * date           : 2025-09-02
 * description    : 토큰 컨트롤러
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping(value = "/oauth2/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ResponseDto<ResTokenDto>> token(HttpServletRequest request,
                                                          @Valid @ModelAttribute ReqTokenDto reqTokenDto) {
        String clientId = reqTokenDto.getClientId();
        String clientSecret = reqTokenDto.getClientSecret();

        String authz = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authz) && authz.startsWith("Basic ")) {
            String decoded = new String(Base64.getDecoder().decode(authz.substring(6)), StandardCharsets.UTF_8);
            int colon = decoded.indexOf(':');
            if (colon > 0) {
                clientId = decoded.substring(0, colon);
                clientSecret = decoded.substring(colon + 1);
            }
        }

        switch (reqTokenDto.getGrantType()) {
            case CODE:
                String code = reqTokenDto.getCode();
                String redirectUri = reqTokenDto.getRedirectUri();
                String codeVerifier = reqTokenDto.getCodeVerifier();
                return ResponseEntity.ok(tokenService.exchangeAuthorizationCode(clientId, clientSecret, code, redirectUri, codeVerifier));
            case REFRESH_TOKEN:
                String refresh = reqTokenDto.getRefreshToken();
                return ResponseEntity.ok(tokenService.refreshToken(clientId, refresh));
            default:
                throw new ServiceException(SERVICE_RESPONSE.GRANT_TYPE_UNSUPPORTED);
        }
    }
}