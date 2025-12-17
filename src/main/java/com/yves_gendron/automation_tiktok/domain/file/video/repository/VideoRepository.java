package com.yves_gendron.automation_tiktok.domain.file.video.repository;

import com.yves_gendron.automation_tiktok.domain.file.video.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, String> {
}
