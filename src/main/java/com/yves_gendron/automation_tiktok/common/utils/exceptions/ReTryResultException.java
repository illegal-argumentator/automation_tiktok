package com.yves_gendron.automation_tiktok.common.utils.exceptions;

public class ReTryResultException extends RuntimeException {
    public ReTryResultException(Exception exception) {
        super(exception);
    }

    public ReTryResultException(String message) {
        super(message);
    }
}
