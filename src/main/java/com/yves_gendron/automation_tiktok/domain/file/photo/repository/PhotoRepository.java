package com.yves_gendron.automation_tiktok.domain.file.photo.repository;

import com.yves_gendron.automation_tiktok.domain.file.photo.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, String> {
}
