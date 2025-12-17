package com.yves_gendron.automation_tiktok.domain.file.video.common.mapper;

import com.yves_gendron.automation_tiktok.domain.file.video.model.Video;
import com.yves_gendron.automation_tiktok.domain.file.video.web.dto.VideoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VideoMapper {

    @Value("${api.host}")
    private String host;

    @Value("${api.port}")
    private int port;

    public List<VideoResponse> mapEntitiesToDtos(List<Video> videos) {
        return videos.stream()
                .map(this::mapEntityToDto)
                .toList();
    }

    public VideoResponse mapEntityToDto(Video video) {
        return VideoResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .videoLink(buildVideoLink(video.getId()))
                .createdDate(video.getCreatedDate())
                .lastModifiedDate(video.getLastModifiedDate())
                .build();
    }

    private String buildVideoLink(String id) {
        return "http://%s:%d/api/video/view/%s".formatted(host, port, id);
    }
}
