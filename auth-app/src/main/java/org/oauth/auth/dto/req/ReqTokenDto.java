package org.oauth.auth.dto.req;

import lombok.*;

/**
 * packageName    : org.oauth.auth.dto.req
 * fileName       : ReqTokenDto
 * author         : sinuk
 * date           : 2025-09-02
 * description    : 토큰 요청 Dto
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqTokenDto {

    private String grantType;
    private String code;
    private String redirectUri;
    private String codeVerifier;
    private String clientId;
    private String clientSecret;
    private String refreshToken;
}