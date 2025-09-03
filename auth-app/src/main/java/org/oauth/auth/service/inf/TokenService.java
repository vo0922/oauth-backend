/**
 * packageName    : org.oauth.auth.service.inf
 * fileName       : TokenService
 * author         : sinuk
 * date           : 2025-09-02
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/
package org.oauth.auth.service.inf;

import org.oauth.auth.dto.res.ResTokenDto;
import org.oauth.common.dto.ResponseDto;

public interface TokenService {
    ResponseDto<ResTokenDto> exchangeAuthorizationCode(String clientId, String clientSecret, String code, String redirectUri, String codeVerifier);

    ResponseDto<ResTokenDto> refreshToken(String clientId, String refresh);
}