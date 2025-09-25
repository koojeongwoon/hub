package com.tinyquest.hub.shared.error;

import com.tinyquest.hub.shared.constants.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] messageArguments;
    private final String overrideMessage;

    public BusinessException(ErrorCode errorCode, Object... messageArguments) {
        super(errorCode.getCode());
        this.errorCode = errorCode;
        this.messageArguments = messageArguments;
        this.overrideMessage = null;
    }

    public BusinessException(ErrorCode errorCode, String overrideMessage, Object... messageArguments) {
        super(overrideMessage);
        this.errorCode = errorCode;
        this.messageArguments = messageArguments;
        this.overrideMessage = overrideMessage;
    }
}
