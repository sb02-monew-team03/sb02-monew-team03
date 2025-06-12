package com.team03.monew.exception;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ErrorResponse(
        String timestamp,
        String code,
        String message,
        ErrorDetail details,
        String exceptionType,
        int status
) {
    public static ErrorResponse from(BaseException ex) {
        return new ErrorResponse(LocalDateTime.now().toString(), ex.getErrorCode().toString(), ex.getMessage(), ex.getDetails(), ex.getExceptionType().toString(), ex.getErrorCode().getStatus());
    }

    public static ErrorResponse methodArgumentNotValidFrom(CustomException e, String message) {
        return new ErrorResponse(LocalDate.now().toString(), e.getErrorCode().toString(), message, e.getDetails(), e.getExceptionType().toString(), e.getErrorCode().getStatus());
    }

}
