package com.team03.monew.util;

import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;

public enum OrderBy {

    CREATED_AT("createdAt"),
    LIKE_COUNT("likeCount");

    private final String value;

    OrderBy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static OrderBy fromValue(String value) {
        for (OrderBy orderBy : OrderBy.values()) {
            if (orderBy.value.equalsIgnoreCase(value)) {
                return orderBy;
            }
        }
        ErrorDetail detail = new ErrorDetail("OrderBy(createdAt, likeCount)", "orderBy", value);
        throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, detail, ExceptionType.COMMENT);
    }
}
