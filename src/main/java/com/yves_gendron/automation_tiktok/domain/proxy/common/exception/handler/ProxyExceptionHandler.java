package com.yves_gendron.automation_tiktok.domain.proxy.common.exception.handler;

import com.yves_gendron.automation_tiktok.domain.proxy.common.exception.ProxyAlreadyExistsException;
import com.yves_gendron.automation_tiktok.domain.proxy.common.exception.ProxyNotAvailableException;
import com.yves_gendron.automation_tiktok.domain.proxy.common.exception.ProxyNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ProxyExceptionHandler {

    @ExceptionHandler(ProxyNotAvailableException.class)
    public ResponseEntity<String> handleProxyNotAvailableException(ProxyNotAvailableException e) {
        return ResponseEntity
                .internalServerError()
                .body(e.getMessage());
    }

    @ExceptionHandler(ProxyAlreadyExistsException.class)
    public ResponseEntity<String> handleProxyAlreadyExistsException(ProxyAlreadyExistsException e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(ProxyNotFoundException.class)
    public ResponseEntity<String> handleProxyNotFoundException(ProxyNotFoundException e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

}
