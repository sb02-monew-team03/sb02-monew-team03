package com.team03.monew.exception;

public class CustomException extends BaseException{

    public CustomException(ErrorCode errorCode, ErrorDetail details, ExceptionType exceptionType) {
        super(errorCode, details, exceptionType);
    }

}
