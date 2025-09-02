package org.oauth.common.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.oauth.common.data.SERVICE_RESPONSE;
import org.oauth.common.exception.ServiceException;

/**
 * packageName    : org.oauth.common.dto
 * fileName       : ResponseDto
 * author         : sinuk
 * date           : 2025-09-02
 * description    : 공통 응답 Dto
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@JsonInclude(JsonInclude.Include.ALWAYS)
@Data
@Builder
public class ResponseDto<T> {

    private final Integer code;
    private final String message;
    private T data;

    private ResponseDto(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private ResponseDto(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseDto<T> ofSuccess() {
        return new ResponseDto<>(SERVICE_RESPONSE.SUCCESS.getCode(), SERVICE_RESPONSE.SUCCESS.getMessage());
    }

    public static <T> ResponseDto<T> ofSuccess(T data) {
        return new ResponseDto<>(SERVICE_RESPONSE.SUCCESS.getCode(), SERVICE_RESPONSE.SUCCESS.getMessage(), data);
    }

    public static <T> ResponseDto<T> ofFail(Integer code, String message) {
        return new ResponseDto<>(code, message);
    }

    public static <T> ResponseDto<T> ofFail(Integer code, String message, T data) {
        return new ResponseDto<>(code, message, data);
    }

    public static <T> ResponseDtoBuilder<T> of(SERVICE_RESPONSE serviceResponse) {
        return ResponseDto.<T>builder()
                .code(serviceResponse.getCode())
                .message(serviceResponse.getMessage());
    }

    public static <T> ResponseDtoBuilder<T> ofError(ServiceException serviceException) {
        return ResponseDto.<T>builder()
                .code(serviceException.getCode())
                .message(serviceException.getMessage());
    }
}