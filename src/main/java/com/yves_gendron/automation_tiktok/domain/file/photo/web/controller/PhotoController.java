package com.yves_gendron.automation_tiktok.domain.file.photo.web.controller;

import com.yves_gendron.automation_tiktok.domain.file.photo.common.mapper.PhotoMapper;
import com.yves_gendron.automation_tiktok.domain.file.photo.model.Photo;
import com.yves_gendron.automation_tiktok.domain.file.photo.service.PhotoService;
import com.yves_gendron.automation_tiktok.domain.file.photo.web.dto.PhotoResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/photo")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoMapper photoMapper;

    private final PhotoService photoService;

    @GetMapping("/all")
    public ResponseEntity<List<PhotoResponse>> findAll() {
        List<Photo> photos = photoService.findAll();
        return ResponseEntity.ok(photoMapper.mapEntitiesToDtos(photos));
    }

    @GetMapping("/view/{id}")
    public void view(@PathVariable String id, HttpServletResponse response) {
        photoService.view(id, response);
    }

    @PostMapping("/upload")
    public void upload(@RequestBody MultipartFile file) {
        photoService.upload(file);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        photoService.delete(id);
    }

}
