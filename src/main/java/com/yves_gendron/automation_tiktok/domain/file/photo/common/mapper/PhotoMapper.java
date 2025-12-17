package com.yves_gendron.automation_tiktok.domain.file.photo.common.mapper;

import com.yves_gendron.automation_tiktok.domain.file.photo.model.Photo;
import com.yves_gendron.automation_tiktok.domain.file.photo.service.PhotoService;
import com.yves_gendron.automation_tiktok.domain.file.photo.web.dto.PhotoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PhotoMapper {

    private final PhotoService photoService;

    public List<PhotoResponse> mapEntitiesToDtos(List<Photo> photos) {
        return photos.stream()
                .map(this::mapEntityToDto)
                .toList();
    }

    public PhotoResponse mapEntityToDto(Photo photo) {
        return PhotoResponse.builder()
                .id(photo.getId())
                .title(photo.getTitle())
                .photoLink(photoService.buildImageLink(photo.getId()))
                .createdDate(photo.getCreatedDate())
                .lastModifiedDate(photo.getLastModifiedDate())
                .build();
    }
}
