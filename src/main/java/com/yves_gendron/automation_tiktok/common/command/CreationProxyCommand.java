package com.yves_gendron.automation_tiktok.common.command;

import com.yves_gendron.automation_tiktok.common.type.Platform;
import com.yves_gendron.automation_tiktok.system.controller.dto.CreateAccountsRequest;

public interface CreationProxyCommand {

    void executeAvailableProxies(CreateAccountsRequest createAccountsRequest);

    Platform getPlatform();
}
