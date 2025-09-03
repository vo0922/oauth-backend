package org.oauth.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.oauth.common.data.SERVICE_RESPONSE;
import org.oauth.common.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * packageName    : org.oauth.common.exception
 * fileName       : CustomExceptionHandler
 * author         : sinuk
 * date           : 2025-09-02
 * description    : Exception 핸들러 클래스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ResponseDto<Void>> handleService(ServiceException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.ofFail(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Void>> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getAllErrors().isEmpty()
                ? SERVICE_RESPONSE.INVALID_REQUEST.getMessage()
                : e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.ofFail(SERVICE_RESPONSE.INVALID_REQUEST.getCode(), msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handleOthers(Exception e) {
        log.error("Exception Occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.ofFail(SERVICE_RESPONSE.SERVER_ERROR.getCode(),
                        e.getMessage()));
    }
}