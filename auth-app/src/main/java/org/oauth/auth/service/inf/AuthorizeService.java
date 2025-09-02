/**
 * packageName    : org.oauth.auth.service.inf
 * fileName       : AuthorizeService
 * author         : sinuk
 * date           : 2025-09-02
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/
package org.oauth.auth.service.inf;

import org.oauth.auth.dto.req.ReqAuthorizeDto;

public interface AuthorizeService {
    String issueCode(String userName, ReqAuthorizeDto reqAuthorizeDto);
}