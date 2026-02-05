package com.yves_gendron.automation_tiktok.common.scheduler.video;

import com.yves_gendron.automation_tiktok.common.command.ActionCommand;
import com.yves_gendron.automation_tiktok.common.factory.ActionActionFactory;
import com.yves_gendron.automation_tiktok.common.type.Action;
import com.yves_gendron.automation_tiktok.common.type.Platform;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.embedded.Workflow;
import com.yves_gendron.automation_tiktok.domain.tiktok.service.TikTokCommandPort;
import com.yves_gendron.automation_tiktok.domain.tiktok.service.TikTokQueryPort;
import com.yves_gendron.automation_tiktok.system.controller.dto.VideoActionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoWorkflowOrchestrator {

    private final TikTokCommandPort tikTokCommandPort;

    private final TikTokQueryPort tikTokQueryPort;

    private final ActionActionFactory actionActionFactory;

    private final Platform platform = Platform.TIKTOK;

    private final Action action = Action.VIDEO;

    public void processVideosUploading() {
        List<TikTokAccount> allWithVideoWorkflow = tikTokQueryPort.getAllWithVideoWorkflow();

        for (TikTokAccount tikTokAccount : allWithVideoWorkflow) {
            if (isReadyForVideoUpload(tikTokAccount)) {
                try {
                    log.info("Started uploading video for {}.", tikTokAccount.getEmail());

                    ActionCommand actionCommand = actionActionFactory.getActionCommand(platform, action);
                    actionCommand.executeAction(tikTokAccount.getId(), action, VideoActionRequest.builder().videoId(tikTokAccount.getWorkflow().getVideoSetting().getVideoId()).platform(platform).build());
                } finally {
                    tikTokCommandPort.clearWorkflow(tikTokAccount.getId());
                }
            }
        }
    }

    private boolean isReadyForVideoUpload(TikTokAccount account) {
        Workflow.VideoSetting videoSetting = account.getWorkflow().getVideoSetting();
        return Math.abs(Duration.between(LocalDateTime.now(), videoSetting.getUploadAt()).toMinutes()) <= 2;
    }

}
