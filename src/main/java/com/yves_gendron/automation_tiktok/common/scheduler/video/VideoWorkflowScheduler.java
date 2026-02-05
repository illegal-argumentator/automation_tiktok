package com.yves_gendron.automation_tiktok.common.scheduler.video;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class VideoWorkflowScheduler {

    private final VideoWorkflowOrchestrator videoWorkflowOrchestrator;

    @Scheduled(cron = "0 * * * * *")
    public void processVideosUploading() {
        log.info("Running video uploading job.");
        videoWorkflowOrchestrator.processVideosUploading();
    }

}
