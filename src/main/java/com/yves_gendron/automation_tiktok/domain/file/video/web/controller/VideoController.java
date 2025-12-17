package com.yves_gendron.automation_tiktok.domain.file.video.web.controller;

import com.yves_gendron.automation_tiktok.domain.file.video.common.mapper.VideoMapper;
import com.yves_gendron.automation_tiktok.domain.file.video.model.Video;
import com.yves_gendron.automation_tiktok.domain.file.video.service.VideoService;
import com.yves_gendron.automation_tiktok.domain.file.video.web.dto.VideoResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/video")
public class VideoController {

    private final VideoService videoService;

    private final VideoMapper videoMapper;

    @GetMapping("/all")
    public ResponseEntity<List<VideoResponse>> findAll() {
        List<Video> videos = videoService.findAll();
        return ResponseEntity.ok(videoMapper.mapEntitiesToDtos(videos));
    }

    @GetMapping("/view/{id}")
    public void view(@PathVariable String id, HttpServletResponse response) {
        videoService.view(id, response);
    }

    @PostMapping("/upload")
    public void upload(@RequestBody MultipartFile file) {
        videoService.upload(file);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        videoService.delete(id);
    }
}
