package com.xuannie.devatlas.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.time.Instant;

// TODO Understand how this works
public class ErrorResponseFactory {
    public static ProblemDetail of(HttpStatus status, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setDetail(detail);
        problemDetail.setProperty("Timestamp", Instant.now());

        return  problemDetail;
    }
}
