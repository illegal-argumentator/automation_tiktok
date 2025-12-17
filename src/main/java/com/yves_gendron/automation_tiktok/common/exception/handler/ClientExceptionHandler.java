package com.yves_gendron.automation_tiktok.common.exception.handler;

import com.yves_gendron.automation_tiktok.system.client.nst.common.exception.NstBrowserException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ClientExceptionHandler {

    @ExceptionHandler(NstBrowserException.class)
    public ResponseEntity<String> handleNstBrowserException(NstBrowserException e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
    }
}
