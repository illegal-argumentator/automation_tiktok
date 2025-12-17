package com.yves_gendron.automation_tiktok.common.factory;

import com.yves_gendron.automation_tiktok.common.command.ActionCommand;
import com.yves_gendron.automation_tiktok.common.type.Action;
import com.yves_gendron.automation_tiktok.common.type.Platform;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ActionActionFactory {

    private final Map<Platform, Map<Action, ActionCommand>> actionCommandsByPlatform;

    public ActionActionFactory(Set<ActionCommand> actionCommands) {
        this.actionCommandsByPlatform = actionCommands.stream()
                .collect(Collectors.groupingBy(
                        ActionCommand::getPlatform,
                        Collectors.toMap(ActionCommand::getAction, h -> h)
                ));
    }

    public ActionCommand getActionCommand(Platform platform, Action action) {
        return Optional.ofNullable(actionCommandsByPlatform.get(platform))
                .map(m -> m.get(action))
                .orElseThrow(() ->
                        new IllegalStateException("No action commands exists by platform: " + platform + " and action: " + action));
    }
}
