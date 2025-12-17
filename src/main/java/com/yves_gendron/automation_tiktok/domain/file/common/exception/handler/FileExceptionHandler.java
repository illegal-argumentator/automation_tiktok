package com.yves_gendron.automation_tiktok.domain.file.common.exception.handler;

import com.yves_gendron.automation_tiktok.domain.file.common.exception.FileNotFoundException;
import com.yves_gendron.automation_tiktok.domain.file.common.exception.UploadFileException;
import com.yves_gendron.automation_tiktok.domain.file.common.exception.ViewFileException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FileExceptionHandler {

    @ExceptionHandler(UploadFileException.class)
    public ResponseEntity<String> handleUploadFileException(UploadFileException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(ViewFileException.class)
    public ResponseEntity<String> handleViewFileException(ViewFileException e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<String> handleFileNotFoundException(FileNotFoundException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
