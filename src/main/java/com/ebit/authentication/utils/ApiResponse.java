package com.ebit.authentication.utils;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private LocalDateTime timestamp;
    private String status;
    private String message;
    private T data;
}
