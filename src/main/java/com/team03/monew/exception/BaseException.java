package com.team03.monew.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;
    private final ErrorDetail details;
    private final ExceptionType exceptionType;

    protected BaseException(ErrorCode errorCode, ErrorDetail details, ExceptionType exceptionType) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
        this.exceptionType = exceptionType;
    }

}
