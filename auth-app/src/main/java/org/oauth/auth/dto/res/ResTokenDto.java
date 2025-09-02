package org.oauth.auth.dto.res;

import lombok.Builder;
import lombok.Data;

/**
 * packageName    : org.oauth.auth.dto.res
 * fileName       : ResTokenDto
 * author         : sinuk
 * date           : 2025-09-02
 * description    : 토큰 응답 Dto
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@Data
@Builder
public class ResTokenDto {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private String refreshToken;
    private String scope;
}