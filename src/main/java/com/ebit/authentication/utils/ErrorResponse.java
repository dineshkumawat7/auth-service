package com.ebit.authentication.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ErrorResponse {
    private int statusCode;
    private String status;
    private String message;
}
