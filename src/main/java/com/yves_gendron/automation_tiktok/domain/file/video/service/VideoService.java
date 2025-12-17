package com.yves_gendron.automation_tiktok.domain.file.video.service;

import com.yves_gendron.automation_tiktok.common.helper.FileHelper;
import com.yves_gendron.automation_tiktok.domain.file.common.exception.FileNotFoundException;
import com.yves_gendron.automation_tiktok.domain.file.common.exception.UploadFileException;
import com.yves_gendron.automation_tiktok.domain.file.common.exception.ViewFileException;
import com.yves_gendron.automation_tiktok.domain.file.video.model.Video;
import com.yves_gendron.automation_tiktok.domain.file.video.repository.VideoRepository;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;

    private final FileHelper fileHelper;

    public List<Video> findAll() {
        return videoRepository.findAll();
    }

    public Video findByIdOrThrow(String id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("Video not found"));
    }

    @Transactional
    public void upload(MultipartFile file) {
        fileHelper.validateMultipart(file, "video");

        try {
            byte[] multipartFileBytes = file.getBytes();
            SerialBlob serialBlob = new SerialBlob(multipartFileBytes);

            Video video = Video.builder()
                    .title(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .dataBlob(serialBlob)
                    .build();

            videoRepository.save(video);
        } catch (IOException | SQLException | IllegalArgumentException e) {
            log.error(e.getMessage());
            throw new UploadFileException("Video upload failed");
        }
    }

    @Transactional
    public void view(String id, HttpServletResponse response) {
        Video video = findByIdOrThrow(id);

        try {
            byte[] bytes = fileHelper.readBlob(video.getDataBlob());
            response.setContentType(video.getContentType());
            response.setContentLength((int) video.getDataBlob().length());

            ServletOutputStream out = response.getOutputStream();
            out.write(bytes);
        } catch (SQLException | IOException e) {
            throw new ViewFileException("Exception while viewing video");
        }
    }

    public void delete(String id) {
        Video video = findByIdOrThrow(id);
        videoRepository.delete(video);
    }
}
