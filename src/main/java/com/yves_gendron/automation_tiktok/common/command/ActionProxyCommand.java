package com.yves_gendron.automation_tiktok.common.command;

import com.yves_gendron.automation_tiktok.common.type.Platform;

public interface ActionProxyCommand {

    void executeActiveProxy(String accountId);

    Platform getPlatform();

}
