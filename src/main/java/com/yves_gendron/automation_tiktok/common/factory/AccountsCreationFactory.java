package com.yves_gendron.automation_tiktok.common.factory;

import com.yves_gendron.automation_tiktok.common.command.CreationCommand;
import com.yves_gendron.automation_tiktok.common.type.Platform;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccountsCreationFactory implements CommandFactory<CreationCommand> {

    private final Map<Platform, CreationCommand> accountCommandsByPlatform;

    public AccountsCreationFactory(Set<CreationCommand> creationCommands) {
        this.accountCommandsByPlatform = creationCommands.stream()
                .collect(Collectors.toMap(CreationCommand::getPlatform, Function.identity()));
    }

    @Override
    public CreationCommand getCommandByAction(Platform platform) {
        return Optional.ofNullable(accountCommandsByPlatform.get(platform))
                .orElseThrow(() -> new IllegalStateException("No account commands exists by platform: " + platform));
    }
}
