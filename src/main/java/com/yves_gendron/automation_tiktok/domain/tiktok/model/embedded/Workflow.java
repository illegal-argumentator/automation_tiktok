package com.yves_gendron.automation_tiktok.domain.tiktok.model.embedded;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Workflow {

    @Embedded
    private VideoSetting videoSetting;

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VideoSetting {

        private LocalDateTime uploadAt;

    }

}
