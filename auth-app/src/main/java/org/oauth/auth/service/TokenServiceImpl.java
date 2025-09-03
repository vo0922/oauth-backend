package org.oauth.auth.service;

import lombok.RequiredArgsConstructor;
import org.oauth.auth.dto.res.ResTokenDto;
import org.oauth.auth.service.inf.TokenService;
import org.oauth.common.data.SERVICE_RESPONSE;
import org.oauth.common.dto.ResponseDto;
import org.oauth.common.exception.ServiceException;
import org.oauth.common.properties.JwtProps;
import org.oauth.common.util.JwtUtil;
import org.oauth.jpa.entity.AuthorizationCode;
import org.oauth.jpa.entity.Client;
import org.oauth.jpa.entity.RefreshToken;
import org.oauth.jpa.repository.AuthorizationCodeRepository;
import org.oauth.jpa.repository.ClientRepository;
import org.oauth.jpa.repository.RefreshTokenRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;

import static org.oauth.common.data.ConstParam.*;

/**
 * packageName    : org.oauth.auth.service
 * fileName       : TokenServiceImpl
 * author         : sinuk
 * date           : 2025-09-02
 * description    : TokenServiceImpl
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    // utils
    private final JwtUtil jwtUtil;
    private final JwtProps jwtProps;
    private final PasswordEncoder passwordEncoder;

    // repository
    private final ClientRepository clientRepository;
    private final AuthorizationCodeRepository authorizationCodeRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public ResponseDto<ResTokenDto> exchangeAuthorizationCode(String clientId,
                                                              String clientSecret,
                                                              String code,
                                                              String redirectUri,
                                                              String codeVerifier) {
        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new ServiceException(SERVICE_RESPONSE.CLIENT_ID_INVALID));

        // confidential client면 secret 검증
        if (!Boolean.TRUE.equals(client.getPublicClient())) {
            if (clientSecret == null || !passwordEncoder.matches(clientSecret, client.getClientSecret())) {
                throw new ServiceException(SERVICE_RESPONSE.CLIENT_SECRET_INVALID);
            }
        }

        // 최근 미사용 코드들 중에서 해시 매칭되는 코드 찾기
        AuthorizationCode authorizationCode = authorizationCodeRepository
                .findTop50ByClient_ClientIdAndUsedFalseOrderByIdxDesc(clientId)
                .stream()
                .filter(c -> BCrypt.checkpw(code, c.getHashValue()))
                .findFirst()
                .orElseThrow(() -> new ServiceException(SERVICE_RESPONSE.AUTHORIZE_CODE_INVALID));

        // 만료/재사용/리다이렉트URI 검증
        if (Boolean.TRUE.equals(authorizationCode.getUsed())) throw new IllegalArgumentException("code_already_used");
        if (authorizationCode.getExpireTime().isBefore(Instant.now())) throw new IllegalArgumentException("code_expired");
        if (!Objects.equals(authorizationCode.getRedirectUri(), redirectUri)) throw new IllegalArgumentException("invalid_redirect_uri");

        // PKCE 검증 (public client 권장/강제)
        if (authorizationCode.getCodeChallengeMethod() != null) {
            if (S256.equalsIgnoreCase(authorizationCode.getCodeChallengeMethod())) {
                if (clientSecret == null || !verifyS256(authorizationCode.getCodeChallenge(), clientSecret)) {
                    throw new ServiceException(SERVICE_RESPONSE.CODE_VERIFIER_INVALID);
                }
            } else if (PLAIN.equalsIgnoreCase(authorizationCode.getCodeChallengeMethod())) {
                if (clientSecret == null || !authorizationCode.getCodeChallenge().equals(clientSecret)) {
                    throw new ServiceException(SERVICE_RESPONSE.CODE_VERIFIER_INVALID);
                }
            }
        }

        // 액세스 토큰 & 리프레시 토큰 발급
        String scope = Optional.ofNullable(authorizationCode.getScope()).orElse(client.getScope());
        String sub = authorizationCode.getUser().getUserId();

        String access = jwtUtil.createAccessToken(sub, clientId, scope, Map.of());
        String refresh = jwtUtil.createRefreshToken(sub, clientId);

        // refresh 해시 저장
        String refreshHash = BCrypt.hashpw(refresh, BCrypt.gensalt());
        refreshTokenRepository.save(RefreshToken.builder()
                .user(authorizationCode.getUser())
                .client(client)
                .hashValue(refreshHash)
                .expireTime(Instant.now().plusSeconds(jwtProps.getRefreshTokenTtlSec())) // 14일 (JwtProps와 일치하도록 관리)
                .revoked(false)
                .metadata(null)
                .build());

        authorizationCodeRepository.save(authorizationCode.toBuilder()
                .used(true)
                .build());

        return ResponseDto.ofSuccess(
                ResTokenDto.builder()
                        .accessToken(access)
                        .tokenType(BEARER)
                        .expiresIn(jwtProps.getAccessTokenTtlSec())
                        .refreshToken(refresh)
                        .scope(scope)
                        .build());
    }

    @Override
    public ResponseDto<ResTokenDto> refreshToken(String clientId, String refresh) {

        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new ServiceException(SERVICE_RESPONSE.CLIENT_ID_INVALID));

        RefreshToken refreshToken = refreshTokenRepository.findByClientAndRevokedAndExpireTimeAfter(client, true, Instant.now())
                .orElseThrow(() -> new ServiceException(SERVICE_RESPONSE.REFRESH_TOKEN_INVALID));
        if (!BCrypt.checkpw(refresh, refreshToken.getHashValue())) {
            throw new ServiceException(SERVICE_RESPONSE.REFRESH_TOKEN_INVALID);
        }

        String sub = refreshToken.getUser().getUserId();
        String scope = Optional.ofNullable(refreshToken.getClient().getScope()).orElse(READ);
        String access = jwtUtil.createAccessToken(sub, clientId, scope, Map.of());

        return ResponseDto.ofSuccess(
                ResTokenDto.builder()
                        .accessToken(access)
                        .tokenType(BEARER)
                        .expiresIn(jwtProps.getAccessTokenTtlSec())
                        .scope(scope)
                        .build()
        );
    }

    private boolean verifyS256(String expectedChallenge, String verifier) {
        try {
            MessageDigest md = MessageDigest.getInstance(SHA256);
            byte[] digest = md.digest(verifier.getBytes(StandardCharsets.US_ASCII));
            String actual = base64UrlNoPad(digest);
            return Objects.equals(expectedChallenge, actual);
        } catch (Exception e) {
            return false;
        }
    }

    private static String base64UrlNoPad(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}