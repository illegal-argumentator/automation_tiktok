package com.yves_gendron.automation_tiktok.common.exception;

public class ApplicationAlreadyInProgressException extends RuntimeException {
    public ApplicationAlreadyInProgressException(String message) {
        super(message);
    }
}
