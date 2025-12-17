package com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.handler;

import com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.*;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.handler.service.TikTokExceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class TikTokExceptionHandler {

    private final TikTokExceptionService tikTokExceptionService;

    @ExceptionHandler(TikTokCaptchaException.class)
    ResponseEntity<String> handleTikTokCaptchaException(TikTokCaptchaException e) {
        String responseMessage = tikTokExceptionService.handleTikTokCaptchaException(e);
        return ResponseEntity.internalServerError().body(responseMessage);
    }

    @ExceptionHandler(TikTokBlockActivityException.class)
    ResponseEntity<String> handleTikTokBlockActivityException(TikTokBlockActivityException e) {
        String responseMessage = tikTokExceptionService.handleTikTokBlockActivityException(e);
        return ResponseEntity.internalServerError().body(responseMessage);
    }

    @ExceptionHandler(TikTokActionException.class)
    ResponseEntity<String> handleTikTokActionException(TikTokActionException e) {
        String responseMessage = tikTokExceptionService.handleTikTokActionException(e);
        return ResponseEntity.internalServerError().body(responseMessage);
    }

    @ExceptionHandler(TikTokCreationException.class)
    ResponseEntity<String> handleTikTokCreationException(TikTokCreationException e) {
        String responseMessage = tikTokExceptionService.handleTikTokCreationException(e);
        return ResponseEntity.internalServerError().body(responseMessage);
    }

    @ExceptionHandler(TikTokAccountNotFoundException.class)
    ResponseEntity<String> handleTikTokAccountNotFoundException(TikTokAccountNotFoundException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
