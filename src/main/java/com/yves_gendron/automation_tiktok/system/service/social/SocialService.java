package com.yves_gendron.automation_tiktok.system.service.social;

import com.yves_gendron.automation_tiktok.common.command.ActionCommand;
import com.yves_gendron.automation_tiktok.common.command.ActionProxyCommand;
import com.yves_gendron.automation_tiktok.common.command.CreationCommand;
import com.yves_gendron.automation_tiktok.common.command.CreationProxyCommand;
import com.yves_gendron.automation_tiktok.common.factory.AccountsCreationFactory;
import com.yves_gendron.automation_tiktok.common.factory.AccountsCreationProxyFactory;
import com.yves_gendron.automation_tiktok.common.factory.ActionActionFactory;
import com.yves_gendron.automation_tiktok.common.factory.ProxyFactory;
import com.yves_gendron.automation_tiktok.common.type.Action;
import com.yves_gendron.automation_tiktok.system.controller.dto.ActionRequest;
import com.yves_gendron.automation_tiktok.system.controller.dto.CreateAccountsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final AccountsCreationFactory accountsCreationFactory;

    private final ActionActionFactory actionActionFactory;

    private final ProxyFactory proxyFactory;

    private final AccountsCreationProxyFactory accountsCreationProxyFactory;

    public void processAccountsCreation(CreateAccountsRequest createAccountsRequest) {
        CreationProxyCommand accountCreationProxyCommand = accountsCreationProxyFactory.getCommandByAction(createAccountsRequest.getPlatform());
        accountCreationProxyCommand.executeAvailableProxies(createAccountsRequest);

        CreationCommand creationCommand = accountsCreationFactory.getCommandByAction(createAccountsRequest.getPlatform());
        creationCommand.executeAccountsCreation(createAccountsRequest);
    }

    public void processAction(String accountId, Action action, ActionRequest actionRequest) {
        ActionProxyCommand actionProxyCommand = proxyFactory.getCommandByAction(actionRequest.getPlatform());
        actionProxyCommand.executeActiveProxy(accountId);

        ActionCommand actionCommand = actionActionFactory.getActionCommand(actionRequest.getPlatform(), action);
        actionCommand.executeAction(accountId, action, actionRequest);
    };
}
