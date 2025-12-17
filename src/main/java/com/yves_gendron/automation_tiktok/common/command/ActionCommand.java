package com.yves_gendron.automation_tiktok.common.command;

import com.yves_gendron.automation_tiktok.common.type.Action;
import com.yves_gendron.automation_tiktok.common.type.Platform;
import com.yves_gendron.automation_tiktok.system.controller.dto.ActionRequest;

public interface ActionCommand {

    void executeAction(String accountId, Action action, ActionRequest actionRequest);

    Platform getPlatform();

    Action getAction();

}
