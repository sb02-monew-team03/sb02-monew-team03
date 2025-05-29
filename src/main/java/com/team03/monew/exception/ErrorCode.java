package com.team03.monew.exception;

public enum ErrorCode {

    // 400 Bad Request
    INVALID_TYPE_VALUE(400, "INVALID_TYPE_VALUE", "잘못된 타입이 입력되었습니다."),
    INVALID_INPUT_VALUE(400, "INVALID_INPUT_VALUE", "입력값이 유효하지 않습니다."),
    MISSING_REQUEST_PARAMETER(400, "MISSING_REQUEST_PARAMETER", "필수 요청 파라미터가 누락되었습니다."),
    TYPE_MISMATCH(400, "TYPE_MISMATCH", "요청 데이터 타입이 올바르지 않습니다."),
    INVALID_JSON_FORMAT(400, "INVALID_JSON_FORMAT", "잘못된 JSON 포맷입니다."),
    DUPLICATE_RESOURCE(400, "DUPLICATE_RESOURCE", "이미 존재하는 리소스입니다."),
    CONSTRAINT_VIOLATION(400, "CONSTRAINT_VIOLATION", "제약 조건을 위반하였습니다."),

    // 401 Unauthorized
    UNAUTHORIZED(401, "UNAUTHORIZED", "인증이 필요합니다."),
    TOKEN_EXPIRED(401, "TOKEN_EXPIRED", "토큰이 만료되었습니다."),
    INVALID_TOKEN(401, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),

    // 403 Forbidden
    FORBIDDEN(403, "FORBIDDEN", "접근 권한이 없습니다."),

    // 404 Not Found
    RESOURCE_NOT_FOUND(404, "RESOURCE_NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
    ENDPOINT_NOT_FOUND(404, "ENDPOINT_NOT_FOUND", "요청한 API 경로가 존재하지 않습니다."),

    // 409 Conflict
    CONFLICT(409, "CONFLICT", "리소스 충돌이 발생했습니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 내부 오류입니다."),
    DATABASE_ERROR(500, "DATABASE_ERROR", "데이터베이스 오류가 발생했습니다."),
    IO_ERROR(500, "IO_ERROR", "입출력 처리 중 오류가 발생했습니다."),
    EXTERNAL_API_ERROR(500, "EXTERNAL_API_ERROR", "외부 API 호출 중 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
