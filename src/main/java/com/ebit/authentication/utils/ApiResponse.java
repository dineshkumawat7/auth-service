package com.ebit.authentication.utils;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private Integer statusCode;
    private String status;
    private String message;
    private T data;
}
