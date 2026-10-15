package com.auction.system.exception;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String errorCode,
        String message,
        String path
) {
    public static ErrorResponse of(ErrorCode code, String message, String path) {
        return new ErrorResponse(
                Instant.now(),
                code.getStatus().value(), 
                code.name(),
                message,
                path
        );
    }
}
