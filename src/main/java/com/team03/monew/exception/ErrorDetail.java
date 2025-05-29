package com.team03.monew.exception;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErrorDetail {

    private String requiredType;
    private String parameter;
    private String value;

    public ErrorDetail(String requiredType, String parameter, String value) {
        this.requiredType = requiredType;
        this.parameter = parameter;
        this.value = value;
    }

}
