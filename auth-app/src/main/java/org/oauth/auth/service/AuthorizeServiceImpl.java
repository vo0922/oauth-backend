package org.oauth.auth.service;

import lombok.RequiredArgsConstructor;
import org.oauth.auth.dto.req.ReqAuthorizeDto;
import org.oauth.auth.service.inf.AuthorizeService;
import org.oauth.common.data.SERVICE_RESPONSE;
import org.oauth.common.exception.ServiceException;
import org.oauth.common.util.CodeGeneratorUtil;
import org.oauth.jpa.entity.AuthorizationCode;
import org.oauth.jpa.entity.Client;
import org.oauth.jpa.entity.User;
import org.oauth.jpa.repository.AuthorizationCodeRepository;
import org.oauth.jpa.repository.ClientRepository;
import org.oauth.jpa.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static org.oauth.common.data.ConstParam.S256;

/**
 * packageName    : org.oauth.auth.service
 * fileName       : AuthorizeServiceImpl
 * author         : sinuk
 * date           : 2025-09-02
 * description    : AuthorizeServiceImpl
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@Service
@RequiredArgsConstructor
public class AuthorizeServiceImpl implements AuthorizeService {

    // util
    private final CodeGeneratorUtil codeGeneratorUtil;

    // repository
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final AuthorizationCodeRepository authorizationCodeRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String issueCode(String userName, ReqAuthorizeDto reqAuthorizeDto) {

        Client client = clientRepository.findByClientId(reqAuthorizeDto.getClientId())
                .orElseThrow(() -> new ServiceException(SERVICE_RESPONSE.CLIENT_ID_INVALID));

        if (!client.getRedirectUri().equals(reqAuthorizeDto.getRedirectUri())) {
            throw new ServiceException(SERVICE_RESPONSE.REDIRECT_URI_NOT_MATCH);
        }

        User user = userRepository.findByUserId(userName)
                .orElseThrow(() -> new ServiceException(SERVICE_RESPONSE.USER_NOT_FOUND));

        if (Boolean.TRUE.equals(client.getPublicClient())) {
            if (!S256.equalsIgnoreCase(reqAuthorizeDto.getCodeChallengeMethod()) ||
                    reqAuthorizeDto.getCodeChallenge() == null) {
                throw new ServiceException(SERVICE_RESPONSE.PUBLIC_CLIENT_REQUIRED);
            }
        }

        String codePlain = codeGeneratorUtil.randomCode(48);
        String codeHash = BCrypt.hashpw(codePlain, BCrypt.gensalt());

        AuthorizationCode entity = AuthorizationCode.builder()
                .hashValue(codeHash)
                .client(client)
                .user(user)
                .redirectUri(reqAuthorizeDto.getRedirectUri())
                .scope(Optional.ofNullable(reqAuthorizeDto.getScope())
                        .filter(s -> !s.isBlank())
                        .orElse(client.getScope()))
                .codeChallenge(reqAuthorizeDto.getCodeChallenge())
                .codeChallengeMethod(reqAuthorizeDto.getCodeChallenge())
                .expireTime(Instant.now().plusSeconds(60))
                .used(false)
                .build();

        authorizationCodeRepository.save(entity);

        return codePlain;
    }
}