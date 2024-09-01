package com.ebit.authentication.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private LocalDateTime timestamp;
    private Integer statusCode;
    private String status;
    private String message;
    private T data;
}
