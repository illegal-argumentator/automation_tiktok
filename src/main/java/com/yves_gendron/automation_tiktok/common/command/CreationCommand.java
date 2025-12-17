package com.yves_gendron.automation_tiktok.common.command;

import com.yves_gendron.automation_tiktok.common.type.Platform;
import com.yves_gendron.automation_tiktok.system.controller.dto.CreateAccountsRequest;

public interface CreationCommand {

    void executeAccountsCreation(CreateAccountsRequest createAccountsRequest);

    Platform getPlatform();

}
