package org.oauth.common.exception;

import lombok.Getter;
import org.oauth.common.data.SERVICE_RESPONSE;

@Getter
public class ServiceException extends RuntimeException {
    private final int code;
    private final String message;

    public ServiceException(int code, String message) {
        super(message); this.code = code; this.message = message;
    }
    public ServiceException(SERVICE_RESPONSE resp) {
        super(resp.getMessage()); this.code = resp.getCode(); this.message = resp.getMessage();
    }
}
