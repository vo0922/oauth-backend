package org.oauth.auth.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName    : org.oauth.auth.dto.req
 * fileName       : ReqAuthorizeDto
 * author         : sinuk
 * date           : 2025-09-02
 * description    : AuthorizeCode 요청 Dto
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqAuthorizeDto {

    @NotNull(message = "response_type is required")
    private String responseType;
    @NotNull(message = "client_id is required")
    private String clientId;
    @NotNull(message = "redirect_uri is required")
    private String redirectUri;
    private String scope;
    private String state;
    private String codeChallenge;
    private String codeChallengeMethod;
}