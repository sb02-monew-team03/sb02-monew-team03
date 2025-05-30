package com.team03.monew.repository;

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
}
