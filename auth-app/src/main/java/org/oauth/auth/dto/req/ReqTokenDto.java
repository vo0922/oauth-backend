package org.oauth.auth.dto.req;

import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "grant_type is required")
    private String grantType;
    private String responseType;
    @NotNull(message = "code is required")
    private String code;
    @NotNull(message = "redirect_uri is required")
    private String redirectUri;
    private String codeVerifier;
    @NotNull(message = "client_id is required")
    private String clientId;
    @NotNull(message = "client_secret is required")
    private String clientSecret;
    private String refreshToken;
}