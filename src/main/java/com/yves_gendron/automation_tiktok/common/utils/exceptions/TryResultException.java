package com.yves_gendron.automation_tiktok.common.utils.exceptions;

public class TryResultException extends RuntimeException {
    public TryResultException(Exception exception) {
        super(exception);
    }

    public TryResultException(String message) {
        super(message);
    }

}
