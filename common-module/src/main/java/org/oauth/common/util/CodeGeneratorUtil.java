package org.oauth.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * packageName    : org.oauth.common.util
 * fileName       : CodeGenerator
 * author         : sinuk
 * date           : 2025-09-02
 * description    : 랜덤 코드 생성 유틸
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@Component
public class CodeGeneratorUtil {

    @Value( "${util.code.generator.alphanum}")
    private String ALPHANUM;
    private static final SecureRandom RND = new SecureRandom();

    private CodeGeneratorUtil() {}

    public String randomCode(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(ALPHANUM.charAt(RND.nextInt(ALPHANUM.length())));
        return sb.toString();
    }
}