package com.yves_gendron.automation_tiktok.domain.tiktok.service.creation;

import com.microsoft.playwright.PlaywrightException;
import com.yves_gendron.automation_tiktok.common.command.CreationCommand;
import com.yves_gendron.automation_tiktok.common.type.Platform;
import com.yves_gendron.automation_tiktok.common.type.Status;
import com.yves_gendron.automation_tiktok.config.AppProps;
import com.yves_gendron.automation_tiktok.domain.proxy.model.Proxy;
import com.yves_gendron.automation_tiktok.domain.proxy.service.ProxyService;
import com.yves_gendron.automation_tiktok.domain.proxy.web.dto.ProxyFilterRequest;
import com.yves_gendron.automation_tiktok.domain.proxy.web.dto.UpdateProxyRequest;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.TikTokCreationException;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.helper.TikTokCreationPlaywrightHelper;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;
import com.yves_gendron.automation_tiktok.domain.tiktok.service.TikTokAccountFactory;
import com.yves_gendron.automation_tiktok.domain.tiktok.service.TikTokService;
import com.yves_gendron.automation_tiktok.domain.tiktok.web.dto.UpdateAccountRequest;
import com.yves_gendron.automation_tiktok.system.client.nst.NstBrowserClient;
import com.yves_gendron.automation_tiktok.system.client.nst.common.dto.CreateProfileResponse;
import com.yves_gendron.automation_tiktok.system.client.nst.common.exception.NstBrowserException;
import com.yves_gendron.automation_tiktok.system.controller.dto.CreateAccountsRequest;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.PlaywrightInitializer;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.dto.PlaywrightDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.yves_gendron.automation_tiktok.domain.tiktok.common.constants.TikTokConstants.TIKTOK_BASE_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class TikTokCreationCommand implements CreationCommand {

    private final NstBrowserClient nstBrowserClient;

    private final TikTokService tikTokService;

    private final PlaywrightInitializer playwrightInitializer;

    private final TikTokAccountFactory tikTokAccountFactory;

    private final TikTokCreationPlaywrightHelper tikTokCreationPlaywrightHelper;

    private final ProxyService proxyService;

    private final AppProps appProps;

    @Override
    public void executeAccountsCreation(CreateAccountsRequest createAccountsRequest) {
        for (int i = 0; i < createAccountsRequest.getAmount(); i++) {
            List<Proxy> proxies = proxyService.findAllWithFilter(ProxyFilterRequest.builder()
                    .accountsLinkedLessThan(appProps.getAccountsPerProxy())
                    .verified(true)
                    .build());

            List<TikTokAccount> tikTokAccounts = new ArrayList<>();
            try {
                tikTokAccounts.add(tikTokAccountFactory.buildRandomTikTokAccount());
                tikTokAccounts = tikTokService.saveAllOrThrow(tikTokAccounts);
                processAccountsCreation(proxies, tikTokAccounts, createAccountsRequest.getAmount());
            } catch (Exception e) {
                throw new TikTokCreationException(e.getMessage());
            }
        }
    }

    private void processAccountsCreation(List<Proxy> proxies, List<TikTokAccount> tikTokAccounts, int createAccountsLimit) {
        int createdCount = 0;

        for (Proxy proxy : proxies) {
            while (canCreateMoreAccounts(proxy.getAccountsLinked(), createdCount, createAccountsLimit)) {
                TikTokAccount tikTokAccount = tikTokAccounts.get(createdCount);
                PlaywrightDto playwrightDto = PlaywrightDto.builder().autoCloseables(List.of()).build();

                try {
                    CreateProfileResponse createProfileResponse = nstBrowserClient.createProfile(buildProfileName(tikTokAccount), proxy);

                    playwrightDto = playwrightInitializer.initBrowser(createProfileResponse.getData().getProfileId());
                    initAccountWithStarterFields(createProfileResponse.getData().getProfileId(), tikTokAccount, proxy);

                    tikTokCreationPlaywrightHelper.processSignUp(playwrightDto, tikTokAccount);
                    finishAccountCreation(tikTokAccount);
                } catch (PlaywrightException e) {
                    log.error(e.getMessage());
                    throw new TikTokCreationException(tikTokAccount, "Unstable proxy connection, couldn't access the element");
                } catch (NstBrowserException e) {
                    log.error(e.getMessage());
                    throw new TikTokCreationException(tikTokAccount, "Nst Browser exception occurred. Probably plan exceeded");
                } finally {
                    finishProcessing(playwrightDto.getAutoCloseables());
                }
                createdCount++;
            }
        }
    }

    private void initAccountWithStarterFields(String nstProfileId, TikTokAccount tikTokAccount, Proxy proxy) {
        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .username(tikTokService.generateUsername(tikTokAccount.getEmail()))
                .nstProfileId(nstProfileId)
                .countryCode(proxy.getGeolocation().getCountryCode())
                .proxy(proxy)
                .build();

        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);
    }


    private void finishAccountCreation(TikTokAccount tikTokAccount) {
        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .status(Status.CREATED)
                .accountLink(TIKTOK_BASE_URL + "/@" + tikTokAccount.getUsername())
                .executionMessage(null)
                .build();

        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);

        Proxy proxy = tikTokAccount.getProxy();
        proxyService.update(proxy.getId(), UpdateProxyRequest.builder().accountsLinked(proxy.getAccountsLinked() + 1).build());
    }

    private void finishProcessing(List<AutoCloseable> autoCloseables) {
        autoCloseables.forEach(ac -> {
            try {
                ac.close();
            } catch (Exception e) {
                log.error("Failed to close resource", e);
            }
        });
    }

    private String buildProfileName(TikTokAccount tikTokAccount) {
        return tikTokAccount.getBio().getName().getFirst() + " " + tikTokAccount.getBio().getName().getLast();
    }

    private boolean canCreateMoreAccounts(int accountsLinked, int createdCount, int createAccountsLimit) {
        boolean underProxyLimit = accountsLinked < appProps.getAccountsPerProxy();
        boolean underCreationLimit = createdCount < createAccountsLimit;
        return underProxyLimit && underCreationLimit;
    }

    @Override
    public Platform getPlatform() {
        return Platform.TIKTOK;
    }
}