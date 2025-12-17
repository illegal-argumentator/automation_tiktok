package com.yves_gendron.automation_tiktok.system.controller;

import com.yves_gendron.automation_tiktok.common.type.Action;
import com.yves_gendron.automation_tiktok.system.controller.dto.*;
import com.yves_gendron.automation_tiktok.system.service.social.SocialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/social")
@RequiredArgsConstructor
public class SocialController {

    private final SocialService socialService;

    @PostMapping("/create-accounts")
    public void createAccounts(@RequestBody @Valid CreateAccountsRequest createAccountsRequest) {
        socialService.processAccountsCreation(createAccountsRequest);
    }

    @PutMapping("/action/avatar")
    public void processAvatarAction(
            @RequestParam String accountId,
            @Valid @RequestBody AvatarActionRequest avatarActionRequest
    ) {
        socialService.processAction(accountId, Action.AVATAR, avatarActionRequest);
    }

    @PutMapping("/action/like")
    public void processLikeAction(@RequestParam String accountId, @RequestBody @Valid LikeActionRequest likeActionRequest) {
        socialService.processAction(accountId, Action.LIKE, likeActionRequest);
    }

    @PutMapping("/action/comment")
    public void processCommentAction(@RequestParam String accountId, @RequestBody @Valid CommentActionRequest commentActionRequest) {
        socialService.processAction(accountId, Action.COMMENT, commentActionRequest);
    }

    @PutMapping("/action/video")
    public void processVideoAction(@RequestParam String accountId, @RequestBody @Valid VideoActionRequest videoActionRequest) {
        socialService.processAction(accountId, Action.VIDEO, videoActionRequest);
    }
}
