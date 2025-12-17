package com.yves_gendron.automation_tiktok.common.factory;

import com.yves_gendron.automation_tiktok.common.command.ActionProxyCommand;
import com.yves_gendron.automation_tiktok.common.type.Platform;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProxyFactory implements CommandFactory<ActionProxyCommand> {

    private final Map<Platform, ActionProxyCommand> proxyCommandsByPlatform;

    public ProxyFactory(Set<ActionProxyCommand> actionProxyCommands) {
        this.proxyCommandsByPlatform = actionProxyCommands.stream()
                .collect(Collectors.toMap(ActionProxyCommand::getPlatform, Function.identity()));
    }

    @Override
    public ActionProxyCommand getCommandByAction(Platform platform) {
        return Optional.ofNullable(proxyCommandsByPlatform.get(platform))
                .orElseThrow(() -> new IllegalStateException("No proxy commands exists by platform: " + platform));
    }

}
