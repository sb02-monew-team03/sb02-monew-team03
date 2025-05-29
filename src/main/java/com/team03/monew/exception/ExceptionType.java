package com.team03.monew.exception;

public enum ExceptionType {

    USER("userException"), NOTIFICATION("notification"), NEWSARTICLE("newArticle"), INTEREST("interest"), COMMENT("comment"), ACTIVITY("activity"),  GLOBAL("globalException");;

    private final String type;

    ExceptionType(String type) {
        this.type = type;
    }
}
