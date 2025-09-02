package org.oauth.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * packageName    : org.oauth.common.config
 * fileName       : JwtConfig
 * author         : sinuk
 * date           : 2025-09-02
 * description    : Jwt props 클래스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProps {

    private String privateKeyPath;
    private String publicKeyPath;
    private long accessTokenTtlSec;
    private long refreshTokenTtlSec;
    private String issuer;
}