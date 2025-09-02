package org.oauth.common.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.oauth.common.data.SERVICE_RESPONSE;
import org.oauth.common.properties.JwtProps;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static org.oauth.common.data.ConstParam.SCOPE;


/**
 * packageName    : org.oauth.common.util
 * fileName       : JwtUtil
 * author         : sinuk
 * date           : 2025-09-02
 * description    : Jwt 유틸 클래스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

public class JwtUtil {

    private final JwtProps props;
    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    public JwtUtil(JwtProps props, RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        this.props = props;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /** access token 생성 */
    public String createAccessToken(String sub, String clientId, String scope, Map<String, Object> extraClaims) {
        ensureSigner();
        Instant now = Instant.now();
        JWTClaimsSet.Builder b = new JWTClaimsSet.Builder()
                .issuer(props.getIssuer())
                .audience(clientId)
                .subject(sub)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(props.getAccessTokenTtlSec())))
                .jwtID(UUID.randomUUID().toString())
                .claim(SCOPE, scope);
        if (extraClaims != null) extraClaims.forEach(b::claim);

        try {
            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), b.build());
            jwt.sign(new RSASSASigner(privateKey));
            return jwt.serialize();
        } catch (Exception e) { throw new IllegalStateException(SERVICE_RESPONSE.TOKEN_INVALID.getMessage(), e); }
    }

    /** refresh token 생성 (subject, aud 만 최소 포함) */
    public String createRefreshToken(String sub, String clientId) {
        ensureSigner();
        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(props.getIssuer())
                .audience(clientId)
                .subject(sub)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(props.getRefreshTokenTtlSec())))
                .jwtID(UUID.randomUUID().toString())
                .build();
        try {
            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims);
            jwt.sign(new RSASSASigner(privateKey));
            return jwt.serialize();
        } catch (Exception e) { throw new IllegalStateException(SERVICE_RESPONSE.TOKEN_INVALID.getMessage(), e); }
    }

    /** 검증 + 파싱 */
    public JWTClaimsSet verifyAndParse(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            boolean ok = jwt.verify(new RSASSAVerifier(publicKey));
            if (!ok) throw new JOSEException(SERVICE_RESPONSE.TOKEN_INVALID.getMessage());
            JWTClaimsSet c = jwt.getJWTClaimsSet();
            if (c.getExpirationTime() == null || c.getExpirationTime().before(new Date())) {
                throw new JOSEException(SERVICE_RESPONSE.TOKEN_EXPIRED.getMessage());
            }
            return c;
        } catch (Exception e) { throw new IllegalArgumentException(SERVICE_RESPONSE.TOKEN_INVALID.getMessage(), e); }
    }

    private void ensureSigner() {
        if (privateKey == null) throw new IllegalStateException(SERVICE_RESPONSE.PRIVATE_KEY_NOT_FOUND.getMessage());
    }
}