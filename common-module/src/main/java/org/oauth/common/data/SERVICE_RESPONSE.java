package org.oauth.common.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * packageName    : org.oauth.common.enumualation
 * fileName       : SERVICE_RESPONSE
 * author         : sinuk
 * date           : 2025-08-20
 * description    : 응답 정의 Enum
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@Getter
@RequiredArgsConstructor
public enum SERVICE_RESPONSE {

    // Const
    SUCCESS(0, "SUCCESS"),
    INVALID_REQUEST(1001, "Invalid Request"),
    UNAUTHORIZED(1002, "Unauthorized"),
    FORBIDDEN(1003, "Forbidden"),
    TOKEN_EXPIRED(1101, "Token Expired"),
    TOKEN_INVALID(1102, "Token Invalid"),
    PRIVATE_KEY_NOT_FOUND(1103, "Not Found PrivateKey"),
    PEM_KEY_INVALID(1104, "pem key Invalid"),
    FILE_NOT_FOUND(1105, "File Not Found"),
    SERVER_ERROR(9000, "Server Error")
    ;

    // Common

    // Authorization

    // Resource

    private final int code;
    private final String message;
}
