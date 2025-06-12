package com.team03.monew.exception;

import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorResponse errorResponse = ErrorResponse.from(e);
        return ResponseEntity.status(errorResponse.status())
                .body(errorResponse);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getFieldError();
        String parameter = fieldError.getField();
        String message = fieldError.getDefaultMessage();
        String value = String.valueOf(fieldError.getRejectedValue());
        ErrorDetail errorDetail = new ErrorDetail("value", parameter, value);
        CustomException customEx = new CustomException(ErrorCode.INVALID_INPUT_VALUE, errorDetail, ExceptionType.GLOBAL);
        ErrorResponse errorResponse = ErrorResponse.methodArgumentNotValidFrom(customEx, message);
        return ResponseEntity.status(errorResponse.status())
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error(" 처리되지 않은 서버 예외 발생", e);

        ErrorResponse errorResponse = new ErrorResponse(
                ZonedDateTime.now().toString(),
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),     // "INTERNAL_SERVER_ERROR"
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),  // "서버 내부 오류입니다."
                null,
                e.getClass().getSimpleName(),                  // 예: "NullPointerException"
                ErrorCode.INTERNAL_SERVER_ERROR.getStatus()    // 500
        );

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(errorResponse);
    }

}
