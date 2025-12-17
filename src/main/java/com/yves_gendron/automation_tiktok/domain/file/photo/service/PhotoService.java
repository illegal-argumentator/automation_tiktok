package com.yves_gendron.automation_tiktok.domain.file.photo.service;

import com.yves_gendron.automation_tiktok.domain.file.common.exception.FileNotFoundException;
import com.yves_gendron.automation_tiktok.domain.file.common.exception.UploadFileException;
import com.yves_gendron.automation_tiktok.domain.file.common.exception.ViewFileException;
import com.yves_gendron.automation_tiktok.domain.file.photo.model.Photo;
import com.yves_gendron.automation_tiktok.domain.file.photo.repository.PhotoRepository;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {

    @Value("${api.host}")
    private String host;

    @Value("${api.port}")
    private int port;

    private final PhotoRepository photoRepository;

    public List<Photo> findAll() {
        return photoRepository.findAll();
    }

    public Photo findById(String id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("Photo not found"));
    }

    public void view(String id, HttpServletResponse response) {
        Photo photo = findById(id);

        response.setContentType(photo.getContentType());
        response.setContentLength(photo.getData().length);

        try (ServletOutputStream out = response.getOutputStream()) {
            out.write(photo.getData());
        } catch (IOException e) {
            throw new ViewFileException("Exception while viewing video");
        }
    }

    public void delete(String id) {
        Photo photo = findById(id);
        photoRepository.delete(photo);
    }

    public String upload(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();

            Photo photo = Photo.builder()
                    .title(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .data(bytes)
                    .build();

            photoRepository.saveAndFlush(photo);
            return buildImageLink(photo.getId());
        } catch (IOException e) {
            throw new UploadFileException(e.getMessage());
        }
    }

    public String buildImageLink(String id) {
        return "http://%s:%d/api/photo/view/%s".formatted(host, port, id);
    }
}