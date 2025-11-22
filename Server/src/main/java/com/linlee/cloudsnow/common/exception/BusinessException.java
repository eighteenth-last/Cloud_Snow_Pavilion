package com.linlee.cloudsnow.common.exception;

/**
 * 业务异常
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
