package com.yves_gendron.automation_tiktok.common.exception.handler;

import com.yves_gendron.automation_tiktok.common.exception.ApplicationAlreadyInProgressException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class MainExceptionHandler {

    @ExceptionHandler(ApplicationAlreadyInProgressException.class)
    public ResponseEntity<String> handleApplicationAlreadyInProgressException(ApplicationAlreadyInProgressException e) {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(409))
                .body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(400))
                .body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        String invalidFields = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatusCode.valueOf(400))
                .body(invalidFields);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String responseMessage = "Invalid request payload";
        Throwable throwable = e.getCause();

        if (throwable instanceof InvalidFormatException invalidFormatException) {
            Class<?> targetType = invalidFormatException.getTargetType();
            List<?> acceptedValues = targetType.isEnum()
                    ? Arrays.asList(targetType.getEnumConstants())
                    : List.of();

            if (!acceptedValues.isEmpty()) {
                responseMessage = "Accepted values are only: " + acceptedValues;
            }
        }

        return ResponseEntity
                .status(HttpStatusCode.valueOf(400))
                .body(responseMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException: ", e);

        return ResponseEntity
                .status(HttpStatusCode.valueOf(400))
                .body("Wrong argument passed");
    }
}
