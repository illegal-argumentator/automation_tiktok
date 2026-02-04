package com.yves_gendron.automation_tiktok.domain.tiktok.service;

import com.yves_gendron.automation_tiktok.common.dto.embedded.Bio;
import com.yves_gendron.automation_tiktok.common.dto.embedded.Geolocation;
import com.yves_gendron.automation_tiktok.common.type.Status;
import com.yves_gendron.automation_tiktok.config.AppProps;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;
import com.yves_gendron.automation_tiktok.system.client.randomuser.RandomUserClient;
import com.yves_gendron.automation_tiktok.system.client.randomuser.common.dto.RandomUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TikTokAccountFactory {

    private final RandomUserClient randomUserClient;

    private final AppProps appProps;

    @Value("${app.mail.host}")
    private String APP_MAIL_HOST;

    public TikTokAccount buildRandomTikTokAccount() {
        RandomUserResponse.RandomResult randomUser = randomUserClient.getRandomUser();

        String username = randomUser.getEmail().substring(0, randomUser.getEmail().indexOf('@'));
        String address = username + String.valueOf(UUID.randomUUID()).substring(0, 4) + "@" + APP_MAIL_HOST;
        Bio bio = Bio.builder()
                .name(randomUser.getName())
                .dob(randomUser.getDob())
                .geolocation(Geolocation.builder().build())
                .build();

        return TikTokAccount.builder()
                .email(address)
                .password(appProps.getAccountsPassword())
                .avatarLink(randomUser.getPicture().getLarge())
                .bio(bio)
                .status(Status.IN_PROGRESS)
                .build();
    }
}
