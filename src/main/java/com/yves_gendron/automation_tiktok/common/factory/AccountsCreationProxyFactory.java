package com.yves_gendron.automation_tiktok.common.factory;

import com.yves_gendron.automation_tiktok.common.command.CreationProxyCommand;
import com.yves_gendron.automation_tiktok.common.type.Platform;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccountsCreationProxyFactory implements CommandFactory<CreationProxyCommand> {

    private final Map<Platform, CreationProxyCommand> accountCreationProxyCommandsByPlatform;

    public AccountsCreationProxyFactory(Set<CreationProxyCommand> accountCreationProxyCommands) {
        this.accountCreationProxyCommandsByPlatform = accountCreationProxyCommands.stream()
                .collect(Collectors.toMap(CreationProxyCommand::getPlatform, Function.identity()));
    }

    @Override
    public CreationProxyCommand getCommandByAction(Platform platform) {
        return Optional.ofNullable(accountCreationProxyCommandsByPlatform.get(platform))
                .orElseThrow(() -> new IllegalStateException("No account proxy commands exists by platform: " + platform));
    }
}
