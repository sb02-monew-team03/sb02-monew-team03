package com.team03.monew.exception;

public class CustomException extends BaseException{

    protected CustomException(ErrorCode errorCode, ErrorDetail details, ExceptionType exceptionType) {
        super(errorCode, details, exceptionType);
    }
}
